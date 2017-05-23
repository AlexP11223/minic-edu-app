package miniceduapp.views.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val mainScreen by cssclass()
        val arrowLabel by cssclass()
        val whitePanel by cssclass()

        private val paragraphBox by cssclass("paragraph-box")
        private val hasCaret by csspseudoclass("has-caret")
    }

    init {
        mainScreen {
            padding = box(10.px)
        }
        arrowLabel {
            fontSize = 1.6.em
            fontWeight = FontWeight.BOLD
        }

        whitePanel {
            backgroundColor = multi(Color.WHITE)
            viewport {
                backgroundColor = multi(Color.TRANSPARENT)
            }
        }

        paragraphBox {
            and(hasCaret) {
                backgroundColor = multi(c("#f2f9fc"))
            }
        }

    }
}

