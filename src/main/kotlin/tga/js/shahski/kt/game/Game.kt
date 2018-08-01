package tga.js.shahski.kt.game

import tga.js.shahski.kt.bots.Bot
import tga.js.shahski.kt.bots.RandomBot
import tga.js.shahski.kt.bots.MoviesHistoryItem

class Game(
        private val maxAttempts: Int,
        private val loggingCallback: (MoviesHistoryItem) -> Unit
) {

    var field = Field()

    var status: String = "in progress"

    val blackBot: Bot = RandomBot().apply { color = Field.BLACK }
    val whiteBot: Bot = RandomBot().apply { color = Field.WHITE }

    var moviesHistory: List<MoviesHistoryItem> = listOf()
    var fieldHistory: List<Field> = listOf(field)

    var currentBot = whiteBot
    var otherBot = blackBot

    var nStep = 0
    var nAttempt = 0


    fun turn() {

        if (status != "in progress") throw RuntimeException("The game status should be 'in progress' but it is '$status'!")

        val result =
            if (field.noMovesAvailable(currentBot.color)) {
                Field.Loose(field, "No more moves available")
            } else {

                val movies = currentBot.getMoves(nStep, nAttempt, field, moviesHistory, fieldHistory)
                println(movies)

                val response = field.move(currentBot.color, movies)

                val historyItem = MoviesHistoryItem(currentBot.color, nStep, nAttempt, movies, response)
                moviesHistory += historyItem

                try { loggingCallback(historyItem) } catch (e: Throwable) {}

                if (response is Field.NewFieldResponse) {
                    field = response.field
                    fieldHistory += field
                }

                if (response is Field.Err && nAttempt >= maxAttempts) {
                    Field.Loose(field, "Max number of attempts ($maxAttempts) achived during a single move.")

                } else {
                    response
                }

            }


        when (result) {
            is Field.Err -> {
                    // if the step failed - increase the number of wrong attempts
                    nAttempt++
                }
            is Field.Done -> {
                    if (currentBot == blackBot) nStep++
                    nAttempt = 0
                    otherBot = currentBot
                    currentBot = if (currentBot == whiteBot) blackBot else whiteBot
                }
            is Field.Win -> {
                    status = "Bot ${currentBot.color} Win!"
                    currentBot.win(field, moviesHistory, fieldHistory)
                    otherBot.loose(field, moviesHistory, fieldHistory)
                }
            is Field.Loose -> {
                    status = "Bot ${currentBot.color} Loose!"
                    currentBot.loose(field, moviesHistory, fieldHistory)
                    otherBot.win(field, moviesHistory, fieldHistory)
                }
        }

    }



}