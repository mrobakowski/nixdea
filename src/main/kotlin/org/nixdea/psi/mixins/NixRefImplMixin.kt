package org.nixdea.psi.mixins

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.nixdea.psi.NixRef
import org.nixdea.psi.NixReference

abstract class NixRefImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), NixRef {
    override fun getReference() = NixReference(this)
}