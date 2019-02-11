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

        val aroundDirections = listOf((1 to 1), (1 to -1), (-1 to 1), (-1 to 1))
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
    fun getStone(l: Int, c: Int) = state[l * FIELD_SIZE + c]

    private fun Array<Int>.moveTo(pFrom: Pair<Int,Int>, pTo: Pair<Int,Int>) = this.apply {
        val v = pFrom.stone(this)
        this.set(pFrom, Field.EMPTY)
        this.set(pTo, v)
    }

    private fun Array<Int>.transformToQuinnIfAny(p: Pair<Int,Int>) = this.apply {
        if (p.first == 0 || p.first == (FIELD_SIZE-1)) {
            if (!p.isQuinn()) {
                val stone = p.stone(this)
                if (
                        (stone == WHITE && p.first == (Field.FIELD_SIZE-1))
                        ||
                        (stone == BLACK && p.first == 0)
                ) {
                    this.set(p, stone + QUINN )
                }

            }
        }
    }


    private fun Array<Int>.set(p: Pair<Int,Int>, value: Int) { this[p.first * FIELD_SIZE + p.second] = value }

    private          fun Pair<Int,Int>.isOnField() = first >= 0 && second >= 0 && first < FIELD_SIZE && second < FIELD_SIZE
    private          fun Pair<Int,Int>.isDarkCell() = (first + second) % 2 == 1

    private          fun Pair<Int,Int>.stone(st: Array<Int>) = st[first * FIELD_SIZE + second]
    private          fun Pair<Int,Int>.stone() = this.stone(state)
    private          fun Pair<Int,Int>.isQuinn() = (this.stone(state) and QUINN) > 0

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

    interface NewFieldResponse { val field: Field }
    open class MoveResponse
        data class  Done(override val field: Field)                  : MoveResponse(), NewFieldResponse
        data class   Win(override val field: Field)                  : MoveResponse(), NewFieldResponse
        data class Loose(override val field: Field, val reason: Any) : MoveResponse(), NewFieldResponse
        data class   Err(val exception: WrongStep)          : MoveResponse()

    fun move(color: Int, moves: Moves): MoveResponse {
        if(moves.size < 2) Err(WrongStep(0, "moves chain should contains at least 2 positions: start and finish."))

        if (noMovesAvailable(color)) return Loose(this, "no more moves available")

        try {

            checkStartPosition(color, moves[0])

            val newField = when( detectStepType(moves) ) {
                MoveType.MOVE  -> doOneMove(color, moves)
                MoveType.SHOT  -> doShots(color, moves)
                MoveType.QUINN -> doQuinnMoves(color, moves)
                else -> throw WrongStep(1, "Unacceptable move to 1:${moves[1].en()} - the move type unrecognized")
            }


            return when (newField.isNoMoreEnemy(color)){
                true -> Win(newField)
                false -> Done(newField)
            }

        } catch (e: WrongStep) {
            return Err(e)
        }
    }

    private fun enemyColor(color: Int) = if (color == WHITE) BLACK else WHITE

    fun noMovesAvailable(color: Int): Boolean {

        val direction = if (color == WHITE) 1 else -1
        val enemy = enemyColor(color)

        fun canMove(p: Pair<Int, Int>): Boolean {
            ifTheOneMoveError(p, p + (direction to 1 )) ?: return true
            ifTheOneMoveError(p, p + (direction to -1)) ?: return true

            aroundDirections.forEach {
                ifTheOneShotError(p, p + it, state, enemy) ?: return true
            }

            if (p.isQuinn()) {
                var result : Boolean? = null
                aroundDirections.forEach {
                    var nextP = p + it
                    var enemyCounter = 0
                    while ( nextP.isOnField() && result == null ) {
                        when (nextP.color()) {
                            EMPTY -> result = true
                            color -> result = false
                            enemy -> when(enemyCounter) {
                                   0 -> enemyCounter++
                                else -> result = false
                            }
                        }
                        nextP += it
                    }
                    result ?: return result!!
                }
            }

            return false
        }

        for (l in 0 until FIELD_SIZE) {
            for (c in 0 until FIELD_SIZE) {
                val p = l to c
                if (p.color() == color) {
                    if (canMove(p)) {
                        return false
                    }
                }
            }
        }
        return true
    }


    private fun isNoMoreEnemy(color: Int) = enemyColor(color).let {
        enemyColor -> !(state.any { it and enemyColor > 0 })
    }

    private fun ifTheOneMoveError(s: Pair<Int, Int>, d: Pair<Int, Int>): String? = when {
        !d.isOnField()      -> "The start position (${d.en()}) should be INSIDE the field!"
        d isNotOnDiagonal s -> "Target cell (${d.en()}) not on a diagonal"
        d distanceTo s != 1 -> "Target cell (${d.en()}) should be near the start position (${s.en()})"
        d.stone() != EMPTY  -> "Target cell (${d.en()}) is not empty"
        else -> null
    }

    /**
     * The function handles a simple step for a simple stone (not a quinn)
     */
    private fun doOneMove(color: Int, moves: Moves): Field {

        if (moves.size != 2)                     throw WrongStep(2, "A simple step should contains only one move!")

        ifTheOneMoveError( moves[0], moves[1] )?.let{
            throw WrongStep(1, it)
        }

        val requiredLinesDelta = when(color){
            WHITE ->  1
            BLACK -> -1
             else -> throw WrongStep(1, "Unrecognized stone")
        }

        if ( moves[0].linesDelta(moves[1]) != requiredLinesDelta ) throw WrongStep(1, "The moving should be in forward direction only")

        return this.copy(state = state.copyOf()
                .moveTo(moves[0], moves[1])
                .transformToQuinnIfAny(moves[1])
        )

    }

    private fun ifTheOneShotError(s: Pair<Int, Int>, d: Pair<Int, Int>, theState: Array<Int>, enemyColor: Int ): String? = when {
        d.isOnField()              -> "The target position ${d.en()} should be INSIDE the field!"
        d isNotOnDiagonal s        -> "Target cell ${d.en()} not on a diagonal of the source one ${s.en()}"
        s distanceTo d != 2        -> "To make a shot - target cell ${d.en()} should be on the distance in 2 diagonal cells from the last one ${s.en()}"
        d.stone(theState) != EMPTY -> "Target cell ${d.en()} is not empty"
        else -> ((d + s) / 2).let{ between -> when {
            between.color(theState) != enemyColor -> "to make the shot ${s.en()} -> ${d.en()} it should be an enemy stone ib the position ${between.en()}"
                                             else -> null
        }}
    }

    /**
     * The function handles a shot-step (or chain of 'shots') by a simple stone (not a quinn)
     */
    private fun doShots(color: Int, moves: Moves): Field {

        val mutableState = state.copyOf()
        val enemy = enemyColor(color)

        fun doOneShot(i: Int) {

            ifTheOneShotError(moves[i-1], moves[i], mutableState, enemy)?.let{
                throw WrongStep(i, it)
            }


            mutableState.moveTo(moves[i-1], moves[i])
            mutableState.transformToQuinnIfAny(moves[i])
            mutableState.set((moves[i-1] + moves[i]) / 2, EMPTY) // set 0 into position between [i-1] and [i]

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

        fun doOneQuinnMove(i: Int): Boolean {
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


            mutableState.moveTo(moves[i-1], moves[i])
            enemyPosition?.let{
                mutableState.set(it, EMPTY)
            }

            return enemyPosition != null

        }

        var canNextMove = true
        for (i in 1 until moves.size) {
            if (!canNextMove) {
                throw WrongStep(i, "you can move the quin only after a SHOT but not after a simple move!")
            }
            canNextMove = doOneQuinnMove(i)
        }

        return this.copy(state = mutableState)
    }


    /**
     * The function - is aa detector of step type
     */
    private fun detectStepType(moves: Moves): MoveType {
        if (moves.size < 2) return MoveType.UNKNOWN
        if (moves[0] isNotOnDiagonal moves[1]) throw WrongStep(1, "you can move your stone ${moves[0].en()} only in a diagonal direction, but not in the position ${moves[1].en()}")


        return when(moves[0].isQuinn()){
            true -> MoveType.QUINN
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