package tga.js.shahski.kt

import kotlinx.html.dom.create
import kotlinx.html.li
import kotlin.browser.document
import kotlin.math.abs

/**
 * Created by grigory@clearscale.net on 7/21/2018.
 */


data class Field(
        val state: Array<Array<Int>>
) {

    companion object {
        const val FIELD_SIZE = 8
        const val EPMTY = 0
        const val BLACK = 1
        const val WHITE = 2
    }

    constructor() : this( Array(FIELD_SIZE){ Array(FIELD_SIZE){0} }) {

        // initial figures position:
        for(l in 0 until FIELD_SIZE)
            for (c in 0 until FIELD_SIZE)
                state[l][c] =
                        if ((l+c)%2 == 1 ) when (l) {
                            0,1,2 -> BLACK
                            7,6,5 -> WHITE
                            else -> EPMTY
                        }
                        else 0
    }


    fun move(from: String, to: String) {
        val (l,c) = decodeCoordinate(from)

        if ( (l+c)%2 != 1 ) throw WrongStep("The source position ($from) should be dark!")

        val color = state[l][c]
        if (color == 0) throw WrongStep("No chess on the source place: $from!");

        val (lTo,cTo) = decodeCoordinate(to)
        if ( (lTo+cTo)%2 != 1 ) throw WrongStep("The target position ($from) should be dark!")
        val colorTo = state[lTo][cTo]
        if (colorTo != 0) throw WrongStep("The target place is not empty: $to!");

        if ( abs(l-lTo) != 1 || abs(c-cTo) != 1 ) throw WrongStep("The step [$from -> $to] is too long!");

        state[l][c] = 0
        state[lTo][cTo] = color

    }


    fun decodeCoordinate(coordinate: String): Pair<Int, Int> {
        return Pair(
                coordinate[0] - '1',
                coordinate[1] - 'a'
        )
    }

    class WrongStep(msg: String): RuntimeException(msg)

}