package org.nixdea.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.nixdea.lexer.NixLexer
import org.nixdea.psi.NIX_KEYWORDS
import org.nixdea.psi.NIX_OPERATORS
import org.nixdea.psi.NIX_STR_TOKENS
import org.nixdea.psi.NixTypes.*
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default


class NixHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = NixLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return pack(when (tokenType) {
            ID -> Default.IDENTIFIER
            INT -> Default.NUMBER
            SEMI -> Default.SEMICOLON
            DOT -> Default.DOT
            COMMA -> Default.COMMA
            LBRAC, RBRAC -> Default.BRACKETS
            LCURLY, RCURLY, DOLLAR_CURLY -> Default.BRACES
            LPAREN, RPAREN -> Default.PARENTHESES


            SCOMMENT -> Default.LINE_COMMENT
            MCOMMENT -> Default.BLOCK_COMMENT

            in NIX_OPERATORS -> Default.OPERATION_SIGN
            in NIX_KEYWORDS, BOOL -> Default.KEYWORD
            in NIX_STR_TOKENS -> Default.STRING // TODO: highlight escape sequences
            else -> null
        })
    }

}
