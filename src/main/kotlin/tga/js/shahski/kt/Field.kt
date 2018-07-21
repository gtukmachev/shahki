package tga.js.shahski.kt

import kotlin.math.abs

/**
 * Created by grigory@clearscale.net on 7/21/2018.
 */


data class Field(
        private val state: Array<Int>
) {

    companion object {
        const val FIELD_SIZE = 8
        const val EMPTY = 0
        const val BLACK = 1
        const val WHITE = 2
    }

    constructor() : this( Array(FIELD_SIZE*FIELD_SIZE){0}) {

        // initial figures position:
        for(l in 0 until FIELD_SIZE)
            for (c in 0 until FIELD_SIZE)
                state[l* FIELD_SIZE + c] =
                        if ((l+c)%2 == 1 ) when (l) {
                            0,1,2 -> WHITE
                            7,6,5 -> BLACK
                            else -> EMPTY
                        }
                        else 0
    }

    fun get(l: Int, c: Int) = state[l * FIELD_SIZE + c]

    fun move(color: Int, fromPosition: Pair<Int,Int>, toPosition: Pair<Int,Int>):Field {
        val (l,c) = fromPosition

        if ( (l+c)%2 != 1 ) throw WrongStep("The source position ($fromPosition) should be dark!")

        if (get(l,c) != color) throw WrongStep("The source position ($fromPosition) should contains your stone!")

        val color = this.get(l,c)
        if (color == 0) throw WrongStep("No chess on the source place: $fromPosition!")

        val (lTo,cTo) = toPosition
        if ( (lTo+cTo)%2 != 1 ) throw WrongStep("The target position ($fromPosition) should be dark!")
        val colorTo = this.get(lTo,cTo)
        if (colorTo != 0) throw WrongStep("The target place is not empty: $toPosition!");

        if ( abs(l-lTo) != 1 || abs(c-cTo) != 1 ) throw WrongStep("The step [$fromPosition -> $toPosition] is too long!");

        val newState = state.copyOf()

        newState[l * FIELD_SIZE + c] = 0
        newState[lTo * FIELD_SIZE + cTo] = color

        return this.copy(state = newState)

    }

    //private fun decodeCoordinate(coordinate: String) = (coordinate[0] - '1') to (coordinate[1] - 'a')

}