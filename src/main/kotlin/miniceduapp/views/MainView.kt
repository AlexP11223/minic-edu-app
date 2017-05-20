package miniceduapp.views

import javafx.event.EventTarget
import javafx.scene.control.TextArea
import miniceduapp.views.styles.Styles
import miniceduapp.controllers.MainController
import miniceduapp.views.styles.CodeHighlightStyles
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import tornadofx.*
import java.util.regex.Pattern

class MainView : View("") {
    val controller: MainController by inject()

    var inputField: CodeArea by singleAssign()
    var outputField: TextArea by singleAssign()

    override val root = borderpane {
        addClass(Styles.mainScreen)
        top {
            hbox {
                button("Tokens") {
                    setOnAction {
                        controller.text = inputField.text
                        find<TokensView>().openWindow()
                        find<TokensView>().show()
                    }
                }
                arrowLabel()
                button("Parse tree")
                arrowLabel()
                vbox {
                    button("   AST   ") {
                        setOnAction {
                            controller.text = inputField.text
                            find<AstView>().openWindow()
                            find<AstView>().show()
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
                stackpane {
                    inputField = codeEditor {
                        minWidth = 500.0
                        minHeight = 300.0
                        richChanges()
                                .filter { ch -> ch.inserted != ch.removed}
                                .subscribe {
                            setStyleSpans(0, computeHighlighting(text))
                        }
                        replaceText("""println("Hello");
int x = 42;
int y = x + 6 * 2 / (3 - 1);
print("x: " + toString(y));
""")

                    }
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

    fun EventTarget.arrowLabel() = label(" ➔ ") {
        addClass(Styles.arrowLabel)
    }

    fun EventTarget.codeEditor(op: (CodeArea.() -> Unit)? = null): CodeArea {
        val codeArea = CodeArea()
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        return opcr(this, codeArea, op);
    }

    private fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        val matcher = PATTERN.matcher(text)
        var lastKwEnd = 0
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        while (matcher.find()) {
            val styleClass = (if (matcher.group("KEYWORD") != null)
                CodeHighlightStyles.keyword.name
            else if (matcher.group("PAREN") != null)
                CodeHighlightStyles.paren.name
            else if (matcher.group("BRACE") != null)
                CodeHighlightStyles.brace.name
            else if (matcher.group("BRACKET") != null)
                CodeHighlightStyles.bracket.name
            else if (matcher.group("SEMICOLON") != null)
                CodeHighlightStyles.semicolon.name
            else if (matcher.group("STRING") != null)
                CodeHighlightStyles.string.name
            else if (matcher.group("COMMENT") != null)
                CodeHighlightStyles.comment.name
            else
                null)!! /* never happens */
            spansBuilder.add(emptyList<String>(), matcher.start() - lastKwEnd)
            spansBuilder.add(setOf(styleClass), matcher.end() - matcher.start())
            lastKwEnd = matcher.end()
        }
        spansBuilder.add(emptyList<String>(), text.length - lastKwEnd)
        return spansBuilder.create()
    }

    companion object {
        private val KEYWORDS = arrayOf("boolean", "break", "continue", "while", "double", "else", "if", "int")

        private val KEYWORD_PATTERN = "\\b(" + KEYWORDS.joinToString("|") + ")\\b"
        private val PAREN_PATTERN = "\\(|\\)"
        private val BRACE_PATTERN = "\\{|\\}"
        private val BRACKET_PATTERN = "\\[|\\]"
        private val SEMICOLON_PATTERN = "\\;"
        private val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
        private val COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"

        private val PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<PAREN>" + PAREN_PATTERN + ")"
                        + "|(?<BRACE>" + BRACE_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        )
    }
}

