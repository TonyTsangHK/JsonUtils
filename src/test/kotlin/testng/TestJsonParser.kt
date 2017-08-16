package testng

import org.testng.annotations.Test
import utils.file.FileUtil
import utils.json.parser.JsonParser

import org.testng.Assert.assertTrue
import org.testng.Assert.fail
import utils.test.JsonTestUtils
import utils.test.TestData
import utils.json.parser.JsonFormatter

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-08-15
 * Time: 14:57
 */
/**
 * Test json parser
 * 
 * JSONObject & JSONArray not tested
 */
class TestJsonParser {
    private val testData = TestData()
    private val jsonParser = JsonParser.getInstance()
    
    @Test
    fun testParseMap() {
        val indentedMap = jsonParser.parseMap<Any?>(FileUtil.getFileContent(javaClass.getResourceAsStream("/mapResultIndented.json")))
        
        if (indentedMap == null) {
            fail("Failed to parse indented map!")
        } else {
            val verifyResult = JsonTestUtils.verifyMap(testData.testMap, indentedMap)
            assertTrue(verifyResult.pass, "Indented map verification failure, reason: ${verifyResult.reason}!")
        }
        
        val noIndentationMap = jsonParser.parseMap<Any?>(FileUtil.getFileContent(javaClass.getResourceAsStream("/mapResultNoIndentation.json")))
        
        if (noIndentationMap == null) {
            fail("Failed to parse no indentation map!")
        } else {
            val verifyResult = JsonTestUtils.verifyMap(testData.testMap, noIndentationMap)
            assertTrue(verifyResult.pass, "No indentation map verification failure, reason: ${verifyResult.reason}!")
        }
    }
    
    @Test
    fun testParseList() {
        val indentedList = jsonParser.parseList<Any?>(FileUtil.getFileContent(javaClass.getResourceAsStream("/listResultIndented.json")))
        
        if (indentedList == null) {
            fail("Failed to parse indented list!")
        } else {
            val verifyResult = JsonTestUtils.verifyList(testData.testList, indentedList) 
            assertTrue(verifyResult.pass, "Indented list verification failure, reason: ${verifyResult.reason}!")
        }
        
        val noIndentationList = jsonParser.parseList<Any?>(FileUtil.getFileContent(javaClass.getResourceAsStream("/listResultNoIndentation.json")))
        
        if (noIndentationList == null) {
            fail("Failed to parse no indentation list")
        } else {
            val verifyResult = JsonTestUtils.verifyList(testData.testList, noIndentationList)
            assertTrue(verifyResult.pass, "No indentation list verification failure, reason: ${verifyResult.reason}!")
        }
    }
}