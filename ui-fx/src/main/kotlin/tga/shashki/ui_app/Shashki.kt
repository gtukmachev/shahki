package tga.shashki.ui_app

import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
/**
 * Created by grigory@clearscale.net on 2/11/2019.
 */
class ShashkiApp : Application() {

    val layout = "/resources/ShashkiApp.fxml"

    override fun start(primaryStage: Stage?) {

        System.setProperty("prism.lcdtext", "false") // for beautiful fonts on linux
        primaryStage?.scene = Scene(load<Parent?>(ShashkiApp::class.java.getResource(layout)))
        primaryStage?.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ShashkiApp::class.java)
        }
    }

}