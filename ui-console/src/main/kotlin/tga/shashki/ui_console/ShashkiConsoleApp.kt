package tga.shashki.ui_console

import tga.shashki.core.bots.MoviesHistoryItem
import tga.shashki.core.game.Field
import tga.shashki.core.game.Game
import tga.shashki.core.game.Moves

/**
 * Created by grigory@clearscale.net on 2/11/2019.
 */

fun main(args: Array<String>) {

    val game = Game(
                maxAttempts =  3,
                loggingCallback = { logTurn(it) }
            )

    println("Game started")

    while (game.status == "in progress") {

        printGame(game)

        val command: String = readCommand()
        if (command == "exit") break

        val moves: Moves = convertCommandToMoves(command)

        game.currentBot.force(moves)
        game.turn()


    }

    printGame(game)

    println("Game over")
}

fun convertCommandToMoves(command: String): Moves = command.split(" ").map {
    (it[0]-'1') to (it[1]-'1')
}

fun printGame(game: Game) {

    fun printLettersLine() {
        print("  ")
        for (c in 0 until Field.FIELD_SIZE ) {
            print("  ${'A' + c} ")
        }
        println("")
    }

    fun printSeparatorLine(startSymbol: Char = '├', midSymbol: Char = '┼', endSymbol: Char = '┤') {
        println("  ${startSymbol}───" + ("${midSymbol}───".repeat(Field.FIELD_SIZE-1)) + endSymbol)
    }

    printLettersLine()
    printSeparatorLine('┌', '┬', '┐')

    var row = Field.FIELD_SIZE

    for (l in 0 until Field.FIELD_SIZE ) {

        row--

        if (l > 0) printSeparatorLine()
        print("${row+1} ")

        for (c in 0 until Field.FIELD_SIZE) {

            val st = game.field.getStone(row,c)
            when {
                (st and Field.BLACK and Field.QUINN) > 0 -> print("│(*)")
                (st and Field.BLACK                ) > 0 -> print("│ * ")
                (st and Field.WHITE and Field.QUINN) > 0 -> print("│(o)")
                (st and Field.WHITE                ) > 0 -> print("│ o ")
                                                    else -> print("│   ")
            }

        }
        println("│ ${row+1}")
    }
    printSeparatorLine('└', '┴', '┘')
    printLettersLine()

    print(game.status)
}

fun readCommand(): String {
    print(">")
    return readLine() ?: "exit"
}


fun logTurn(turn: MoviesHistoryItem) {
    println(turn)
}