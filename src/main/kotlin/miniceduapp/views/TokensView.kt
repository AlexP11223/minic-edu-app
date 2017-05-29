package miniceduapp.views

import javafx.collections.ListChangeListener
import javafx.scene.layout.Priority
import minic.frontend.lexer.Token
import miniceduapp.viewmodels.TokensViewModel
import miniceduapp.views.editor.codeEditor
import miniceduapp.views.editor.showLineNumbers
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*

class TokensView : View("Lexer tokens") {
    val viewModel: TokensViewModel by inject()

    var outputArea: CodeArea by singleAssign()

    override val root = hbox {
        addClass(Styles.windowContent)
        stackpane {
            hgrow = Priority.ALWAYS
            outputArea = codeEditor(paneOp = {
                hgrow = Priority.ALWAYS
            }) {
                isWrapText = true
                isEditable = false
                //showLineNumbers() // weird bug, onDock doesn't fire if called here
            }
            imageview("loading.gif") {
                visibleWhen { viewModel.status.running }
            }
        }
    }

    init {
        viewModel.tokens.addListener { change: ListChangeListener.Change<out Token> ->
            val text = change.list
                    .groupBy { it.line }
                    .map { it.value.map { it.name }.joinToString(", ") }
                    .joinToString("\n")
            outputArea.replaceText(text)
        }
    }

    override fun onDock() {
        setWindowMinSize(500, 400)
        outputArea.showLineNumbers()

        viewModel.loadTokens()
    }
}
