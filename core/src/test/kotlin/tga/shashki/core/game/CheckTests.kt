package tga.shashki.core.game

import kotlin.test.*

/**
 * Created by grigory@clearscale.net on 2/17/2019.
 */
class CheckTests : withFieldMovesDSL, withFieldAssertsDSL {

    lateinit var field: Field

    @BeforeTest fun setup() {
        field = Field()
    }


    @Test fun white_B3_A4_should_move_OK() {
        field.white("B3 A4")
            .expectDoneResult()
            .withEmptyOn("B3")
            .withWhiteOn("A4")

    }

    @Test fun white_B4_A4_should_fails() {
        field.white("B4 A4")
            .expectErrResult()

    }


}