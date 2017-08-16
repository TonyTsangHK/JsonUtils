package testng

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import org.testng.Assert.assertEquals
import utils.test.TestData
import utils.file.FileUtil

import utils.json.parser.JsonFormatter

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-08-15
 * Time: 10:40
 */

/**
 * Test map & list formatting
 * 
 * JSONObject & JSONArray not tested
 */
class TestJsonFormatter {
    private val testData = TestData()
    private lateinit var expectedMapNoIndentation: String
    private lateinit var expectedMapIndentation4: String
    private lateinit var expectedListNoIndentation: String
    private lateinit var expectedListIndentation4: String
    
    private val formatter = JsonFormatter.getInstance()
    
    init {
        formatter.formatBinary = true
    }
    
    @BeforeMethod
    fun setup() {
        // hard coded expected values
        expectedMapNoIndentation = FileUtil.getFileContent(javaClass.getResourceAsStream("/mapResultNoIndentation.json"))
        expectedMapIndentation4 = FileUtil.getFileContent(javaClass.getResourceAsStream("/mapResultIndented.json"))
        
        expectedListNoIndentation = FileUtil.getFileContent(javaClass.getResourceAsStream("/listResultNoIndentation.json"))
        expectedListIndentation4 = FileUtil.getFileContent(javaClass.getResourceAsStream("/listResultIndented.json"))
    }
    
    @Test
    fun testFormattingMap() {
        // no indentation
        val testMapNoIndentation = formatter.format(testData.testMap)
        
        assertEquals(testMapNoIndentation, expectedMapNoIndentation, "Failed to format map with no indentation!")
        
        // Indent by 4
        val testMapIndentation4 = formatter.format(testData.testMap, 4)
        
        assertEquals(testMapIndentation4, expectedMapIndentation4, "Failed to format map with indentation!")
    }
    
    @Test
    fun testFormattingList() {
        // no indentation
        val testListNoIndentation = formatter.format(testData.testList)

        assertEquals(testListNoIndentation, expectedListNoIndentation, "Fail to format list with no indentation!")
        
        // indent by 4
        val testListIndentation4 = formatter.format(testData.testList, 4)

        assertEquals(testListIndentation4, expectedListIndentation4, "Fail to format list with indentation!")
    }
}