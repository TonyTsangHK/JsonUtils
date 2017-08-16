package utils.test

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-08-15
 * Time: 14:58
 */
object JsonTestUtils {
    fun verifyMap(sourceMap: Map<String, Any?>, targetMap: Map<String, Any?>): VerifyResult {
        if (sourceMap.size == targetMap.size) {
            for (key in sourceMap.keys) {
                if (!targetMap.containsKey(key)) {
                    return VerifyResult.fail("Missing key fromm target map: $key")
                } else {
                    val sourceData = sourceMap[key]
                    val targetData = targetMap[key]
                    
                    val verifyResult = verifyData(sourceData, targetData)
                    
                    if (!verifyResult.pass) {
                        return verifyResult
                    }
                }
            }
            // All map data matches
            return VerifyResult.PASSED
        } else {
            // different map size
            return VerifyResult.fail("Map size mismatch, source map size: ${sourceMap.size}, target map size: ${targetMap.size}")
        }
    }
    
    fun verifyList(sourceList: List<Any?>, targetList: List<Any?>): VerifyResult {
        if (sourceList.size == targetList.size) {
            for (i in sourceList.indices) {
                val sourceData = sourceList[i]
                val targetData = targetList[i]
                
                val verifyResult = verifyData(sourceList[i], targetList[i]) 
                if (!verifyResult.pass) {
                    return verifyResult
                }
            }
            
            // All list data matches
            return VerifyResult.PASSED
        } else {
            // different list size
            return VerifyResult.fail("List size mismatch: source list size: ${sourceList.size}, target list size: ${targetList.size}")
        }
    }
    
    private fun verifyData(sourceData: Any?, targetData: Any?): VerifyResult {
        if (sourceData == null && targetData == null) {
            // null equals
            return VerifyResult.PASSED
        } else {
            if (sourceData == null || targetData == null) {
                val sourceDataType: String
                val targetDataType: String
                if (sourceData != null) {
                    sourceDataType = sourceData.javaClass.simpleName
                } else {
                    sourceDataType = "nullType"
                }
                if (targetData != null) {
                    targetDataType = targetData.javaClass.simpleName
                } else {
                    targetDataType = "nullType"
                }
                // null mismatch
                return VerifyResult.fail("Null mismatch, source: $sourceData, target: $targetData, source type: $sourceDataType, target type: $targetDataType")
            } else {
                // both sourceData & targetData should be not null
                if (
                    sourceData is Boolean || sourceData is Int || sourceData is Long || sourceData is String 
                ) {
                    // general data equals verification
                    if (sourceData == targetData) {
                        return VerifyResult.PASSED
                    } else {
                        return VerifyResult.fail(getDataMismatchReason(sourceData, targetData))
                    }
                } else {
                    if ((sourceData is Float || sourceData is Double) && (targetData is Float || targetData is Double)) {
                        val compareResult: Int
                        
                        if (sourceData is Float) {
                            if (targetData is Float) {
                                compareResult = sourceData.compareTo(targetData)
                            } else {
                                // promote to double for comparison, parser is allowed to parse single precision to double precision floating point number 
                                compareResult = sourceData.toDouble().compareTo(targetData as Double)
                            }
                        } else {
                            if (targetData is Float) {
                                // Possible??
                                compareResult = (sourceData as Double).compareTo(targetData.toDouble())
                            } else {
                                compareResult = (sourceData as Double).compareTo(targetData as Double)
                            }
                        }

                        if (compareResult == 0) {
                            return VerifyResult.PASSED
                        } else {
                            // Result mismatch
                            return VerifyResult.fail(getDataMismatchReason(sourceData, targetData))
                        }
                    } else if (
                        (sourceData is BigInteger && targetData is BigInteger) ||
                        (sourceData is BigDecimal && targetData is BigDecimal) ||
                        (sourceData is Date && targetData is Date)
                    ) {
                        val compareResult: Int
                        
                        // Smart cast is not smart enough to realize sourceData & targetData are of the same comparable type ... 
                        if (sourceData is BigInteger && targetData is BigInteger) {
                            compareResult = sourceData.compareTo(targetData)
                        } else if (sourceData is BigDecimal && targetData is BigDecimal) {
                            compareResult = sourceData.compareTo(targetData)
                        } else if (sourceData is Date && targetData is Date) {
                            compareResult = sourceData.compareTo(targetData)
                        } else {
                            // Actually unreachable code, just to convince the compiler compareResult is already initialized
                            compareResult = -1
                        }
                        
                        if (compareResult == 0) {
                            return VerifyResult.PASSED
                        } else {
                            // Result mismatch
                            return VerifyResult.fail(getDataMismatchReason(sourceData, targetData))
                        }
                    } else if (sourceData is ByteArray && targetData is ByteArray) {
                        return verifyByteArray(sourceData, targetData)
                    } else if (sourceData is List<Any?> && targetData is List<Any?>) {
                        return verifyList(sourceData, targetData)
                    } else if (sourceData is Map<*, *> && targetData is Map<*, *>) {
                        return verifyMap(sourceData as Map<String, Any?>, targetData as Map<String, Any?>)
                    } else {
                        // Type mismatch
                        return VerifyResult.fail(getTypeMismatchReason(sourceData, targetData))
                    }
                }
            }
        }
    }
    
    private fun getTypeMismatchReason(sourceData: Any, targetData: Any): String {
        return "Data type mismatch, source: ${sourceData.javaClass.simpleName}, target: ${targetData.javaClass.simpleName}"
    }
    
    private fun getDataMismatchReason(sourceData: Any, targetData: Any): String {
        return "Data mismatch, source: $sourceData, target: $targetData"
    }
    
    fun verifyByteArray(sourceArray: ByteArray, targetArray: ByteArray): VerifyResult {
        if (sourceArray.size == targetArray.size) {
            for (i in sourceArray.indices) {
                if (sourceArray[i] != targetArray[i]) {
                    // content byte mismatch
                    return VerifyResult.fail("Content byte mismatch")
                }
            }
            // all bytes matches
            return VerifyResult.PASSED
        } else {
            // byte array size mismatch
            return VerifyResult.fail("Byte array size mismatch")
        }
    }
}