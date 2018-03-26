package org.nixdea.psi.mixins

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.nixdea.psi.NixLambda

abstract class NixLambdaImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), NixLambda {
    override val declarations: Map<String, PsiElement> get() = param.let { param ->
        val res = mutableMapOf<String, PsiElement>()

        val id = param.paramStartingWithId?.paramSetId ?: param.paramStartingWithSet?.paramSetId
        id?.let { res += it.text to it }

        val paramSet = param.paramStartingWithId?.paramSet ?: param.paramStartingWithSet?.paramSet
        paramSet?.paramSetAttrList?.let { attrs ->
            attrs.forEach { res += it.id.text to it}
        }

        res
    }
}