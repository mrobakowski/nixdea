package org.nixdea.psi

import com.intellij.psi.PsiElement

interface HasDeclarations: PsiElement {
    val declarations: Map<String, PsiElement>
}