package tga.shashki.ui_console

import tga.shashki.core.bots.DevBot
import tga.shashki.core.bots.MoviesHistoryItem
import tga.shashki.core.game.Field
import tga.shashki.core.game.Game
import tga.shashki.core.game.Moves

/**
 * Created by grigory@clearscale.net on 2/11/2019.
 */

val game = Game(
        whiteBot =  DevBot(),
        blackBot = DevBot(),
        maxAttempts =  3,
        loggingCallback = { logTurn(it) }
)


fun main(args: Array<String>) {


    println("Game started")

    while (game.status == "in progress") {

        printGame()

        val command: String = readCommand()
        if (command == "exit") break

        val moves: Moves = convertCommandToMoves(command)

        (game.currentBot as DevBot).force(moves)
        game.turn()


    }

    printGame()

    println("Game over")
}

fun convertCommandToMoves(command: String): Moves = command.split(" ").map {
        (
            when (it[0]) {
                in 'a'..'h' -> it[0] - 'a'
                in 'A'..'H' -> it[0] - 'A'
                in '1'..'8' -> it[0] - '1'
                       else -> throw RuntimeException("wrong input format")
            }
        ) to (it[1]-'1')
}

fun printGame() {

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
}

fun readCommand(): String {

    print(game.status)
    print(" | ")
    print( when (game.currentBot.color){
        Field.WHITE -> "white"
        Field.BLACK -> "black"
               else -> "unknown color"
    })

    print("[attempt ${game.attempt+1}/${game.maxAttempts}]")

    print(">")
    val cmd = (readLine() ?: "exit").trim()

    return when(cmd) {
        "", "e", "E", "x", "X", "q", "Q" -> "exit"
        else -> cmd
    }
}


fun logTurn(turn: MoviesHistoryItem) {
    println(turn)
}