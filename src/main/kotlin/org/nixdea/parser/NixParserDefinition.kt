package org.nixdea.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageUtil
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.nixdea.stubs.NixFileStub
import org.nixdea.lexer.NixLexer
import org.nixdea.psi.NixFile
import org.nixdea.psi.NixTypes
import org.nixdea.psi.NixTypes.*

class NixParserDefinition : ParserDefinition {

    override fun createFile(viewProvider: FileViewProvider): PsiFile? = NixFile(viewProvider)

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, NixLexer())
    }

    override fun getFileNodeType(): IFileElementType = NixFileStub.Type
    override fun getStringLiteralElements(): TokenSet = TokenSet.create(STR, IND_STR)
    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
    override fun getCommentTokens() = TokenSet.create(MCOMMENT, SCOMMENT)
    override fun createElement(node: ASTNode?): PsiElement = NixTypes.Factory.createElement(node)
    override fun createLexer(project: Project?): Lexer = NixLexer()
    override fun createParser(project: Project?): PsiParser = NixParser()
}