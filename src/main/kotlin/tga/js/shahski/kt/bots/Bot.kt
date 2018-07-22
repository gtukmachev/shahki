package tga.js.shahski.kt.bots

import tga.js.shahski.kt.game.Field

/**
 * Created by grigory@clearscale.net on 7/21/2018.
 */
interface Bot {

    /**
     *  color of the bot
     */
    var color: Int

    /**
     *
     *  @param nStep - a number of the moves (0, 1, 2, ...)
     *  @param nAttempt - a number of the attempt inside  (0,1,2) -
     *             only 3 attempts possible.
     *             if no possible moves will be returned during 3 attempts - the player will loose the game
     *  @param field - the current field
     *  @param stepsHistory - fieldsHistory of stepsHistory (without rejected stepsHistory)
     *  @param fieldsHistory - list of fields in the state before every moves in the 'stepsHistory' list.
     *
     *  @return the bot should calculate and return a Moves)
     */
    fun getMoves(nStep: Int, nAttempt: Int, field: Field, stepsHistory: List<MoviesHistoryItem>, fieldsHistory: List<Field>): Moves

}

data class MoviesHistoryItem(val color: Int, val moves: Moves, val result: Any?)

/**
 * Created by grigory@clearscale.net on 7/22/2018.
 *
 * first = line
 * second = column
 *
 */
typealias Moves = List<Pair<Int, Int>>
