package miniceduapp.views

import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.scene.image.ImageView
import minic.Compiler
import miniceduapp.controllers.MainController
import tornadofx.*

class AstView : View("AST") {
    val controller: MainController by inject()

    var img: ImageView by singleAssign()

    override val root = hbox {
        img = imageview {

        }

        style {
            padding = box(10.px)
        }
    }

    fun show() {
        img.image = SwingFXUtils.toFXImage(Compiler(controller.text).drawAst(), null)
        img.scene.window.sizeToScene()
    }
}
