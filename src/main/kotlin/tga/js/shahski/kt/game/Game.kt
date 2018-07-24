package tga.js.shahski.kt.game

import tga.js.shahski.kt.WrongStep
import tga.js.shahski.kt.bots.Bot
import tga.js.shahski.kt.bots.RandomBot
import tga.js.shahski.kt.bots.MoviesHistoryItem

class Game(private val loggingCallback: (MoviesHistoryItem) -> Unit ) {

    var field = Field()

    val blackBot: Bot = RandomBot().apply { color = Field.BLACK }
    val whiteBot: Bot = RandomBot().apply { color = Field.WHITE }

    var moviesHistory: List<MoviesHistoryItem> = listOf()
    var fieldHistory: List<Field> = listOf(field)

    var currentBot = whiteBot
    var nStep = 0
    var nAttempt = 0


    fun force(moves: Moves){
        currentBot.force(moves)
    }

    fun turn() {

        val movies = currentBot.getMoves(nStep, nAttempt, field, moviesHistory, fieldHistory)
        val result = doStep(currentBot.color, movies)

        val historyItem = MoviesHistoryItem(currentBot.color, nStep, nAttempt, movies, result)

        try { loggingCallback(historyItem) } catch (e: Throwable) {}

        moviesHistory += historyItem
        fieldHistory += field

        if (result == null) { // if the step is successful

            if (currentBot == blackBot) nStep++
            nAttempt = 0
            currentBot = if (currentBot == whiteBot) blackBot else whiteBot

        } else {
            // if the step failed - increase the number of wrong attempts
            nAttempt++
        }
    }

    private fun doStep(color: Int, movies: Moves): WrongStep? = try {
        field = field.move(color, movies)
        null
    } catch (e: WrongStep) {
        e
    }




}