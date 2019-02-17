package tga.shashki.core.bots

import tga.shashki.core.game.Field
import tga.shashki.core.game.Moves

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 */
class DevBot: Bot {

    private var colorVal: Int = -1
    private var focedMoves: Moves? = null

    override var color: Int
        get() = colorVal
        set(value) {colorVal = value}


    override fun win(field: Field, stepsHistory: List<MoviesHistoryItem>, fieldsHistory: List<Field>) {
        println("$color: I'm win :-)))) !")
    }

    override fun loose(field: Field, stepsHistory: List<MoviesHistoryItem>, fieldsHistory: List<Field>) {
        println("$color: I'm loose ;-(")
    }

    /**
     * The function used for testing only
     *
     * @param moves After the function called - the bot HAVE TO return exactly this moves
     *              in the next invocation of the getMoves() function
     */
    fun force(command: String) {
        focedMoves = convertCommandToMoves(command)
    }

    fun convertCommandToMoves(command: String): Moves = command.split(" ").map {
        ( (it[1]-'1') to when (it[0]) {
            in 'a'..'h' -> it[0] - 'a'
            in 'A'..'H' -> it[0] - 'A'
            in '1'..'8' -> it[0] - '1'
            else -> throw RuntimeException("wrong input format")
        }
                )
    }

    override fun getMoves(nStep: Int, nAttempt: Int, field: Field, stepsHistory: List<MoviesHistoryItem>,
                          fieldsHistory: List<Field>): Moves {

        if (focedMoves != null) {
            val m = focedMoves!!
            focedMoves = null
            return m
        }

        return when(color) {
            Field.WHITE -> when(nStep){
                0 -> listOf((2 to 1), (3 to 2))
                1 -> listOf((1 to 2), (2 to 1))
                else -> listOf((2 to 1), (3 to 2))
            }
            Field.BLACK -> when(nStep){
                0 -> listOf((5 to 2), (4 to 1))
                1 -> listOf((6 to 3), (5 to 2))
                else -> listOf((5 to 2), (4 to 1))
            }
            else -> throw RuntimeException("Unknown color!")
        }

    }
}