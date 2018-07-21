package tga.js.shahski.kt.game

import tga.js.shahski.kt.WrongStep
import tga.js.shahski.kt.bots.Bot
import tga.js.shahski.kt.bots.RandomBot
import tga.js.shahski.kt.bots.Step
import tga.js.shahski.kt.bots.StepAttempt

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 */
class Game(private val loggingCallback: (color: Int, step: StepAttempt, e: WrongStep?) -> Unit ) {

    var field = Field()

    val blackBot: Bot = RandomBot().apply { color = Field.BLACK }
    val whiteBot: Bot = RandomBot().apply { color = Field.WHITE }

    var steps: List<Step> = listOf()
    var history: List<Field> = listOf(field)

    var currentBot = whiteBot
    var nStep = 0
    var nAttempt = 0


    fun turn() {
        val theStepAttempt = currentBot.getStep(nStep, nAttempt, field, steps, history)

        val result = doStep(currentBot.color, theStepAttempt)

        if (result) {
            steps += Step(currentBot.color, theStepAttempt, null)
            history += field

            if (currentBot == blackBot) nStep++
            nAttempt = 0
            currentBot = if (currentBot == whiteBot) blackBot else whiteBot

        } else {
            nAttempt++

        }

    }

    private fun doStep(color: Int, step: StepAttempt): Boolean {

        fun logStep(e: WrongStep? = null) {
            loggingCallback(color, step, e)
        }

        try {
            field = field.move(color, step.first, step.second)
            logStep()
            return true
        } catch (e: WrongStep) {
            logStep(e)
        }
        return false

    }

}