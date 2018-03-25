package org.nixdea.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.nixdea.lexer.NixLexer

class NixHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = NixLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        // TODO
        return pack(null)
    }

}
