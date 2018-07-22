package tga.js.shahski.kt.bots

import tga.js.shahski.kt.game.Field
import tga.js.shahski.kt.game.Moves

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 */
class RandomBot: Bot {

    private var colorVal: Int = -1

    override var color: Int
        get() = colorVal
        set(value) {colorVal = value}

    override fun getMoves(nStep: Int, nAttempt: Int, field: Field, stepsHistory: List<MoviesHistoryItem>,
                          fieldsHistory: List<Field>): Moves {

        return when(color) {
            Field.WHITE -> when(nStep){
                0 -> listOf((2 to 1), (3 to 2))
                else -> listOf((2 to 1), (3 to 2))
            }
            Field.BLACK -> when(nStep){
                0 -> listOf((5 to 2), (4 to 1))
                else -> listOf((5 to 2), (4 to 1))
            }
            else -> throw RuntimeException("Unknown color!")
        }

    }
}