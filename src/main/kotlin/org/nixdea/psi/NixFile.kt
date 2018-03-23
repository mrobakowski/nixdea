package org.nixdea.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import org.nixdea.NixFileType
import org.nixdea.NixLanguage
import org.nixdea.stubs.NixFileStub

class NixFile(
        fileViewProvider: FileViewProvider
) : PsiFileBase(fileViewProvider, NixLanguage), NixExpr {
    override fun getReference(): PsiReference? = null

    override fun getFileType(): FileType = NixFileType

    override fun getStub(): NixFileStub? = super.getStub() as NixFileStub?

    override fun getOriginalFile(): NixFile = super.getOriginalFile() as NixFile

    override fun setName(name: String): PsiElement {
        val nameWithExtension = if ('.' !in name) "$name.rs" else name
        return super.setName(nameWithExtension)
    }

}

val PsiFile.nixExpr: NixExpr? get() = this as? NixFile

val VirtualFile.isNotNixFile: Boolean get() = !isRustFile
val VirtualFile.isRustFile: Boolean get() = fileType == NixFileType


