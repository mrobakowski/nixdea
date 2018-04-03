package org.nixdea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase

class NixPath(path: NixPathLit) : PsiReferenceBase<NixPathLit>(path, TextRange.EMPTY_RANGE.grown(path.textLength)) {
    override fun resolve(): PsiElement? {
        element.path?.let { return resolveSimplePath() }
        element.hpath?.let { return resolveHomePath() }
        element.spath?.let { return resolveSystemPath() }
        return null
    }

    private val pathText get() = if (element.text.endsWith(".nix")) { // TODO: check how Nix does this
        element.text
    } else {
        "${element.text.removeSuffix("/")}/default.nix"
    }

    private val virtualHome get() = VirtualFileManager.getInstance()
            .findFileByUrl(VirtualFileManager.constructUrl("file", System.getProperty("user.home")))

    private fun resolveSystemPath(): PsiElement? {
        TODO("figure out system path resolution logic")
    }

    private fun resolveHomePath(): PsiElement? {
        val relativeToHome = "./" + pathText.removePrefix("~").removePrefix("/")
        val vFile = virtualHome?.findFileByRelativePath(relativeToHome) ?: return null
        return PsiManager.getInstance(element.project).findFile(vFile)
    }

    private fun resolveSimplePath(): PsiElement? {
        val vFile = element.containingFile.containingDirectory.virtualFile.findFileByRelativePath(pathText)
                ?: return null
        return PsiManager.getInstance(element.project).findFile(vFile)
    }

    override fun getVariants(): Array<Any> {
        return arrayOf()
        // TODO("not implemented")
    }
}