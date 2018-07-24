package tga.js.shahski.kt

import kotlinx.html.*
import kotlinx.html.dom.create
import org.w3c.dom.HTMLInputElement
import tga.js.shahski.kt.bots.MoviesHistoryItem
import tga.js.shahski.kt.game.Field
import tga.js.shahski.kt.game.Game
import tga.js.shahski.kt.game.Moves
import kotlin.browser.document

/**
 * Created by grigory@clearscale.net on 7/14/2018.
 */

val theGame = Game{ moviesHistoryItem -> writeStepToList(moviesHistoryItem) }

fun main(args: Array<String>) {
    createHtmlField()
    drawGameFieldState()

    val lin1Input = "lin1".let{ document.getElementById(it) ?: throw NullPointerException("no '$it' element found") } as HTMLInputElement
    val col1Input = "col1".let{ document.getElementById(it) ?: throw NullPointerException("no '$it' element found") } as HTMLInputElement
    val lin2Input = "lin2".let{ document.getElementById(it) ?: throw NullPointerException("no '$it' element found") } as HTMLInputElement
    val col2Input = "col2".let{ document.getElementById(it) ?: throw NullPointerException("no '$it' element found") } as HTMLInputElement


    val btn = document.getElementById("btn") ?: throw NullPointerException("no 'btn' element found")
    btn.addEventListener("click", {
        theGame.force( listOf(
                lin1Input.value.toInt() to col1Input.value.toInt(),
                lin2Input.value.toInt() to col2Input.value.toInt()
        ))
        theGame.turn()
        drawGameFieldState()
    })

}

private fun writeStepToList(hi: MoviesHistoryItem) {

    val err = hi.result.apply { " :: " + this } ?: ""
    val mv = hi.moves.joinToString(" -> "){ encodeStep(it) }
    val att = if (hi.nAttempt == 0) "" else "(${hi.nAttempt})"

    val li = document.create.li{
        +"${hi.nStep}. $att $mv $err"
    }

    val clr = when(hi.color){
        Field.WHITE -> "white"
        Field.BLACK -> "black"
        else        -> "?????"
    }

    document.getElementById("log-$clr")!!.append(li)
}

private fun encodeStep(step: Pair<Int,Int>) = ""+('1'+step.first)+('a'+step.second)

fun createHtmlField() {
    val root = document.getElementById("root") ?: throw NullPointerException("Cannot find root element in html")

    val myDiv = document.create.table {
        classes += "simple"

        for (l in 1..Field.FIELD_SIZE+1) {
            tr{
                if (l == 1) classes += "head"

                for(c in 1..Field.FIELD_SIZE+1) {
                    td{
                        if (c == 1 || l == 1) {
                            classes += "head"

                            if (c == 1 && l > 1) {
                                +"${l-1}"
                            } else if (l == 1 && c > 1) {
                                +when(c){
                                    2 -> "a"
                                    3 -> "b"
                                    4 -> "c"
                                    5 -> "d"
                                    6 -> "e"
                                    7 -> "f"
                                    8 -> "g"
                                    9 -> "h"
                                    else -> ""
                                }
                            }
                        } else {
                            id = "c-${l-2}-${c-2}"
                            classes += "field"
                            classes += when( (l+c)%2 ){
                                1 -> "dark"
                                else -> "light"
                            }

                        }
                    }
                }
            }
        }
    }
    root.append(myDiv)

}

fun drawGameFieldState() {
    for(l in 0 until Field.FIELD_SIZE) for (c in 0 until Field.FIELD_SIZE) {
        val cell = document.getElementById("c-$l-$c") ?: throw RuntimeException("no cell with id='c-$l-$c' found!")
        when (theGame.field.get(l,c)) {
            Field.EMPTY -> { cell.classList.remove("white"); cell.classList.remove("black") }
            Field.BLACK -> { cell.classList.remove("white"); cell.classList.   add("black") }
            Field.WHITE -> { cell.classList.   add("white"); cell.classList.remove("black") }
        }
    }
}
