package tga.shashki.ui_app

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import tga.shashki.core.bots.MoviesHistoryItem
import tga.shashki.core.game.Field
import tga.shashki.core.game.Game
import tga.shashki.core.game.Moves
import kotlin.browser.document

/**
 * Created by grigory@clearscale.net on 7/14/2018.
 */

val theGame = Game(3) { moviesHistoryItem -> writeStepToList(moviesHistoryItem) }
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
                                    theGame.currentBot.force(m)
                                    theGame.turn()

                                } else {
                                    manualMoves += cell
                                }
                                println(manualMoves)

                                drawGameFieldState()
                            }
                            div {
                                id = "s-${l-2}-${c-2}"
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
        val stone = "s-$l-$c".let{ document.getElementById(it) ?: throw RuntimeException("no <div> with id='$it' found!") }

        stone.classList.remove("white")
        stone.classList.remove("black")
        stone.classList.remove("white-quinn")
        stone.classList.remove("black-quinn")

        when ( theGame.field.getStone(l,c) ) {
            Field.WHITE + Field.QUINN  -> { stone.classList.add("white-quinn") }
            Field.BLACK + Field.QUINN  -> { stone.classList.add("black-quinn") }
            Field.WHITE -> { stone.classList.add("white") }
            Field.BLACK -> { stone.classList.add("black") }
        }

        val cell = "c-$l-$c".let{ document.getElementById(it) ?: throw RuntimeException("no <td> with id='$it' found!") }
        if ( manualMoves.contains(l to c) ) {
            cell.classList.add("choosen")
        } else {
            cell.classList.remove("choosen")
        }
    }

}
