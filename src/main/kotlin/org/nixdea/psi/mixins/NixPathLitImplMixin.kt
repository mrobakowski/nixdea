package org.nixdea.psi.mixins

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.nixdea.psi.NixPath
import org.nixdea.psi.NixPathLit

abstract class NixPathLitImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), NixPathLit {
    override fun getReference() = NixPath(this)
}