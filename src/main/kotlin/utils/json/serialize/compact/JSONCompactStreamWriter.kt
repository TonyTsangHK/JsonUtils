package utils.json.serialize.compact

import java.io.IOException
import java.io.OutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

import utils.json.core.JSONArray
import utils.json.core.JSONException
import utils.json.core.JSONObject

import utils.json.serialize.JSONStreamWriter
import utils.json.serialize.JsonConstants
import utils.stream.SerializeUtils

/**
 * Compact binary JSON stream writer
 *  
 * @author Tony Tsang
 *
 */
class JSONCompactStreamWriter: JSONStreamWriter {
    private var jsonInfoHolder: JSONInfoHolder
    
    /**
     * Construct an JSONCompactStreamWriter with specified output stream
     * @param out
     */
    constructor(output: OutputStream, jsonInfoHolder: JSONInfoHolder): super(output) {
        this.jsonInfoHolder = jsonInfoHolder
    }
    
    /**
     * Set the json info holder
     * 
     * @param jsonInfoHolder target jsonInfoHolder
     */
    fun setJsonInfoHolder(jsonInfoHolder: JSONInfoHolder) {
        this.jsonInfoHolder = jsonInfoHolder
    }
    
    /**
     * Write JSONInfoHolder as JSONArrays
     * 
     */
    @Throws(IOException::class)
    fun writeJSONInfoHolder() {
        var infoTypeLength = 0
        
        if (jsonInfoHolder.stringSize() > 0) {
            infoTypeLength++
        }
        if (jsonInfoHolder.bigIntegerSize() > 0) {
            infoTypeLength++
        }
        if (jsonInfoHolder.bigDecimalSize() > 0) {
            infoTypeLength++
        }
        if (jsonInfoHolder.dateSize() > 0) {
            infoTypeLength++
        }
        
        writeToStream(JsonConstants.TYPE_REF.toInt())
        writeToStream(infoTypeLength)
        
        if (jsonInfoHolder.stringSize() > 0) {
            writeToStream(JsonConstants.TYPE_STRING.toInt())
            writeLength(jsonInfoHolder.stringSize())
            
            for (i in 0 .. jsonInfoHolder.stringSize() - 1) {
                writeStringImpl(jsonInfoHolder.getString(i)!!)
            }
        }
        
        if (jsonInfoHolder.bigIntegerSize() > 0) {
            writeToStream(JsonConstants.TYPE_BIGINTEGER.toInt())
            writeLength(jsonInfoHolder.bigIntegerSize())
            
            for (i in 0 .. jsonInfoHolder.bigIntegerSize() - 1) {
                writeBigIntegerImpl(jsonInfoHolder.getBigInteger(i)!!)
            }
        }
        if (jsonInfoHolder.bigDecimalSize() > 0) {
            writeToStream(JsonConstants.TYPE_BIGDECIMAL.toInt())
            writeLength(jsonInfoHolder.bigDecimalSize())
            
            for (i in 0 .. jsonInfoHolder.bigDecimalSize() - 1) {
                writeBigDecimalImpl(jsonInfoHolder.getBigDecimal(i)!!)
            }
        }
        if (jsonInfoHolder.dateSize() > 0) {
            writeToStream(JsonConstants.TYPE_DATE.toInt())
            writeLength(jsonInfoHolder.dateSize())
            
            for (i in 0 .. jsonInfoHolder.dateSize() - 1) {
                writeToStream(SerializeUtils.longToByteArrayStrict(jsonInfoHolder.getDate(i)!!.time))
            }
        }
    }
    
