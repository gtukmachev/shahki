package tga.shashki.ui_app

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.stage.Stage
import javafx.scene.layout.GridPane


/**
 * Created by grigory@clearscale.net on 2/11/2019.
 */
class ShashkiApp : Application() {

    val BUTTON_PADDING = 0.0

    val NUM_BUTTON_LINES = 8
    val BUTTONS_PER_LINE = 8

    override fun start(primaryStage: Stage) {


        val grid = GridPane()
        grid.padding = Insets(BUTTON_PADDING)
        grid.hgap = BUTTON_PADDING
        grid.vgap = BUTTON_PADDING

        for (r in 0 until NUM_BUTTON_LINES) {
            for (c in 0 until BUTTONS_PER_LINE) {
                val number = r + c
                val button = Button(number.toString()).apply {
                    styleClass.add("field")
                    styleClass.add( if (number % 2 == 0) "white" else "black" )
                    setOnAction {
                        this.styleClass.add("active")
                    }
                }

                grid.add(button, c, (BUTTONS_PER_LINE - r)-1)
            }
        }

        val scrollPane = ScrollPane(grid)

        primaryStage.scene = Scene(scrollPane).apply {
            stylesheets.add("main.css")
        }
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ShashkiApp::class.java)
        }
    }

}