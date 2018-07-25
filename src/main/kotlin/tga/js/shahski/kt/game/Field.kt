package tga.js.shahski.kt.game

import tga.js.shahski.kt.WrongStep
import kotlin.math.abs

data class Field(
        private val state: Array<Int>
) {

    companion object {
        const val FIELD_SIZE = 8
        const val EMPTY = 0
        const val BLACK = 1
        const val WHITE = 2
    }

    constructor() : this( Array(FIELD_SIZE * FIELD_SIZE){0}) {

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

    fun getColor(l: Int, c: Int) = state[l * FIELD_SIZE + c]

    private fun Array<Int>.set(p: Pair<Int,Int>, value: Int) { this[p.first * FIELD_SIZE + p.second] = value }

    private fun Pair<Int,Int>.isOnField() = first >= 0 && second >= 0 && first < FIELD_SIZE && second < FIELD_SIZE
    private fun Pair<Int,Int>.isDarkCell() = (first + second) % 2 == 1
    private fun Pair<Int,Int>.color(st: Array<Int>) = st[first * FIELD_SIZE + second]
    private fun Pair<Int,Int>.color() = this.color(state)
    private infix fun Pair<Int,Int>.isOnDiagonal(that: Pair<Int,Int>) = abs(this.first - that.first) == abs(this.second - that.second)
    private infix fun Pair<Int,Int>.isNotOnDiagonal(that: Pair<Int,Int>) = !(this isOnDiagonal that)
    private fun Pair<Int,Int>.distance(that: Pair<Int,Int>) = abs(that.first - this.first)
    private fun Pair<Int,Int>.linesDelta(that: Pair<Int,Int>) = that.first - this.first
    private operator fun Pair<Int,Int>.plus(that: Pair<Int,Int>) = (that.first + this.first) to (that.second + this.second)
    private operator fun Pair<Int,Int>.div(n :Int) = (this.first / n) to (this.second / n)


    private fun checkStartPosition(color: Int, p: Pair<Int, Int>) {
        if (!p.isOnField()) throw WrongStep(0, "The start position ($p) should be INSIDE the field!")
        if (!p.isDarkCell()) throw WrongStep(0, "The start position ($p) should be dark!")
        if (p.color() != color) throw WrongStep(0, "The source position ($p) should contains your stone!")
    }

    fun move(color: Int, moves: Moves): Field {
        if(moves.size < 2) throw WrongStep(0, "moves chain should contains at least 2 positions: start and finish.")

        checkStartPosition(color, moves[0])

        return when( detectStepType(moves[0], moves[1]) ) {
            MoveType.MOVE -> doOneMove(color, moves)
            MoveType.SHOT -> doShots(color, moves)
                     else -> throw WrongStep(1, "Unacceptable move to 1:${moves[1]} - the move type unrecognized")
        }

    }

    private fun doOneMove(color: Int, moves: Moves): Field {

        if (!moves[1].isOnField())               throw WrongStep(1, "The start position (${moves[1]}) should be INSIDE the field!")
        if ( moves[0] isNotOnDiagonal moves[1] ) throw WrongStep(1, "Target cell (${moves[1]}) not on a diagonal")
        if ( moves[0].distance(moves[1]) != 1 )  throw WrongStep(1, "Target cell (${moves[1]}) should be near the start position (${moves[0]})")
        if ( moves[1].color() != 0 ) throw WrongStep(1, "Target cell (${moves[1]}) is not empty")

        val requiredLinesDelta = when(color){
            WHITE ->  1
            BLACK -> -1
             else -> throw WrongStep(1, "Unrecognized color")
        }

        if ( moves[0].linesDelta(moves[1]) != requiredLinesDelta ) throw WrongStep(1, "The moving should be in forward direction only")

        return this.copy(state = state.copyOf().apply {
            set(moves[0], 0)
            set(moves[1], color)
        })


    }

    private fun doShots(color: Int, moves: Moves): Field {

        val mutableState = state.copyOf()

        fun doOneShot(i: Int) {
            if (!moves[i].isOnField())               throw WrongStep(i, "The start position ${moves[i]} should be INSIDE the field!")
            if ( moves[i-1] isNotOnDiagonal moves[i] ) throw WrongStep(i, "Target cell ${moves[i]} not on a diagonal")
            if ( moves[i-1].distance(moves[i]) != 2 )  throw WrongStep(i, "Target cell ${moves[i]} should be on distance in 2 diagonal cells from the last one ${moves[i-1]}")
            if ( moves[i].color(mutableState) != 0 ) throw WrongStep(i, "Target cell ${moves[i]} is not empty")

            val between = (moves[i] + moves[i-1]) / 2
            val enemyColor = if (color == WHITE) BLACK else WHITE
            if ( between.color(mutableState) != enemyColor ) throw WrongStep(i, "to make the shot ${moves[i-1]} -> ${moves[i]} it should be an enemy stone ib the position $between")

            mutableState.set(moves[i-1], EMPTY)
            mutableState.set(between, EMPTY)
            mutableState.set(moves[i], color)

        }

        for (i in 1 until moves.size) { doOneShot(i) }

        return this.copy(state = mutableState)
    }


    private fun detectStepType(from: Pair<Int, Int>, to: Pair<Int, Int>): MoveType {
        val (lf, cf) = from
        val (lt, ct) = to

        if ( abs(lt-lf) == 1 && abs(ct-cf) == 1 ) return MoveType.MOVE
        if ( abs(lt-lf) == 2 && abs(ct-cf) == 2 ) return MoveType.SHOT

        return MoveType.UNKNOWN
    }

}

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 *
 * first = line
 * second = column
 *
 */
typealias Moves = List<Pair<Int, Int>>

enum class MoveType{ MOVE, SHOT, UNKNOWN}