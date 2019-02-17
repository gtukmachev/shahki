package tga.shashki.core.game

import tga.shashki.core.bots.Converters
import kotlin.test.assertTrue

/**
 * Created by grigory@clearscale.net on 2/17/2019.
 */
interface withFieldAssertsDSL {

    private fun Field.NewFieldResponse.withStoneOn(position: String, color: Int): Field.NewFieldResponse{
        assertTrue("There should be a '${Converters.stoneToHumanReadableName(color)}' at the '$position' position!" ) {
            this.field.getStone(
                    Converters.toRow(position),
                    Converters.toCol(position)
            ) == color
        }
        return this
    }

    fun Field.NewFieldResponse.withWhiteOn(position: String) = withStoneOn(position, Field.WHITE)
    fun Field.NewFieldResponse.withBlackOn(position: String) = withStoneOn(position, Field.BLACK)

    fun Field.NewFieldResponse.withEmptyOn(position: String) = withStoneOn(position, Field.EMPTY)

    fun Field.NewFieldResponse.withWhiteQuinnOn(position: String) = withStoneOn(position, Field.WHITE + Field.QUINN)
    fun Field.NewFieldResponse.withBlackQuinnOn(position: String) = withStoneOn(position, Field.BLACK + Field.QUINN)

}