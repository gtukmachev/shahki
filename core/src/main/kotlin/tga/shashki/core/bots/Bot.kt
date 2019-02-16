package tga.shashki.core.bots

import tga.shashki.core.game.Field
import tga.shashki.core.game.Moves

/**
 * Created by grigory@clearscale.net on 7/21/2018.
 */
interface Bot {

    /**
     *  color of the bot
     *
     *  @see Field.BLACK
     *  @see Field.WHITE
     */
    var color: Int

    /**
     *
     *  @param nStep a number of the moves (0, 1, 2, ...)
     *  @param nAttempt a number of the attempt inside  (0,1,2) -
     *             only 3 attempts possible.
     *             if no possible moves will be returned during 3 attempts - the player will loose the game
     *  @param field the current field
     *  @param stepsHistory fieldsHistory of stepsHistory (without rejected stepsHistory)
     *  @param fieldsHistory list of fields in the state before every moves in the 'stepsHistory' list.
     *
     *  @return the bot should calculate and return a Moves)
     */
    fun getMoves(nStep: Int, nAttempt: Int, field: Field, stepsHistory: List<MoviesHistoryItem>, fieldsHistory: List<Field>): Moves

    /**
     * A game will call the function only once in case the bot will win the game
     * no requirements for the implementation present
     */
    fun win(field: Field, stepsHistory: List<MoviesHistoryItem>, fieldsHistory: List<Field>)

    /**
     * A game will call the function only once in case the bot will loose the game
     * no requirements for the implementation present
     */
    fun loose(field: Field, stepsHistory: List<MoviesHistoryItem>, fieldsHistory: List<Field>)

}


data class MoviesHistoryItem(val color: Int, val nStep: Int, val nAttempt: Int, val moves: Moves, val result: Any?)

