package org.nixdea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase

class NixPath(path: NixPathLit): PsiReferenceBase<NixPathLit>(path, TextRange.EMPTY_RANGE.grown(path.textLength)) {
    override fun resolve(): PsiElement? {
        element.path?.let { return resolveSimplePath() }
        element.hpath?.let { return resolveHomePath() }
        element.spath?.let { return resolveSystemPath() }
        return null
    }

    private fun resolveSystemPath(): PsiElement? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun resolveHomePath(): PsiElement? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun resolveSimplePath(): PsiElement? {
        val path = if (element.text.endsWith(".nix")) element.text else "${element.text.removeSuffix("/")}/default.nix"
        val vFile = element.containingFile.containingDirectory.virtualFile.findFileByRelativePath(path) ?: return null
        return PsiManager.getInstance(element.project).findFile(vFile)
    }

    override fun getVariants(): Array<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}