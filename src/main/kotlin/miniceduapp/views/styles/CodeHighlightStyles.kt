package miniceduapp.views.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import miniceduapp.views.editor.HighlightStyle
import tornadofx.*

class CodeHighlightStyles : Stylesheet(), HighlightStyle {

    companion object {
        val keyword by cssclass()
        val semicolon by cssclass()
        val paren by cssclass()
        val bracket by cssclass()
        val brace by cssclass()
        val string by cssclass()
        val comment by cssclass()
    }

    override val classes: List<String>
        get() = listOf(
                keyword.name,
                semicolon.name,
                paren.name,
                bracket.name,
                brace.name,
                string.name,
                comment.name
        )

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
