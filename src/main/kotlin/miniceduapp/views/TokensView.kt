package miniceduapp.views

import javafx.event.EventTarget
import minic.Compiler
import miniceduapp.viewmodels.MainViewModel
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import tornadofx.*

class TokensView : View("Lexer tokens") {
    val mainViewModel: MainViewModel by inject()

    var outputArea: CodeArea by singleAssign()

    override val root = hbox {
        outputArea = codeEditor {
            minWidth = 450.0
            minHeight = 400.0
            isWrapText = true
            isEditable = false
            paragraphGraphicFactory = LineNumberFactory.get(this)
        }

        style {
            padding = box(10.px)
        }
    }

    fun EventTarget.codeEditor(op: (CodeArea.() -> Unit)? = null): CodeArea {
        val codeArea = CodeArea()
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        return opcr(this, codeArea, op);
    }

    override fun onDock() {
        val text = Compiler(mainViewModel.programCode).tokens
                .groupBy { it.line }
                .map { it.value.map { it.name }.joinToString(", ") }
                .joinToString("\n")
        outputArea.replaceText(text)
    }
}
