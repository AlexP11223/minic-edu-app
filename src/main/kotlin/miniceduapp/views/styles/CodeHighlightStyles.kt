package miniceduapp.views.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class CodeHighlightStyles : Stylesheet() {
    companion object {
        val keyword by cssclass()
        val semicolon by cssclass()
        val paren by cssclass()
        val bracket by cssclass()
        val brace by cssclass()
        val string by cssclass()
        val comment by cssclass()
    }

    init {
        keyword {
            fontWeight = FontWeight.BOLD
            fill = Color.BLUE
        }
        semicolon {
            fontWeight = FontWeight.BOLD
        }
        paren {
            fontWeight = FontWeight.BOLD
            fill = Color.FIREBRICK
        }
        bracket {
            fontWeight = FontWeight.BOLD
            fill = Color.DARKGREEN
        }
        brace {
            fontWeight = FontWeight.BOLD
            fill = Color.TEAL
        }
        string {
            fill = Color.GREEN
        }
        comment {
            fill = c(128, 128, 128)
        }
    }
}
