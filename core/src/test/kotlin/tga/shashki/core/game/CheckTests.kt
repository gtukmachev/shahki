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


    @Test fun t1() {
        field.white("B3 A4")
            .expectDoneResult()
            .withEmptyOn("B3")
            .withWhiteOn("A4")

    }


}