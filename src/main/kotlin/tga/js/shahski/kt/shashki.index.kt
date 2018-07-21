package tga.js.shahski.kt

import kotlinx.html.*
import kotlinx.html.dom.create
import tga.js.shahski.kt.bots.RandomBot
import kotlin.browser.document

/**
 * Created by grigory@clearscale.net on 7/14/2018.
 */

var field = Field()

fun main(args: Array<String>) {
    createHtmlField()
    drawField()


    val blackBot: Bot = RandomBot().apply { color = Field.BLACK }
    val whiteBot:  Bot = RandomBot().apply { color = Field.WHITE }

    var steps: List<Step> = listOf()
    var history: List<Field> = listOf()

    var currentBot = whiteBot
    var nStep = 0
    var nAttempt = 0


    document.getElementById("btn")!!.addEventListener("click", {

        val theStepAttempt = currentBot.getStep(nStep, nAttempt, field, steps, history)

        val result = doStep(currentBot.color, theStepAttempt.first, theStepAttempt.second)

        if (result) {
            if (currentBot == blackBot) nStep++
            nAttempt = 0
            currentBot = if (currentBot == whiteBot) blackBot else whiteBot
            drawField()
        } else {
            nAttempt++

        }
    })

}

private fun doStep(color: Int, from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {

    fun logStep(e: WrongStep? = null) {
        val clr = when(color){
            Field.WHITE -> "WHITE"
            Field.BLACK -> "BLACK"
            else        -> "?????"
        }

        val li = document.create.li{
            +"$clr : $from -> $to ${e?.message ?: ""}"
        }

        document.getElementById("log")!!.append(li)
    }

    try {
        field = field.move(color, from, to)
        logStep()
        return true
    } catch (e: WrongStep) {
        logStep(e)
    }
    return false

}


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


fun drawField() {
    for(l in 0 until Field.FIELD_SIZE) for (c in 0 until Field.FIELD_SIZE) {
        val cell = document.getElementById("c-$l-$c") ?: throw RuntimeException("no cell with id='c-$l-$c' found!")
        when (field.get(l,c)) {
            Field.EMPTY -> { cell.classList.remove("white"); cell.classList.remove("black") }
            Field.BLACK -> { cell.classList.remove("white"); cell.classList.   add("black") }
            Field.WHITE -> { cell.classList.   add("white"); cell.classList.remove("black") }
        }
    }
}
