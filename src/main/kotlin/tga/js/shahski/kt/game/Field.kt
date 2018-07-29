package tga.js.shahski.kt.game

import tga.js.shahski.kt.WrongStep
import kotlin.math.abs

data class Field(
        private val state: Array<Int>
) {

    companion object {
        const val FIELD_SIZE = 8
        const val EMPTY = 0b0000
        const val BLACK = 0b0010
        const val WHITE = 0b0100
        const val QUINN = 0b0001
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

    fun getColor(l: Int, c: Int) = (state[l * FIELD_SIZE + c] shr 1) shl 1

    private fun Array<Int>.set(p: Pair<Int,Int>, value: Int) { this[p.first * FIELD_SIZE + p.second] = value }

    private          fun Pair<Int,Int>.isOnField() = first >= 0 && second >= 0 && first < FIELD_SIZE && second < FIELD_SIZE
    private          fun Pair<Int,Int>.isDarkCell() = (first + second) % 2 == 1

    private          fun Pair<Int,Int>.stone(st: Array<Int>) = st[first * FIELD_SIZE + second]
    private          fun Pair<Int,Int>.stone() = this.stone(state)

    private          fun Pair<Int,Int>.color(st: Array<Int>) = (st[first * FIELD_SIZE + second] shr 1) shl 1
    private          fun Pair<Int,Int>.color() = this.color(state)

    private    infix fun Pair<Int,Int>.isOnDiagonal(that: Pair<Int,Int>) = abs(this.first - that.first) == abs(this.second - that.second)
    private    infix fun Pair<Int,Int>.isNotOnDiagonal(that: Pair<Int,Int>) = !(this isOnDiagonal that)
    private    infix fun Pair<Int,Int>.distanceTo(that: Pair<Int,Int>) = this.distance(that)
    private          fun Pair<Int,Int>.distance(that: Pair<Int,Int>) = abs(that.first - this.first)
    private          fun Pair<Int,Int>.linesDelta(that: Pair<Int,Int>) = that.first - this.first
    private operator fun Pair<Int,Int>.plus(that: Pair<Int,Int>) = (that.first + this.first) to (that.second + this.second)
    private operator fun Pair<Int,Int>.minus(that: Pair<Int,Int>) = (this.first - that.first) to (this.second - that.second)
    private operator fun Pair<Int,Int>.div(n :Int) = (this.first / n) to (this.second / n)
    fun Pair<Int,Int>.en()="[${'1'+first}${'a'+second}]"

    private fun checkStartPosition(color: Int, p: Pair<Int, Int>) {
        if (!p.isOnField()) throw WrongStep(0, "The start position ${p.en()} should be INSIDE the field!")
        if (!p.isDarkCell()) throw WrongStep(0, "The start position ${p.en()} should be dark!")
        if (p.color() != color) throw WrongStep(0, "The start position ${p.en()} should contains your stone!")
    }

    fun move(color: Int, moves: Moves): Field {
        if(moves.size < 2) throw WrongStep(0, "moves chain should contains at least 2 positions: start and finish.")

        checkStartPosition(color, moves[0])

        return when( detectStepType(moves) ) {
            MoveType.MOVE  -> doOneMove(color, moves)
            MoveType.SHOT  -> doShots(color, moves)
            MoveType.QUINN -> doQuinnMoves(color, moves)
                     else -> throw WrongStep(1, "Unacceptable move to 1:${moves[1].en()} - the move type unrecognized")
        }

    }

    /**
     * The function handles a simple step for a simple stone (not a quinn)
     */
    private fun doOneMove(color: Int, moves: Moves): Field {

        if (moves.size != 2)                     throw WrongStep(2, "A simple step should contains only one move!")
        if (!moves[1].isOnField())               throw WrongStep(1, "The start position (${moves[1].en()}) should be INSIDE the field!")
        if ( moves[0] isNotOnDiagonal moves[1] ) throw WrongStep(1, "Target cell (${moves[1].en()}) not on a diagonal")
        if ( moves[0].distance(moves[1]) != 1 )  throw WrongStep(1, "Target cell (${moves[1].en()}) should be near the start position (${moves[0].en()})")
        if ( moves[1].stone() != EMPTY )         throw WrongStep(1, "Target cell (${moves[1].en()}) is not empty")

        val requiredLinesDelta = when(color){
            WHITE ->  1
            BLACK -> -1
             else -> throw WrongStep(1, "Unrecognized stone")
        }

        if ( moves[0].linesDelta(moves[1]) != requiredLinesDelta ) throw WrongStep(1, "The moving should be in forward direction only")

        return this.copy(state = state.copyOf().apply {
            set(moves[0], 0)
            set(moves[1], color)
        })

    }

    /**
     * The function handles a shot-step (or chain of 'shots') by a simple stone (not a quinn)
     */
    private fun doShots(color: Int, moves: Moves): Field {

        val mutableState = state.copyOf()

        fun doOneShot(i: Int) {
            if (!moves[i].isOnField())               throw WrongStep(i, "The target position ${moves[i].en()} should be INSIDE the field!")
            if ( moves[i-1] isNotOnDiagonal moves[i] ) throw WrongStep(i, "Target cell ${moves[i].en()} not on a diagonal")
            if ( moves[i-1].distance(moves[i]) != 2 )  throw WrongStep(i, "To make a shot - target cell ${moves[i].en()} should be on distance in 2 diagonal cells from the last one ${moves[i-1]}")
            if ( moves[i].stone(mutableState) != EMPTY ) throw WrongStep(i, "Target cell ${moves[i].en()} is not empty")

            val between = (moves[i] + moves[i-1]) / 2
            val enemyColor = if (color == WHITE) BLACK else WHITE

            if ( between.color(mutableState) != enemyColor ) throw WrongStep(i, "to make the shot ${moves[i-1]} -> ${moves[i].en()} it should be an enemy stone ib the position $between")

            mutableState.set(moves[i-1], EMPTY)
            mutableState.set(between, EMPTY)
            mutableState.set(moves[i], color)

        }

        for (i in 1 until moves.size) { doOneShot(i) }

        return this.copy(state = mutableState)
    }

    /**
     * a Quinn moves handler
     */
    private fun doQuinnMoves(ownColor: Int, moves: Moves): Field {

        val mutableState = state.copyOf()
        val enemyColor = if (ownColor == WHITE) BLACK else WHITE

        fun doOneQuinnMove(i: Int) {
            if (!moves[i-1].isOnField())                 throw WrongStep(i, "The start position ${moves[i-1].en()} should be INSIDE the field!")
            if (!moves[i].isOnField())                   throw WrongStep(i, "The target position ${moves[i].en()} should be INSIDE the field!")
            if ( moves[i-1] isNotOnDiagonal moves[i] )   throw WrongStep(i, "Target cell ${moves[i].en()} not on a diagonal to start ${moves[i-1].en()} cell!")
            if ( moves[i].stone(mutableState) != EMPTY ) throw WrongStep(i, "Target cell ${moves[i].en()} is not empty")

            val d = abs(moves[i] distanceTo moves[i-1])
            val direction = (moves[i] - moves[i-1]) / d

            var p = moves[i-1] + direction
            var enemyPosition: Pair<Int, Int>? = null

            while (p != moves[i]) {
                when ( p.color(mutableState) ){
                    enemyColor -> enemyPosition = when (enemyPosition) {
                                                    null -> p
                                                    else -> throw WrongStep(i, "A quinn can jump over a single enemy stone per a singe move, but you trying to jump over the second enemy stone: ${p.en()}!")
                                                }
                      ownColor -> throw WrongStep(i, "A quinn cannot jump over own stone ${p.en()}!")
                }
                p += direction
            }

            val myStone = moves[i-1].stone(mutableState)
            mutableState.set(moves[i-1], EMPTY)
            enemyPosition?.let{
                mutableState.set(it, EMPTY)
            }
            mutableState.set(moves[i], myStone)

        }

        for (i in 1 until moves.size) { doOneQuinnMove(i) }

        return this.copy(state = mutableState)
    }


    /**
     * The function - is aa detector of step type
     */
    private fun detectStepType(moves: Moves): MoveType {
        if (moves.size < 2) return MoveType.UNKNOWN
        if (moves[0] isNotOnDiagonal moves[1]) throw WrongStep(1, "you can move your stone ${moves[0].en()} only in a diagonal direction, but not in the position ${moves[1].en()}")


        return when(moves[0].stone() and QUINN){
            QUINN -> MoveType.QUINN
             else ->
                     when(moves[0] distanceTo moves[1]){
                            1 -> MoveType.MOVE
                            2 -> MoveType.SHOT
                         else -> MoveType.UNKNOWN
                     }
        }

    }

}

/**
 *
 * first = line
 * second = column
 *
 */
typealias Moves = List<Pair<Int, Int>>

enum class MoveType{ MOVE, SHOT, QUINN, UNKNOWN}