package miniceduapp

import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val mainScreen by cssclass()
        val arrowLabel by cssclass()

        val keyword by cssclass()
    }

    init {
        mainScreen {
            padding = box(10.px)
        }
        arrowLabel {
            fontSize = 1.6.em
            fontWeight = FontWeight.BOLD
        }

        keyword {
            fontWeight = FontWeight.BOLD
        }
    }
}

