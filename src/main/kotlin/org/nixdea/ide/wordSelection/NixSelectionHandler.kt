package org.nixdea.ide.wordSelection

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class NixSelectionHandler : ExtendWordSelectionHandler {
    override fun canSelect(e: PsiElement): Boolean {
        // TODO
        return false
    }

    override fun select(e: PsiElement, editorText: CharSequence, cursorOffset: Int, editor: Editor): List<TextRange>? {
        // TODO
        return listOf()
    }
}
