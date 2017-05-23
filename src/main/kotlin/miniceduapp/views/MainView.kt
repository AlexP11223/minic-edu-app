package miniceduapp.views

import javafx.event.EventTarget
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import miniceduapp.viewmodels.MainViewModel
import miniceduapp.views.editor.MiniCSyntaxHighlighter
import miniceduapp.views.editor.addSyntaxHighlighting
import miniceduapp.views.editor.codeEditor
import miniceduapp.views.editor.showLineNumbers
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*

class MainView : View("") {
    val viewModel: MainViewModel by inject()

    var codeArea: CodeArea by singleAssign()
    var outputField: TextArea by singleAssign()

    override val root = borderpane {
        addClass(Styles.mainScreen)
        minHeight = 500.0
        minWidth = 600.0
        top {
            hbox {
                button("Tokens") {
                    setOnAction {
                        find<TokensView>().openWindow()
                    }
                }
                arrowLabel()
                vbox {
                    button("   AST   ") {
                        setOnAction {
                            find<AstView>().openWindow()
                        }
                    }
                    button("Symbols") {
                        vboxConstraints { marginTop = 5.0 }
                        vboxConstraints { marginBottom = 10.0 }
                    }
                }
                arrowLabel()
                button("Bytecode")
                arrowLabel()
                button("Execute") {
                    setOnAction {
                    }
                }
            }
        }
        center {
            hbox {
                codeArea = codeEditor(paneOp = {
                    hgrow = Priority.ALWAYS
                }) {
                    addSyntaxHighlighting(MiniCSyntaxHighlighter())
                    showLineNumbers()
                }
            }
        }
        bottom {
            vbox {
                label("Output")
                outputField = textarea {
                    maxHeight = 100.0
                }
            }
        }
    }

    init {
        codeArea.textProperty().onChange {
            viewModel.programCode = it!!
        }
        viewModel.programCodeProperty.onChange {
            if (it != codeArea.text) {
                codeArea.replaceText(it)
            }
        }

        viewModel.programCode = """println("Hello");
int x = 42;
int y = x + 8 * 2 / (3 - 1);
print("x: " + toString(y));
"""
    }

    fun EventTarget.arrowLabel() = label(" ➔ ") {
        addClass(Styles.arrowLabel)
    }
}

