package tga.shashki.core.bots

import tga.shashki.core.game.Field
import tga.shashki.core.game.Moves

/**
 * Created by grigory@clearscale.net on 2/17/2019.
 */
object Converters {

    /**
     * input format: "A1 B2 C3 ..." -
     * output - List of pairs (row, column) - coordinates on a game board
     */
    fun convertCommandToMoves(HumanReadableCoordinatesSiquence: String): Moves =
            HumanReadableCoordinatesSiquence
                    .split(" ")
                    .map{ toRow(it) to toCol(it) }

    /**
     * input format: "A1"
     * output - number of row [0..7]
     */
    fun toRow(humanReadablePosition: String) = checkRange(humanReadablePosition[1] - '1')

    /**
     * input format: "A1"
     * output - number of column [0..7]
     */
    fun toCol(humanReadablePosition: String) = checkRange( when(humanReadablePosition[0]){
        in 'a'..'h' -> humanReadablePosition[0] - 'a'
        in 'A'..'H' -> humanReadablePosition[0] - 'A'
        in '1'..'8' -> humanReadablePosition[0] - '1'
               else -> throw RuntimeException("unrecognized input format (can't recognize row)")
    })

    fun checkRange(p: Int): Int = when (p) {
        in 0..7 -> p
           else -> throw RuntimeException("Wrong coordinate! passed value should be inside the game board bounds - from 0 till 7, but passed value is: $p")
    }

    fun stoneToHumanReadableName(stone: Int) = when {
                               stone == Field.EMPTY -> "EMPTY place"
        (stone and Field.BLACK and Field.QUINN) > 0 -> "BLACK QUINN"
        (stone and Field.BLACK                ) > 0 -> "BLACK stone"
        (stone and Field.WHITE and Field.QUINN) > 0 -> "WHITE QUINN"
        (stone and Field.WHITE                ) > 0 -> "WHITE stone"
                                               else -> throw RuntimeException("Unrecognized stone type: $stone")
    }
}