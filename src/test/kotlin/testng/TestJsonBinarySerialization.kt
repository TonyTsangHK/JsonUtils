package testng

import org.testng.Assert.assertTrue
import org.testng.Assert.fail
import org.testng.annotations.Test
import utils.file.FileUtil
import utils.json.serialize.JSONBinaryDeserializer
import utils.json.serialize.JSONBinarySerializer
import utils.json.serialize.JSONUniversalBinaryDeserializer
import utils.json.serialize.compact.JSONCompactBinaryDeserializer
import utils.json.serialize.compact.JSONCompactBinarySerializer
import utils.test.JsonTestUtils
import utils.test.TestData

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-08-15
 * Time: 12:56
 */

/**
 * Test map & list binary serialization
 * 
 * JSONObject & JSONArray not tested
 */
class TestJsonBinarySerialization {
    private val testData = TestData()
    
    @Test
    fun testBinarySerialization() {
        val mapBinaryBytes = FileUtil.getFileContentBytes(javaClass.getResourceAsStream("/mapBinaryJson.bson"))
        val serializedMapBinaryBytes = JSONBinarySerializer.getInstance().serializeToBytes(testData.testMap)
        
        val mapBinaryVerifyResult = JsonTestUtils.verifyByteArray(mapBinaryBytes, serializedMapBinaryBytes)
        
        assertTrue(mapBinaryVerifyResult.pass, "Map binary serialization verification failure!")
        
        val listBinaryBytes = FileUtil.getFileContentBytes(javaClass.getResourceAsStream("/listBinaryJson.bson"))
        val serializedListBinaryBytes = JSONBinarySerializer.getInstance().serializeToBytes(testData.testList)
        
        val listBinaryVerifyResult = JsonTestUtils.verifyByteArray(listBinaryBytes, serializedListBinaryBytes)
        
        assertTrue(listBinaryVerifyResult.pass, "List binary serialization failure!")
    }
    
    @Test
    fun testCompactBinarySerialization() {
        val mapCompactBinaryBytes = FileUtil.getFileContentBytes(javaClass.getResourceAsStream("/mapCompactBinaryJson.bson"))
        val serializedMapCompactBinaryBytes = JSONCompactBinarySerializer.getInstance().serializeToBytes(testData.testMap)

        val mapCompactBinaryVerifyResult = JsonTestUtils.verifyByteArray(mapCompactBinaryBytes, serializedMapCompactBinaryBytes)

        assertTrue(mapCompactBinaryVerifyResult.pass, "Map compact binary serialization verification failure!")

        val listCompactBinaryBytes = FileUtil.getFileContentBytes(javaClass.getResourceAsStream("/listCompactBinaryJson.bson"))
        val serializedListCompactBinaryBytes = JSONCompactBinarySerializer.getInstance().serializeToBytes(testData.testList)

        val listCompactBinaryVerifyResult = JsonTestUtils.verifyByteArray(listCompactBinaryBytes, serializedListCompactBinaryBytes)

        assertTrue(listCompactBinaryVerifyResult.pass, "List compact binary serialization failure!")
    }
    
    @Test
    fun testBinaryDeserialization() {
        val deserializedMap = JSONBinaryDeserializer.getInstance().deserializeToMap(javaClass.getResourceAsStream("/mapBinaryJson.bson"))
        
        if (deserializedMap == null) {
            fail("Map binary deserialize failure!")
        } else {
            val mapVerifyResult = JsonTestUtils.verifyMap(testData.testMap, deserializedMap)
            
            assertTrue(mapVerifyResult.pass, "Deserialized map (binary) verification failure, reason: ${mapVerifyResult.reason}")
        }
        
        val deserializedList = JSONBinaryDeserializer.getInstance().deserializeToList(javaClass.getResourceAsStream("/listBinaryJson.bson"))
        
        if (deserializedList == null) {
            fail("List binary deserialize failure")
        } else {
            val listVerifyResult = JsonTestUtils.verifyList(testData.testList, deserializedList)
            
            assertTrue(listVerifyResult.pass, "Deserialized list (binary) verification failure, reason: ${listVerifyResult.reason}")
        }
    }
    
    @Test
    fun testCompactBinaryDeserialization() {
        val deserializedMap = JSONCompactBinaryDeserializer.getInstance().deserializeToMap(javaClass.getResourceAsStream("/mapCompactBinaryJson.bson"))

        if (deserializedMap == null) {
            fail("Map compact binary deserialize failure!")
        } else {
            val mapVerifyResult = JsonTestUtils.verifyMap(testData.testMap, deserializedMap)

            assertTrue(mapVerifyResult.pass, "Deserialized map (compact binary) verification failure, reason: ${mapVerifyResult.reason}")
        }

        val deserializedList = JSONCompactBinaryDeserializer.getInstance().deserializeToList(javaClass.getResourceAsStream("/listCompactBinaryJson.bson"))

        if (deserializedList == null) {
            fail("List binary deserialize failure")
        } else {
            val listVerifyResult = JsonTestUtils.verifyList(testData.testList, deserializedList)

            assertTrue(listVerifyResult.pass, "Deserialized list (compact binary) verification failure, reason: ${listVerifyResult.reason}")
        }
    }
    
    @Test
    fun testUniversalBinaryDeserialization() {
        val deserializedBinaryMap = JSONUniversalBinaryDeserializer.getInstance().deserializeToMap(javaClass.getResourceAsStream("/mapBinaryJson.bson"))

        if (deserializedBinaryMap == null) {
            fail("Map binary deserialize (universal) failure!")
        } else {
            val mapVerifyResult = JsonTestUtils.verifyMap(testData.testMap, deserializedBinaryMap)

            assertTrue(mapVerifyResult.pass, "Deserialized map (binary - universal) verification failure, reason: ${mapVerifyResult.reason}")
        }

        val deserializedBinaryList = JSONUniversalBinaryDeserializer.getInstance().deserializeToList(javaClass.getResourceAsStream("/listBinaryJson.bson"))

        if (deserializedBinaryList == null) {
            fail("List binary deserialize (universal) failure")
        } else {
            val listVerifyResult = JsonTestUtils.verifyList(testData.testList, deserializedBinaryList)

            assertTrue(listVerifyResult.pass, "Deserialized list (binary - universal) verification failure, reason: ${listVerifyResult.reason}")
        }
        
        val deserializedCompactMap = JSONUniversalBinaryDeserializer.getInstance().deserializeToMap(javaClass.getResourceAsStream("/mapCompactBinaryJson.bson"))

        if (deserializedCompactMap == null) {
            fail("Map compact binary deserialize (universal) failure!")
        } else {
            val mapVerifyResult = JsonTestUtils.verifyMap(testData.testMap, deserializedCompactMap)

            assertTrue(mapVerifyResult.pass, "Deserialized map (compact binary - universal) verification failure, reason: ${mapVerifyResult.reason}")
        }

        val deserializedCompactList = JSONUniversalBinaryDeserializer.getInstance().deserializeToList(javaClass.getResourceAsStream("/listCompactBinaryJson.bson"))

        if (deserializedCompactList == null) {
            fail("List binary deserialize (universal) failure")
        } else {
            val listVerifyResult = JsonTestUtils.verifyList(testData.testList, deserializedCompactList)

            assertTrue(listVerifyResult.pass, "Deserialized list (compact binary - universal) verification failure, reason: ${listVerifyResult.reason}")
        }
    }
}