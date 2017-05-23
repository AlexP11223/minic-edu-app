package miniceduapp.views.editor

import miniceduapp.views.styles.CodeHighlightStyles
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import java.util.regex.Pattern

class MiniCSyntaxHighlighter : SyntaxHighlighter {

    companion object {
        private val KEYWORDS = arrayOf("boolean", "break", "continue", "while", "double", "else", "if", "int", "string")

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

    override fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
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
}
