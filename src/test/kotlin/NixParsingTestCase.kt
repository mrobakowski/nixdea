import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.ParsingTestCase
import org.nixdea.parser.NixParserDefinition

class NixParsingTestCase : ParsingTestCase("parsing-test-fixtures", "nix", true, NixParserDefinition()) {
    override fun getTestDataPath() = "src/test/resources"
    fun testLambda() = doTest(true)
    fun testComposableDerivations() = doTest(true)
    fun testEmptyList() = doTest(true)
    fun testLambdaAndSelect() = doTest(true)

    private fun hasError(file: PsiFile): Boolean {
        var hasErrors = false
        file.accept(object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement?) {
                if (element is PsiErrorElement) {
                    hasErrors = true
                    return
                }
                element!!.acceptChildren(this)
            }
        })
        return hasErrors
    }

    override fun checkResult(targetDataName: String?, file: PsiFile?) {
        super.checkResult(targetDataName, file)
        check(!hasError(file!!)){
            "Error in well formed file ${file.name}"
        }
    }
}