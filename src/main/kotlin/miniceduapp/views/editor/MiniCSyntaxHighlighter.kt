package miniceduapp.views.editor

import miniceduapp.views.styles.CodeHighlightStyles
import java.util.regex.Pattern

class MiniCSyntaxHighlighter : RegexSyntaxHighlighter(PATTERN, CodeHighlightStyles()) {

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
}
