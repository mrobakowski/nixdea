package org.nixdea.psi.mixins

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.nixdea.psi.NixAttrAssign
import org.nixdea.psi.NixRecSet

abstract class NixRecSetImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), NixRecSet {
    override val declarations: Map<String, PsiElement> get() = this.simpleSet.let {
        val res = mutableMapOf<String, PsiElement>()

        it?.bindingList?.forEach { binding ->
            when (binding) {
                is NixAttrAssign -> binding.attrPath.attrList.firstOrNull()?.let { res += it.text to it }
                else -> Unit // let's ignore others for now
            }
        }

        res
    }
}