package org.nixdea

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object NixFileType : LanguageFileType(NixLanguage) {
    override fun getName(): String = "Nix"
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/nixdea.png")
    override fun getDefaultExtension(): String = "nix"
    override fun getCharset(file: VirtualFile, content: ByteArray): String = "UTF-8"
    override fun getDescription(): String = "Nix Files"
}