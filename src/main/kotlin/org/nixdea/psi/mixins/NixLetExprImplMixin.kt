package org.nixdea.psi.mixins

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.nixdea.psi.NixAttrAssign
import org.nixdea.psi.NixLetExpr

abstract class NixLetExprImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), NixLetExpr {
    override val declarations: Map<String, PsiElement> get() = bindingList.let { bindings ->
        val res = mutableMapOf<String, PsiElement>()
        bindings.forEach { binding ->
            when (binding) {
                is NixAttrAssign -> binding.attrPath.attrList.firstOrNull()?.let { res += it.text to it }
                else -> Unit // let's ignore others for now
            }
        }
        res
    }
}