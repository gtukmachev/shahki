package tga.js.shahski.kt

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlin.browser.document

/**
 * Created by grigory@clearscale.net on 7/14/2018.
 */

fun main(args: Array<String>) {
    createHtmlField()


    var field = Field()

    drawField(field)

    var counter = 0;
    document.getElementById("btn")!!.addEventListener("click",{
        counter++
             if (counter == 1) step(field, "3b", "4c")
        else if (counter == 2) step(field, "6c", "5b")
        else if (counter == 3) step(field, "4e", "5d")
        else if (counter == 4) step(field, "a1", "b2")

        drawField(field)

    })

}

private fun step(from: String, to: String) {
    try {
        move(from, to)
        logStep(from, to)
    } catch (e: Field.WrongStep) {
        logStep(from, to, e)
    }
}

private fun logStep(from: String, to: String, e: Field.WrongStep? = null) {
    val li = document.create.li{
        +"$from -> $to ${e?.message ?: ""}"
    }

    document.getElementById("log")!!.append(li)
}



fun createHtmlField() {
    val root = document.getElementById("root") ?: throw NullPointerException("Cannot find root element in html")

    val myDiv = document.create.table {
        classes += "simple"

        for (l in 1..FIELD_SIZE+1) {
            tr{
                if (l == 1) classes += "head"

                for(c in 1..FIELD_SIZE+1) {
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

/*
                                if ((l+c)%2 == 1) {
                                    if (l < 5) classes += "black"
                                    if (l > 6) classes += "white"
                                }
*/
                        }
                    }
                }
            }
        }
    }
    root.append(myDiv)

}



fun drawField(field: Field) {
    for(l in 0 until FIELD_SIZE) for (c in 0 until FIELD_SIZE) {
        val cell = document.getElementById("c-$l-$c") ?: throw RuntimeException("no cell with id='c-$l-$c' found!")
        when (field.state[l][c]) {
            Field.EPMTY -> { cell.classList.remove("white"); cell.classList.remove("black") }
            Field.BLACK -> { cell.classList.remove("white"); cell.classList.   add("black") }
            Field.WHITE -> { cell.classList.   add("white"); cell.classList.remove("black") }
        }
    }

}