    /**
     * Write a JSONObject
     * 
     * @param json target json object
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun writeJSONObject(json: JSONObject) {
        writeToStream(JsonConstants.TYPE_OBJECT.toInt())
        
        writeLength(json.length())
        
        val iter = json.keys()
        
        while (iter.hasNext()) {
            val key = iter.next()
            
            writeRefImpl(jsonInfoHolder.indexOf(key), jsonInfoHolder.byteSize())
            write(json.get(key))
        }
    }
    
    /**
     * Write a Map
     * 
     * @param map target map
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun writeMap(map: Map<String, Any?>) {
        writeToStream(JsonConstants.TYPE_OBJECT.toInt())
        writeLength(map.size)
        
        for (key in map.keys) {
            writeRefImpl(jsonInfoHolder.indexOf(key), jsonInfoHolder.byteSize())
            writeWithOutJson(map.get(key))
        }
    }
    
    /**
     * Write a JSONArray
     * 
     * @param jsonArray target json array
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Throws(IOException::class)
    override fun writeJSONArray(jsonArray: JSONArray) {
        writeToStream(JsonConstants.TYPE_ARRAY.toInt())
        
        writeLength(jsonArray.length())
        
        for (i in 0 .. jsonArray.length() - 1) {
            write(jsonArray.get(i))
        }
    }
    
    /**
     * Write a list
     * 
     * @param list target list
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun writeList(list: List<Any?>) {
        writeToStream(JsonConstants.TYPE_ARRAY.toInt())
        
        writeLength(list.size)
        
        for (obj in list) {
            writeWithOutJson(obj)
        }
    }
    
    /**
     * Write reference index
     * 
     * @param index reference index
     * @param byteSize target byteSize of the reference index
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeRef(index: Int, byteSize: Int) {
        if (index < 0) {
            return
        }
        writeToStream(JsonConstants.TYPE_REF.toInt())
        writeRefImpl(index, byteSize)
    }
    
    /**
     * Write reference index without writing the type byte
     * 
     * @param index reference index
     * @param byteSize target byteSize of the reference index
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeRefImpl(index: Int, byteSize: Int) {
        if (index < 0) {
            return
        }
        val bytes = SerializeUtils.intToByteArrayStrict(index)
        if (byteSize == 4) {
            writeToStream(bytes)
        } else {
            val targetBytes = ByteArray(byteSize)
            System.arraycopy(bytes, 4-byteSize, targetBytes, 0, byteSize)
            writeToStream(targetBytes)
        }
    }

    @Throws(IOException::class)
    override fun writeWithOutJson(o: Any?) {
        if (o == null) {
            writeToStream(JsonConstants.TYPE_NULL.toInt())
        } else if (o is Boolean) {
            if (o) {
                writeToStream(JsonConstants.TYPE_BOOLEAN_TRUE.toInt())
            } else {
                writeToStream(JsonConstants.TYPE_BOOLEAN_FALSE.toInt())
            }
        } else if (o is Int) {
            writeInt(o)
        } else if (o is Long) {
            writeLong(o)
        } else if (o is Float) {
            writeFloat(o)
        } else if (o is Double) {
            writeDouble(o)
        } else if (o is BigInteger || o is BigDecimal || o is Date || o is String) {
            writeRef(jsonInfoHolder.indexOf(o), jsonInfoHolder.byteSize())
        } else if (o is Map<*, *>) {
            writeMap(o as Map<String, Any?>)
        } else if (o is List<*>) {
            writeList(o as List<Any?>)
        } else {
            throw UnsupportedOperationException(
                "Unsupported data type detected: ${o.javaClass.name}, toString: $o"
            )
        }
    }
    
    /**
     * Write an object, object type determined before actual writing<br>
     * type of String/BigInteger/BigDecimal/Date will written as TYPE_REF
     * 
     * @param o target object
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun write(o: Any?) {
        if (o == null || o == JSONObject.NULL) {
            writeToStream(JsonConstants.TYPE_NULL.toInt())
        } else if (o is Boolean) {
            if (o) {
                writeToStream(JsonConstants.TYPE_BOOLEAN_TRUE.toInt())
            } else {
                writeToStream(JsonConstants.TYPE_BOOLEAN_FALSE.toInt())
            }
        } else if (o is Int) {
            writeInt(o)
        } else if (o is Long) {
            writeLong(o)
        } else if (o is Float) {
            writeFloat(o)
        } else if (o is Double) {
            writeDouble(o)
        } else if (o is BigInteger || o is BigDecimal || o is Date || o is String) {
            writeRef(jsonInfoHolder.indexOf(o), jsonInfoHolder.byteSize())
        } else if (o is JSONObject) {
            writeJSONObject(o)
        } else if (o is JSONArray) {
            writeJSONArray(o)
        } else if (o is Map<*, *>) {
            writeMap(o as Map<String, Any?>)
        } else if (o is List<*>) {
            writeList(o as List<Any?>)
        } else {
            throw UnsupportedOperationException(
                "Unsupported data type detected: ${o.javaClass.name}, toString: $o"
            )
        }
    }
}
