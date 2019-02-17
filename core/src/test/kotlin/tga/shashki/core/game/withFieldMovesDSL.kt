package tga.shashki.core.game

import tga.shashki.core.bots.Converters
import kotlin.test.fail

/**
 * Created by grigory@clearscale.net on 2/17/2019.
 */
interface withFieldMovesDSL {

    private fun Field.move(color: Int, cmd: String) = this.move( color, Converters.convertCommandToMoves(cmd))

    fun Field.white(cmd: String) = move( Field.WHITE, cmd)
    fun Field.black(cmd: String) = move( Field.BLACK, cmd)


    fun Field.MoveResponse.expectDoneResult() : Field.Done {
        if (this is Field.Done) return this
        fail("game response should be 'Done'")
    }

    fun Field.MoveResponse.expectErrResult() : Field.Err {
        if (this is Field.Err) return this
        fail("game response should be 'Err'")
    }

}