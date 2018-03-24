import com.intellij.testFramework.ParsingTestCase
import org.nixdea.parser.NixParserDefinition

class NixParsingTestCase : ParsingTestCase("parsing-test-fixtures", "nix", true, NixParserDefinition()) {
    override fun getTestDataPath() = "src/test/resources"
    fun testLambda() = doTest(true)
}