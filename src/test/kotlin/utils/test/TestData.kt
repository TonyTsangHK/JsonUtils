package utils.test

import utils.date.SimpleDateUtils
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-08-15
 * Time: 14:06
 */
class TestData {
    val testMap: Map<String, Any?>
    val testList: List<Any?>
    
    init {
        val nullVal = null
        val trueVal = true
        val falseVal = false
        val positiveInt = 1011134567232323258
        val negativeInt = -1022436789123456789
        val positiveLong = 4539999999999999966L
        val negativeLong = -6539999999999999956L

        // Using 2^-7 float and 2^-16 double, this is to avoid losing precision during IEE 754 conversion
        val positiveFloat = 0.0078125f
        val negativeFloat = -0.0078125f
        val positiveDouble = 0.0000152587890625
        val negativeDouble = -0.0000152587890625

        val positiveBigInteger = BigInteger("1111222233334444555566667777888899990000")
        val negativeBigInteger = BigInteger("-9999888877776666555544443333222211110000")

        val positiveBigDecimal = BigDecimal("1111222233334444555566667777888899990000.0000111122223333444455556666777788889999")
        val negativeBigDecimal = BigDecimal("9999888877776666555544443333222211110000.0000999988887777666655554444333322221111")
        
        val dateVal = SimpleDateUtils.getDate(2017, 1, 15)

        val stringVal = "This is a dummy testing string, just for testing purpose!"
        val binaryVal = "Binary data bytes".toByteArray(Charsets.UTF_8)
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
        val map = mapOf(
            "key1" to "object1",
            "key2" to "object2",
            "nestedList" to listOf("element1", "element2", "element3"),
            "nestedMap" to mapOf(
                "nkey1" to "nobject1",
                "nkey2" to "nobject2"
            )
        )

        // Using LinkedMap to ensure data order
        testMap = linkedMapOf(
            "nullVal" to nullVal,
            "trueVal" to trueVal,
            "falseVal" to falseVal,
            "positiveInt" to positiveInt,
            "negativeInt" to negativeInt,
            "positiveLong" to positiveLong,
            "negativeLong" to negativeLong,
            "positiveFloat" to positiveFloat,
            "negativeFloat" to negativeFloat,
            "positiveDouble" to positiveDouble,
            "negativeDouble" to negativeDouble,
            "positiveBigInteger" to positiveBigInteger,
            "negativeBigInteger" to negativeBigInteger,
            "positiveBigDecimal" to positiveBigDecimal,
            "negativeBigDecimal" to negativeBigDecimal,
            "stringVal" to stringVal,
            "dateVal" to dateVal,
            "binaryVal" to binaryVal,
            "list" to list,
            "map" to map
        )

        // Using ArrayList to ensure data order
        testList = arrayListOf(
            nullVal, trueVal, falseVal, positiveInt, negativeInt, positiveLong, negativeLong,
            positiveFloat, negativeFloat, positiveDouble, negativeDouble, positiveBigInteger, negativeBigInteger,
            positiveBigDecimal, negativeBigDecimal, stringVal, dateVal, binaryVal, list, map
        )
    }
}