package org.nixdea.psi

import com.intellij.psi.tree.IElementType
import org.nixdea.NixLanguage

class NixTokenType(name: String): IElementType(name, NixLanguage)