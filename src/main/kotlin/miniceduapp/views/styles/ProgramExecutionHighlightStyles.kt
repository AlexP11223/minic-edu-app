package miniceduapp.views.styles

import javafx.scene.paint.Color
import miniceduapp.views.editor.HighlightStyle
import tornadofx.*

class ProgramExecutionStyles : Stylesheet(), HighlightStyle {

    companion object {
        val command by cssclass()
        val exception by cssclass()
    }

    override val classes: List<String>
        get() = listOf(
                command.name,
                exception.name
        )

    init {
        command {
            fill = c("#65657D")
        }
        exception {
            fill = Color.RED
        }
    }
}
