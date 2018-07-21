package tga.js.shahski.kt

/**
 * Created by grigory@clearscale.net on 7/21/2018.
 */
interface Bot {

    /**
     * the function for the bot initialization.
     * game will call this method before start and pass to the bot his color.
     *
     * @param color - possible values are: Field.WHITE oR Field.BLACK
     */
    fun initiate(color: Int)

    /**
     *
     *  @param nStep - a number of the step (0, 1, 2, ...)
     *  @param nAttempt - a number of the attempt inside  (0,1,2) -
     *             only 3 attempts possible.
     *             if no possible step will be returned during 3 attempts - the player will loose the game
     *  @param field - the current field
     *  @param steps - history of steps (without rejected steps)
     *  @param history - list of fields in the state before every step in the 'steps' list.
     *
     *  @return the bot should calculate and return a StepAttempt)
     */
    fun getStep(nStep: Int, nAttempt: Int, field: Field, steps: List<Step>, history: List<Field>): StepAttempt

}

