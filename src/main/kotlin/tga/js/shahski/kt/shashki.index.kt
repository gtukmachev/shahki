package tga.js.shahski.kt

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import tga.js.shahski.kt.bots.MoviesHistoryItem
import tga.js.shahski.kt.game.Field
import tga.js.shahski.kt.game.Game
import tga.js.shahski.kt.game.Moves
import kotlin.browser.document

/**
 * Created by grigory@clearscale.net on 7/14/2018.
 */

val theGame = Game{ moviesHistoryItem -> writeStepToList(moviesHistoryItem) }
var manualMoves: Moves = listOf()

fun main(args: Array<String>) {
    createHtmlField()
    drawGameFieldState()
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
                                + ("" + ('a' + c - 2))
                            }
                        } else {
                            id = "c-${l-2}-${c-2}"
                            classes += "field"
                            classes += when( (l+c)%2 ){
                                1 -> "dark"
                                else -> "light"
                            }
                            onClickFunction = {
                                val cell = (l-2) to (c-2)
                                if (manualMoves.isNotEmpty() && manualMoves.last() == cell ) {
                                    val m = manualMoves
                                    manualMoves = listOf()
                                    theGame.force(m)
                                    theGame.turn()

                                } else {
                                    manualMoves += cell
                                }
                                println(manualMoves)

                                drawGameFieldState()
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

        when (theGame.field.getColor(l,c)) {
            Field.EMPTY -> { cell.classList.remove("white"); cell.classList.remove("black") }
            Field.BLACK -> { cell.classList.remove("white"); cell.classList.   add("black") }
            Field.WHITE -> { cell.classList.   add("white"); cell.classList.remove("black") }
        }

        if ( manualMoves.contains(l to c) ) {
            cell.classList.add("choosen")
        } else {
            cell.classList.remove("choosen")
        }
    }

}
