package utils.json.serialize.compact

import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import utils.json.core.JSONArray
import utils.json.core.JSONException
import utils.json.core.JSONObject

import utils.json.serialize.JSONStreamReader
import utils.json.serialize.JsonConstants

/**
 * Compact binary JSON stream reader
 * 
 * @author Tony Tsang
 *
 */
class JSONCompactStreamReader: JSONStreamReader {
    private lateinit var stringList: MutableList<String>
    private lateinit var bigIntegerList: MutableList<BigInteger>
    private lateinit var bigDecimalList: MutableList<BigDecimal>
    private lateinit var dateList: MutableList<Date>
    
    @Throws(IOException::class)
    constructor(input: InputStream): super(input) {
        initialize(false)
    }

    @Throws(IOException::class)
    constructor(input: InputStream, skipTypeCheck: Boolean): super(input) {
        initialize(skipTypeCheck)
    }

    @Throws(IOException::class)
    override fun setInputStream(input: InputStream) {
        super.setInputStream(input)
        initialize(false)
    }

    @Throws(IOException::class)
    fun setInputStream(input: InputStream, skipTypeCheck: Boolean) {
        super.setInputStream(input)
        initialize(skipTypeCheck)
    }
    
    /**
     * Read a JSONObject
     * 
     * @return JSONObject
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun readObject(): JSONObject {
        val json = JSONObject()
        val memberCount = readLength()
        
        if (memberCount == 0) {
            return json
        }
        
        for (i in 0 until memberCount) {
            val k = readRef().toString()
            val v = readValueFromStream(true)
            
            json.put(k, v)
        }
        
        return json
    }
    
    /**
     * Read a map
     * 
     * @return Map value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun readMap(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        
        val memberCount = readLength()
        
        if (memberCount == 0) {
            return map
        }
        
        for (i in 0 until memberCount) {
            val k = readRef().toString()
            val v = readValueFromStream(false)
            
            map[k] = v
        }
        
        return map
    }

    /**
     * Read a JSONArray
     *
     * @return JSONArray value
     *
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun readArray(): JSONArray {
        val memberCount = readLength()
        
        if (memberCount == 0) {
            return JSONArray()
        } else {
            val array = JSONArray()
            
            for (i in 0 until memberCount) {
                val v = readValueFromStream(true)

                array.put(v)
            }

            return array
        }
    }
    
    /**
     * Read a list
     *
     * @return List value
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun readList(): List<Any?> {
        val memberCount = readLength()
        
        if (memberCount == 0) {
            return ArrayList()
        } else {
            val list = ArrayList<Any?>(memberCount)

            for (i in 0 until memberCount) {
                val v = readValueFromStream(false)
                
                list.add(v)
            }
            
            return list
        }
    }
    
    /**
     * Check whether stream is starting with TYPE_REF
     * 
     * @return check result
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun typeCheck(): Boolean {
        var type = read()
        
        if (type == 'B'.toInt() && read() == 'i'.toInt() && read() == 'J'.toInt()) {
            type = read()
        } else if (type == 'F'.toInt() && read() == 'M'.toInt() && read() == 'B'.toInt()) {
            type = read()
        }
        
        return type == JsonConstants.TYPE_REF.toInt()
    }
    
    /**
     * Read the json intermediate datas holder
     * 
     */
    @Throws(IOException::class)
    private fun initialize(skipTypeCheck: Boolean) {
        stringList = ArrayList()
        bigIntegerList = ArrayList()
        bigDecimalList = ArrayList()
        dateList = ArrayList()
        
        if (!skipTypeCheck && !typeCheck()) {
            return
        }
        
        val len = read()
        
        for (i in 0 until len) {
            val type = read()
            val typeLength = readLength()
            
            if (type == JsonConstants.TYPE_STRING.toInt()) {
                for (j in 0 until typeLength) {
                    stringList.add(readString())
                }
            } else if (type == JsonConstants.TYPE_BIGINTEGER.toInt()) {
                for (j in 0 until typeLength) {
                    bigIntegerList.add(readBigInteger())
                }
            } else if (type == JsonConstants.TYPE_BIGDECIMAL.toInt()) {
                for (j in 0 until typeLength) {
                    bigDecimalList.add(readBigDecimal())
                }
            } else if (type == JsonConstants.TYPE_DATE.toInt()) {
                for (j in 0 until typeLength) {
                    dateList.add(readDate())
                }
            }
        }
    }
    
    private fun byteSize(): Int {
        val s = stringList.size + bigIntegerList.size + bigDecimalList.size + dateList.size
        
        if (s <= 0xFF) {
            return 1
        } else if (s <= 0xFFFF) {
            return 2
        } else if (s <= 0xFFFFFF) {
            return 3
        } else {
            return 4
        }
    }
    
