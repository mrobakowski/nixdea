package org.nixdea.psi.mixins

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import org.nixdea.NixFileType
import org.nixdea.psi.NixAttrAssign
import org.nixdea.psi.NixRef
import org.nixdea.psi.NixTypes.ID

abstract class NixNameOwnerMixin(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
    // honestly, this should be split into two mixins
    override fun getNameIdentifier(): PsiElement? = when (this) {
        is NixAttrAssign -> this.attrPath.attrList.firstOrNull()?.id
        else -> findChildByType(ID)
    }

    override fun getName(): String? = nameIdentifier?.text
    override fun setName(name: String): PsiElement {
        nameIdentifier?.replace(createIdentifier(project, name))
        return this
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()
}

fun createIdentifier(project: Project, name: String): PsiElement {
    val f = PsiFileFactory.getInstance(project).createFileFromText("DUMMY.nix", NixFileType, name)
    val ref = PsiTreeUtil.findChildOfType(f, NixRef::class.java, true)!!
    return ref.id
}