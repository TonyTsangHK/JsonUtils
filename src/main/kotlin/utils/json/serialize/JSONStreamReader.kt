package utils.json.serialize

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
import utils.stream.SerializeUtils
import java.nio.charset.Charset

/**
 * JSONStreamReader handles all the reading process during serialization.
 * 
 * @author Tony Tsang
 *
 */
open class JSONStreamReader(var input: InputStream) {
    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun readInt(input: InputStream, byteCount: Int): Int = SerializeUtils.byteArrayToInt(SerializeUtils.readStream(input, byteCount))
        

        @JvmStatic
        @Throws(IOException::class)
        fun readLong(input: InputStream, byteCount: Int): Long = SerializeUtils.byteArrayToLong(SerializeUtils.readStream(input, byteCount))

        @JvmStatic
        @Throws(IOException::class)
        fun readBigInteger(input: InputStream): BigInteger {
            val l = readLength(input)

            return BigInteger(SerializeUtils.readStream(input, l))
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readBigDecimal(input: InputStream): BigDecimal {
            val scale = readLength(input)

            val bigInteger = readBigInteger(input)

            return BigDecimal(bigInteger, scale)
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readLength(input: InputStream): Int {
            var len = input.read()

            if (len == 255) {
                len = readInt(input, 4)
            }

            return len
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readIntOrLong(input: InputStream, byteCount: Int): Any {
            val v = SerializeUtils.byteArrayToLong(SerializeUtils.readStream(input, byteCount))
            if (v > 0) {
                if (v > Integer.MAX_VALUE) {
                    return v
                } else {
                    return v.toInt()
                }
            } else {
                if (v < Integer.MIN_VALUE) {
                    return v
                } else {
                    return v.toInt()
                }
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readFloat(input: InputStream): Float = SerializeUtils.byteArrayToFloat(SerializeUtils.readStream(input, 4))

        @Throws(IOException::class)
        fun readDouble(input: InputStream): Double = SerializeUtils.byteArrayToDouble(SerializeUtils.readStream(input, 8))

        @JvmStatic
        @Throws(IOException::class)
        fun readDate(input: InputStream): Date = Date(SerializeUtils.byteArrayToLong(SerializeUtils.readStream(input, 8)))

        @JvmStatic
        @Throws(IOException::class)
        fun readString(input: InputStream): String {
            val len = readLength(input)

            if (len == 0) {
                return ""
            }

            return String(SerializeUtils.readStream(input, len), Charset.forName(SerializeUtils.DEFAULT_CHARSET))
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readBinary(input: InputStream): ByteArray {
            val len = readLength(input)

            if (len == 0) {
                return ByteArray(0)
            }

            return SerializeUtils.readStream(input, len)
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readArray(input: InputStream): JSONArray {
            val jsonArray = JSONArray()

            val memberCount = readLength(input)

            if (memberCount == 0) {
                return jsonArray
            }

            for (i in 0 until memberCount) {
                val v = readValueFromStream(input, true)

                jsonArray.put(v)
            }

            return jsonArray
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readList(input: InputStream): List<Any?> {
            val list = ArrayList<Any?>()

            val memberCount = readLength(input)

            if (memberCount == 0) {
                return list
            }

            for (i in 0 until memberCount) {
                list.add(readValueFromStream(input, false))
            }

            return list
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readObject(input: InputStream): JSONObject {
            val json = JSONObject()
            val memberCount = readLength(input)

            if (memberCount == 0) {
                return json
            }

            for (i in 0 until memberCount) {
                val k = readString(input)
                val v = readValueFromStream(input, true)

                json.put(k, v)
            }

            return json
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readMap(input: InputStream): Map<String, Any?>? {
            val map = HashMap<String, Any?>()

            val memberCount = readLength(input)

            if (memberCount == 0) {
                return map
            }

            for (i in 0 until memberCount) {
                val k = readString(input)
                val v = readValueFromStream(input, false)

                map.put(k, v)
            }

            return map
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readValueFromStream(input: InputStream, jsonOutput: Boolean): Any? {
            val id = input.read().toByte()

            try {
                when (id) {
                    JsonConstants.TYPE_NULL -> {
                        if (jsonOutput) {
                            return JSONObject.NULL
                        } else {
                            return null
                        }
                    }
                    JsonConstants.TYPE_BOOLEAN_TRUE -> {
                        return true
                    }
                    JsonConstants.TYPE_BOOLEAN_FALSE -> {
                        return false
                    }
                    JsonConstants.TYPE_INT8 -> {
                        return readInt(input, 1)
                    }
                    JsonConstants.TYPE_INT16 -> {
                        return readInt(input, 2)
                    }
                    JsonConstants.TYPE_INT24 -> {
                        return readInt(input, 3)
                    }
                    JsonConstants.TYPE_INT32 -> {
                        return readIntOrLong(input, 4)
                    }
                    JsonConstants.TYPE_INT40 -> {
                        return readLong(input, 5)
                    }
                    JsonConstants.TYPE_INT48 -> {
                        return readLong(input, 6)
                    }
                    JsonConstants.TYPE_INT56 -> {
                        return readLong(input, 7)
                    }
                    JsonConstants.TYPE_INT64 -> {
                        return readLong(input, 8)
                    }
                    JsonConstants.TYPE_N_INT8 -> {
                        return -(readInt(input, 1))
                    }
                    JsonConstants.TYPE_N_INT16 -> {
                        return -(readInt(input, 2))
                    }
                    JsonConstants.TYPE_N_INT24 -> {
                        return -(readInt(input, 3))
                    }
                    JsonConstants.TYPE_N_INT32 -> {
                        val obj = readIntOrLong(input,4)
                        if (obj is Int) {
                            return -obj.toInt()
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
                        return -(readLong(input, 5))
                    }
                    JsonConstants.TYPE_N_INT48 -> {
                        return -(readLong(input, 6))
                    }
                    JsonConstants.TYPE_N_INT56 -> {
                        return -(readLong(input, 7))
                    }
                    JsonConstants.TYPE_N_INT64 -> {
                        return -(readLong(input, 8))
                    }
                    JsonConstants.TYPE_BIGINTEGER -> {
                        return readBigInteger(input)
                    }
                    JsonConstants.TYPE_BIGDECIMAL -> {
                        return readBigDecimal(input)
                    }
                    JsonConstants.TYPE_SINGLE -> {
                        return readFloat(input)
                    }
                    JsonConstants.TYPE_SINGLE_ZERO -> {
                        return 0.0F
                    }
                    JsonConstants.TYPE_DOUBLE -> {
                        return readDouble(input)
                    }
                    JsonConstants.TYPE_DOUBLE_ZERO -> {
                        return 0.0
                    }
                    JsonConstants.TYPE_DATE -> {
                        return readDate(input)
                    }
                    JsonConstants.TYPE_STRING -> {
                        return readString(input)
                    }
                    JsonConstants.TYPE_ARRAY -> {
                        if (jsonOutput) {
                            return readArray(input)
                        } else {
                            return readList(input)
                        }
                    }
                    JsonConstants.TYPE_OBJECT -> {
                        if (jsonOutput) {
                            return readObject(input)
                        } else {
                            return readMap(input)
                        }
                    }
                    JsonConstants.TYPE_BINARY -> {
                        return readBinary(input)
                    }
                    else -> {
                        return null
                    }
                }
            } catch (e: Exception) {
                // Return null for any exception
                return null
            }
        }
    }
    
    /**
     * Read a byte from the underlying input stream, use it with caution.
     * 
     * @return byte value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun read(): Int = input.read()
    
    /**
     * Read bytes and fill the data to the specified byte array, use it with caution.
     *
     * @param bytes byte array holder
     *
     * @return length read
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readStream(bytes: ByteArray): Int = SerializeUtils.readStream(input, bytes)
    
    /**
     * Read bytes up to the specified length
     * 
     * @param length data length to be read
     * 
     * @return read data bytes
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readStream(length: Int): ByteArray = SerializeUtils.readStream(input, length)
    
    /**
     * Read length data (not the available length of the input stream but Binary JSON length data)
     * 
     * @return length
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readLength(): Int = readLength(input)
    
    /**
     * Read an integer value
     * 
     * @param byteCount number of bytes of the underlying integer
     * @return integer value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readInt(byteCount: Int): Int = readInt(input, byteCount)
    
    /**
     * Read an integer value, byte count will be determined by type byte, non integer type will raise IOException
     * 
     * @return integer value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readInt(): Int {
        val type = input.read().toByte()
        
        when (type) {
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
                return readInt(4)
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
                return -(readInt(4))
            }
            else -> {
                throw IOException (
                    "Type format error, expected int type was not found (${Integer.toHexString(type.toInt())})!"
                )
            }
        }
    }
    
    /**
     * Read an integer / long value, type will be determined by byte length and actual decoded value
     * 
     * @param byteCount number of bytes of the underlying data
     * @return value in integer / long
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readIntOrLong(byteCount: Int): Any = readIntOrLong(input, byteCount)
    
    /**
     * Read an integer / long value, type will be determined by type byte and actual decoded value
     * Invalid type will raise IOException
     * 
     * @return value in integer / long
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readIntOrLong(): Any {
        val type = input.read().toByte()
        
        when (type) {
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
                    return -obj.toInt()
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
            else -> {
                throw IOException (
                    "Type format error, expected int/long type was not found (${Integer.toHexString(type.toInt())})!"
                )
            }
        }
    }
    
    /**
     * Read a long value
     * 
     * @param byteCount number of bytes of the underlying data
     * 
     * @return long value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readLong(byteCount: Int): Long = readLong(input, byteCount)
    
    /**
     * Read a single floating point value
     * 
     * @return single floating point value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readFloat(): Float = readFloat(input)
    
    /**
     * Read a double floating point value
     * 
     * @return double floating point value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readDouble(): Double = readDouble(input)
    
    /**
     * Read a date value
     * 
     * @return date value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readDate(): Date = readDate(input)
    
    /**
     * Read a BigInteger value
     * 
     * @return BigInteger value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readBigInteger(): BigInteger = readBigInteger(input)
    
    /**
     * Read BigDecimal value
     * 
     * @return BigDecimal value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readBigDecimal(): BigDecimal = readBigDecimal(input)
    
    /**
     * Read a string
     * 
     * @return String value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readString(): String = readString(input)

    /**
     * Read binary data
     *
     * @return binary byte array
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readBinary(): ByteArray = readBinary(input)
    
    /**
     * Read a JSONObject
     * 
     * @return JSONObject
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readObject(): JSONObject = readObject(input)
    
    /**
     * Read a map
     * 
     * @return Map value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readMap(): Map<String, Any?>? = readMap(input)
    
    /**
     * Read a JSONArray
     * 
     * @return JSONArray value
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readArray(): JSONArray = readArray(input)
    
    /**
     * Read a list
     * 
     * @return List value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readList(): List<Any?>? = readList(input)
    
    /**
     * Read a value from the underlying stream, type will be determined by type bytes
     * 
     * @param jsonOutput flag indicating result should be in JSON or Map form
     * @return Value read from the underlying stream
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun readValueFromStream(jsonOutput: Boolean): Any? = readValueFromStream(input, jsonOutput)
    
    /**
     * Check the header of the underlying stream
     * 
     * @param type first expected type
     * @return first expected match result
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun checkHeader(type: Int): Boolean {
        val byt = input.read()
        if (byt == type) {
            return true
        } else if (byt == 'B'.toInt()) {
            return input.read() == 'i'.toInt() && input.read() == 'J'.toInt() && input.read() == type
        } else {
            return byt == 'F'.toInt() && input.read() == 'M'.toInt() && input.read() == 'B'.toInt() && input.read() == type
        }
    }

    /**
     * Set the underlying input stream
     *
     * @param in target input stream.
     */
    @Throws(IOException::class)
    open fun setInputStream(input: InputStream) {
        this.input = input
    }
}