    private fun getRef(refIndex: Int): Any? {
        var localRefIndex = refIndex
        if (localRefIndex < 0) {
            return null
        } else {
            if (localRefIndex < stringList.size) {
                return stringList.get(localRefIndex)
            } else {
                localRefIndex -= stringList.size
                if (localRefIndex < bigIntegerList.size) {
                    return bigIntegerList.get(localRefIndex)
                } else {
                    localRefIndex -= bigIntegerList.size
                    if (localRefIndex < bigDecimalList.size) {
                        return bigDecimalList.get(localRefIndex)
                    } else {
                        localRefIndex -= bigDecimalList.size
                        if (localRefIndex < dateList.size) {
                            return dateList.get(localRefIndex)
                        } else {
                            return null
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Read an object from the reference
     * 
     * @return object from reference
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readRef(): Any? {
        return getRef(readInt(byteSize()))
    }
    
    /**
     * Read a value from the underlying stream, type will be determined by type bytes
     * 
     * @param jsonOutput flag indicating result should be in JSON or Map form
     * @return Value read from the underlying stream
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun readValueFromStream(jsonOutput: Boolean): Any? {
        val id = read().toByte()
        
        try {
            when (id) {
                JsonConstants.TYPE_NULL -> {
                    if (jsonOutput) {
                        return JSONObject.NULL
                    } else {
                        return null
                    }
                }
                JsonConstants.TYPE_REF -> {
                    return readRef()
                }
                JsonConstants.TYPE_BOOLEAN_TRUE -> {
                    return true
                }
                JsonConstants.TYPE_BOOLEAN_FALSE -> {
                    return false
                }
                JsonConstants.TYPE_INT8 -> {
                    return readInt(1)
                }
                JsonConstants.TYPE_INT16 -> {
                    return readInt(2)
                }
                JsonConstants.TYPE_INT24 -> {
                    return readInt(3)
                }
                JsonConstants.TYPE_INT32 -> {
                    return readIntOrLong(4)
                }
                JsonConstants.TYPE_INT40 -> {
                    return readLong(5)
                }
                JsonConstants.TYPE_INT48 -> {
                    return readLong(6)
                }
                JsonConstants.TYPE_INT56 -> {
                    return readLong(7)
                }
                JsonConstants.TYPE_INT64 -> {
                    return readLong(8)
                }
                JsonConstants.TYPE_N_INT8 -> {
                    return -(readInt(1))
                }
                JsonConstants.TYPE_N_INT16 -> {
                    return -(readInt(2))
                }
                JsonConstants.TYPE_N_INT24 -> {
                    return -(readInt(3))
                }
                JsonConstants.TYPE_N_INT32 -> {
                    val obj = readIntOrLong(4)
                    if (obj is Int) {
                        return -obj
                    } else {
                        val l = obj as Long
                        if (-l == Integer.MIN_VALUE.toLong()) {
                            return Integer.MIN_VALUE
                        } else {
                            return -l
                        }
                    }
                }
                JsonConstants.TYPE_N_INT40 -> {
                    return -(readLong(5))
                }
                JsonConstants.TYPE_N_INT48 -> {
                    return -(readLong(6))
                }
                JsonConstants.TYPE_N_INT56 -> {
                    return -(readLong(7))
                }
                JsonConstants.TYPE_N_INT64 -> {
                    return -(readLong(8))
                }
                JsonConstants.TYPE_BIGINTEGER -> {
                    return readBigInteger()
                }
                JsonConstants.TYPE_BIGDECIMAL -> {
                    return readBigDecimal()
                }
                JsonConstants.TYPE_SINGLE -> {
                    return readFloat()
                }
                JsonConstants.TYPE_SINGLE_ZERO -> {
                    return 0F
                }
                JsonConstants.TYPE_DOUBLE -> {
                    return readDouble()
                }
                JsonConstants.TYPE_DOUBLE_ZERO -> {
                    return 0.0
                }
                JsonConstants.TYPE_DATE -> {
                    return readDate()
                }
                JsonConstants.TYPE_STRING -> {
                    return readString()
                }
                JsonConstants.TYPE_ARRAY -> {
                    if (jsonOutput) {
                        return readArray()
                    } else {
                        return readList()
                    }
                }
                JsonConstants.TYPE_OBJECT -> {
                    if (jsonOutput) {
                        return readObject()
                    } else {
                        return readMap()
                    }
                }
                JsonConstants.TYPE_BINARY -> {
                    return readBinary()
                }
                else -> {
                    return null
                }
            }
        } catch (e: Exception) {
            // Return null, if any exception
            return null
        }
    }
}
