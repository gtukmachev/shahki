package tga.js.shahski.kt.game

import tga.js.shahski.kt.WrongStep
import tga.js.shahski.kt.bots.Bot
import tga.js.shahski.kt.bots.RandomBot
import tga.js.shahski.kt.bots.MoviesHistoryItem

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 */
class Game(private val loggingCallback: (color: Int, step: Moves, e: WrongStep?) -> Unit ) {

    var field = Field()

    val blackBot: Bot = RandomBot().apply { color = Field.BLACK }
    val whiteBot: Bot = RandomBot().apply { color = Field.WHITE }

    var moviesHistory: List<MoviesHistoryItem> = listOf()
    var fieldHistory: List<Field> = listOf(field)

    var currentBot = whiteBot
    var nStep = 0
    var nAttempt = 0


    fun turn() {
        val movies = currentBot.getMoves(nStep, nAttempt, field, moviesHistory, fieldHistory)

        val result = doStep(currentBot.color, movies)

        moviesHistory += MoviesHistoryItem(currentBot.color, nStep, movies, result)
        fieldHistory += field

        if (result) { // if the step is successful

            if (currentBot == blackBot) nStep++
            nAttempt = 0
            currentBot = if (currentBot == whiteBot) blackBot else whiteBot

        } else {
            // if the step failed - increase the number of wrong attempts
            nAttempt++
        }
    }

    private fun doStep(color: Int, movies: Moves): Boolean {

        fun logStep(e: WrongStep? = null) { loggingCallback(color, movies, e) }

        try {
            field = field.move(color, movies)
            logStep()
            return true
        } catch (e: WrongStep) {
            logStep(e)
        }
        return false

    }

}