package org.nixdea

import com.intellij.lang.Language


object NixLanguage : Language("Nix", "text/nix", "text/x-nix", "application/x-nix") {
    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Nix"
}