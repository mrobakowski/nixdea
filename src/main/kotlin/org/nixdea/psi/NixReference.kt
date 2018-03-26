package org.nixdea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.parentOfType

class NixReference(ref: NixRef): PsiReferenceBase<NixRef>(ref, TextRange.EMPTY_RANGE.grown(ref.textLength)) {
    override fun resolve(): PsiElement? {
        element.withParentDeclarations {
            it[element.text]?.let { return it }
        }
        return null
    }

    override fun getVariants(): Array<Any> {
        val res = mutableListOf<PsiElement>()
        element.withParentDeclarations { res += it.values }
        return res.toTypedArray()
    }
}

inline fun PsiElement.withParentDeclarations(f: (Map<String, PsiElement>) -> Unit) {
    var parentScope = parentOfType<HasDeclarations>()
    while (parentScope != null) {
        f(parentScope.declarations)
        parentScope = parentScope.parentOfType()
    }
}