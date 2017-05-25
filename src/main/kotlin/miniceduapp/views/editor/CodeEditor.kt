package miniceduapp.views.editor

import javafx.event.EventTarget
import miniceduapp.views.styles.CodeHighlightStyles
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import tornadofx.*
import java.util.regex.Pattern

fun EventTarget.codeEditor(paneOp: (VirtualizedScrollPane<CodeArea>.() -> Unit)? = null, op: (CodeArea.() -> Unit)? = null): CodeArea {
    val codeArea = CodeArea()
    val scrollPane = VirtualizedScrollPaneExt(codeArea)

    addChildIfPossible(scrollPane)

    paneOp?.invoke(scrollPane)
    op?.invoke(codeArea)

    return codeArea
}

fun CodeArea.showLineNumbers() {
    paragraphGraphicFactory = LineNumberFactory.get(this)
}

fun CodeArea.addSyntaxHighlighting(syntaxHighlighter: SyntaxHighlighter) {
    richChanges()
            .filter { ch -> ch.inserted != ch.removed}
            .subscribe {
                setStyleSpans(0, syntaxHighlighter.computeHighlighting(text))
            }
}
