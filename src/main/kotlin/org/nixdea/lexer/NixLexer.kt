package org.nixdea.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.TokenType.WHITE_SPACE
import org.nixdea.psi.NixTypes.IND_STR
import org.nixdea.psi.NixTypes.STR
import org.nixdea.psi.tokenSetOf

class NixLexer: MergingLexerAdapter(FlexAdapter(object : _NixLexer() {
    override fun reset(buffer: CharSequence?, start: Int, end: Int, initialState: Int) {
        onReset()
        super.reset(buffer, start, end, initialState)
    }
}), tokenSetOf(STR, IND_STR, WHITE_SPACE))