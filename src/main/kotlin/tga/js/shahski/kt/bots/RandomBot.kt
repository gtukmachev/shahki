package tga.js.shahski.kt.bots

import tga.js.shahski.kt.Bot
import tga.js.shahski.kt.Field
import tga.js.shahski.kt.Step
import tga.js.shahski.kt.StepAttempt

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 */
class RandomBot: Bot {

    private var colorVal: Int = -1

    override var color: Int
        get() = colorVal
        set(value) {colorVal = value}

    override fun getStep(nStep: Int, nAttempt: Int, field: Field, steps: List<Step>, history: List<Field>): StepAttempt {

        return when(color) {
            Field.WHITE -> when(nStep){
                0 -> (2 to 1) to (3 to 2)
                else -> (2 to 1) to (3 to 2)
            }
            Field.BLACK -> when(nStep){
                0 -> (5 to 2) to (4 to 1)
                else -> (5 to 2) to (4 to 1)
            }
            else -> throw RuntimeException("Unknown color!")
        }

    }
}