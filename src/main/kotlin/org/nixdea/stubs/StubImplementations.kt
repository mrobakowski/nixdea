package org.nixdea.stubs

import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import org.nixdea.NixLanguage
import org.nixdea.psi.NixFile

class NixFileStub(nixFile: NixFile?) : PsiFileStubImpl<NixFile>(nixFile) {
    override fun getType() = Type

    object Type : IStubFileElementType<NixFileStub>(NixLanguage) {
        // Bump this number if Stub structure changes
        override fun getStubVersion(): Int = 1

        override fun getBuilder(): StubBuilder = object : DefaultStubBuilder() {
            override fun createStubForFile(file: PsiFile): StubElement<*> = NixFileStub(file as NixFile)
        }

        override fun serialize(stub: NixFileStub, dataStream: StubOutputStream) {
            // TODO?
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): NixFileStub {
            // TODO?
            return NixFileStub(null)
        }

        override fun getExternalId(): String = "Nix.file"
    }
}

