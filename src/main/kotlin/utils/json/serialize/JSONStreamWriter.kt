package utils.json.serialize

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import utils.stream.SerializeUtils
import java.io.IOException
import java.io.OutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.*

/**
 * JSONStreamWriter handles all the writing process during serialization.
 * 
 * @author Tony Tsang
 */
open class JSONStreamWriter(var output: OutputStream) {
    @Throws(IOException::class)
    protected fun writeToStream(byt: Int) {
        this.output.write(byt)
    }

    @Throws(IOException::class)
    protected fun writeToStream(bytes: ByteArray) {
        this.output.write(bytes)
    }
    
    /**
     * Write an integer value
     * 
     * @param v integer value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeInt(v: Int) {
        val bytes = SerializeUtils.intToByteArray(Math.abs(v))

        val type: Int

        if (v > 0) {
            type = bytes.size + 4
        } else {
            type = bytes.size + 0x14
        }
        
        output.write(type)
        
        output.write(bytes)
    }
    
    /**
     * Write a long value
     * 
     * @param v target long value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeLong(v: Long) {
        var bytes = SerializeUtils.longToByteArray(Math.abs(v))

        // Add padding bytes to avoid long to int degrade issue.
        if (bytes.size < 5) {
            val nbytes = ByteArray(bytes.size+4, {0})
            System.arraycopy(bytes, 0, nbytes, 4, bytes.size)
            bytes = nbytes
        }

        val type: Int

        if (v > 0) {
            type = bytes.size + 4
        } else {
            type = bytes.size + 0x14
        }
        
        output.write(type)
        
        output.write(bytes)
    }
    
    /**
     * Write a single floating point value
     * 
     * @param v target single floating point value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeFloat(v: Float) {
        if (v == 0.0f) {
            output.write(JsonConstants.TYPE_SINGLE_ZERO.toInt())
        } else {
            output.write(JsonConstants.TYPE_SINGLE.toInt())
            
            val bytes = SerializeUtils.floatToByteArray(v)
            
            output.write(bytes)
        }
    }
    
    /**
     * Write a double floating point value
     * 
     * @param v target double floating point value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeDouble(v: Double) {
        if (v == 0.0) {
            output.write(JsonConstants.TYPE_DOUBLE_ZERO.toInt())
        } else {
            output.write(JsonConstants.TYPE_DOUBLE.toInt())
            
            val bytes = SerializeUtils.doubleToByteArray(v)
            
            output.write(bytes)
        }
    }
    
    /**
     * Write a date (formatted as long)
     * 
     * @param date target date value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeDate(date: Date) {
        output.write(JsonConstants.TYPE_DATE.toInt())
        
        output.write(SerializeUtils.longToByteArrayStrict(date.time))
    }
    
    /**
     * Write a string
     * 
     * @param str target string
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeString(str: String) {
        output.write(JsonConstants.TYPE_STRING.toInt())
        
        writeStringImpl(str)
    }

    /**
     * Write binary data
     *
     * @param binaryData binary data to be written to stream
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeBinary(binaryData: ByteArray) {
        output.write(JsonConstants.TYPE_BINARY.toInt())

        writeBinaryImpl(binaryData)
    }
    
    /**
     * Write a string without type byte
     * 
     * @param str target string
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeStringImpl(str: String) {
        if (str.isEmpty()) {
            output.write(0)
            return
        }
        
        val bytes = str.toByteArray(Charset.forName(SerializeUtils.DEFAULT_CHARSET))
        
        writeLength(bytes.size)
        
        output.write(bytes)
    }

    /**
     * Write binary data without type byte
     *
     * @param bytes target binary data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeBinaryImpl(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            output.write(0)
            return
        }

        writeLength(bytes.size)
        
        output.write(bytes)
    }
    
    /**
     * Write a BigInteger value
     * 
     * @param v target BigInteger value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeBigInteger(v: BigInteger) {
        output.write(JsonConstants.TYPE_BIGINTEGER.toInt())
        
        writeBigIntegerImpl(v)
    }
    
    /**
     * Write a BigInteger value without type byte
     * 
     * @param v target BigInteger value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeBigIntegerImpl(v: BigInteger) {
        val bytes = v.toByteArray()
        
        writeLength(bytes.size)
        
        output.write(bytes)
    }
    
    /**
     * Write a BigDecimal value
     * 
     * @param v target BigDecimal value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeBigDecimal(v: BigDecimal) {
        output.write(JsonConstants.TYPE_BIGDECIMAL.toInt())
        
        writeBigDecimalImpl(v)
    }
    
    /**
     * Write a BigDecimal value without type byte
     * 
     * @param v target BigDecimal value
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeBigDecimalImpl(v: BigDecimal) {
        val bi = v.unscaledValue()
        val scale = v.scale()
        
        writeLength(scale)
        
        writeBigIntegerImpl(bi)
    }
    
    /**
     * Write a JSONObject
     * 
     * @param json target JSONObject
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeJSONObject(json: JSONObject) {
        output.write(JsonConstants.TYPE_OBJECT.toInt())
        
        writeLength(json.length())
        
        val iter = json.keys()
        
        while (iter.hasNext()) {
            val key = iter.next()
            
            writeStringImpl(key)
            
            write(json.get(key))
        }
    }
    
    /**
     * Write a map (same as JSONObject)
     * 
     * @param map target map
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeMap(map: Map<String, Any?>) {
        output.write(JsonConstants.TYPE_OBJECT.toInt())
        
        writeLength(map.size)
        
        for (entry in map.entries) {
            writeStringImpl(entry.key)
            writeWithOutJson(entry.value)
        }
    }
    
    /**
     * Write a JSONArray
     * 
     * @param jsonArray target JSONArray
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeJSONArray(jsonArray: JSONArray) {
        output.write(JsonConstants.TYPE_ARRAY.toInt())
        
        writeLength(jsonArray.length())
        
        for (i in 0 until jsonArray.length()) {
            write(jsonArray.get(i))
        }
    }
    
    /**
     * Write a list (same as JSONArray)
     * 
     * @param list target list
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeList(list: List<Any?>){
        output.write(JsonConstants.TYPE_ARRAY.toInt())
        
        writeLength(list.size)
        
        for (obj in list) {
            writeWithOutJson(obj)
        }
    }

    @Throws(IOException::class)
    open fun writeWithOutJson(o: Any?) {
        if (o == null) {
            output.write(JsonConstants.TYPE_NULL.toInt())
        } else if (o is Boolean) {
            if (o) {
                output.write(JsonConstants.TYPE_BOOLEAN_TRUE.toInt())
            } else {
                output.write(JsonConstants.TYPE_BOOLEAN_FALSE.toInt())
            }
        } else if (o is Int) {
            writeInt(o)
        } else if (o is Long) {
            writeLong(o)
        } else if (o is Float) {
            writeFloat(o)
        } else if (o is Double) {
            writeDouble(o)
        } else if (o is BigInteger) {
            writeBigInteger(o)
        } else if (o is BigDecimal) {
            writeBigDecimal(o)
        } else if (o is Date) {
            writeDate(o)
        } else if (o is String) {
            writeString(o.toString())
        } else if (o is Map<*, *>) {
            writeMap(o as Map<String, Any?>)
        } else if (o is List<*>) {
            writeList(o as List<Any?>)
        } else if (o is ByteArray) {
            writeBinary(o)
        } else {
            throw UnsupportedOperationException(
                "Unsupported data type detected: ${o.javaClass.name}, toString: $o"
            )
        }
    }
    
    /**
     * Write an object, object type will be determined before writing, unsupported type will be ignored
     * 
     * @param o target object
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    @SuppressWarnings("unchecked")
    open fun write(o: Any?) {
        if (o == null || o == JSONObject.NULL) {
            output.write(JsonConstants.TYPE_NULL.toInt())
        } else if (o is Boolean) {
            if (o) {
                output.write(JsonConstants.TYPE_BOOLEAN_TRUE.toInt())
            } else {
                output.write(JsonConstants.TYPE_BOOLEAN_FALSE.toInt())
            }
        } else if (o is Int) {
            writeInt(o)
        } else if (o is Long) {
            writeLong(o)
        } else if (o is Float) {
            writeFloat(o)
        } else if (o is Double) {
            writeDouble(o)
        } else if (o is BigInteger) {
            writeBigInteger(o)
        } else if (o is BigDecimal) {
            writeBigDecimal(o)
        } else if (o is Date) {
            writeDate(o)
        } else if (o is String) {
            writeString(o)
        } else if (o is JSONObject) {
            writeJSONObject(o)
        } else if (o is JSONArray) {
            writeJSONArray(o)
        } else if (o is Map<*, *>) {
            writeMap(o as Map<String, Any?>)
        } else if (o is List<*>) {
            writeList(o as List<Any?>)
        } else if (o is ByteArray) {
            writeBinary(o)
        } else {
            throw UnsupportedOperationException(
                "Unsupported data type detected: ${o.javaClass.name}, toString: $o"
            )
        }
    }
    
    /**
     * Write length data, length from 1 to 254 > 1 byte, length greater than or equals to 255 > [255, length in 4 byte]
     * 
     * @param length target length
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun writeLength(length: Int) {
        if (length < 255) {
            output.write(length)
        } else {
            output.write(255)
            output.write(SerializeUtils.intToByteArrayStrict(length))
        }
    }
    
    /**
     * Flush all buffered bytes to be written out.
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    open fun flush() {
        output.flush()
    }
    
    /**
     * Set the underlying output stream
     * 
     * @param out output stream
     */
    open fun setOutputStream(output: OutputStream) {
        this.output = output
    }
}
