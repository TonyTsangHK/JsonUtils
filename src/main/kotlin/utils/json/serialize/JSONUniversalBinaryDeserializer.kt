package utils.json.serialize

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import utils.json.serialize.compact.JSONCompactStreamReader
import java.io.IOException
import java.io.InputStream

/**
 * Universal deserializer for compacted or normal binary json stream.
 * 
 * As the first type byte can only be: TYPE_REF / TYPE_OBJECT / TYPE_ARRAY,
 * Reading the first byte is enough to determine the stream type (compact / normal).
 * 
 * @author Tony Tsang
 */
class JSONUniversalBinaryDeserializer private constructor() : AbstractJSONDeserializer() {
    companion object {
        private val _instance = JSONUniversalBinaryDeserializer()
        
        /**
         * Get a singleton instance of JSONUniversalBinaryDeserializer
         *
         * @return instance of JSONUniversalBinaryDeserializer
         */
        @JvmStatic
        fun getInstance(): JSONUniversalBinaryDeserializer {
            return _instance
        }
    }

    @Throws(IOException::class)
    private fun extractFirstTypeByte(input: InputStream): Int {
        var byt = input.read()
        
        if (
            (byt == 'B'.toInt() && input.read() == 'i'.toInt() && input.read() == 'J'.toInt()) || 
            (byt == 'F'.toInt() && input.read() == 'M'.toInt() && input.read() == 'B'.toInt())
        ) {
            byt = input.read()
        }
        
        return byt
    }

    @Throws(IOException::class)
    override fun deserialize(input: InputStream): JSONObject? {
        val typeByte = extractFirstTypeByte(input)
        
        if (typeByte == JsonConstants.TYPE_REF.toInt()) {
            val reader = JSONCompactStreamReader(input, true)
            
            if (reader.read() == JsonConstants.TYPE_OBJECT.toInt()) {
                return reader.readObject()
            }
        } else if (typeByte == JsonConstants.TYPE_OBJECT.toInt()) {
            val reader = JSONStreamReader(input)
            
            return reader.readObject()
        }
        
        return null
    }

    @Throws(IOException::class)
    override fun deserializeToJSONArray(input: InputStream): JSONArray? {
        val typeByte = extractFirstTypeByte(input)
        
        if (typeByte == JsonConstants.TYPE_REF.toInt()) {
            val reader = JSONCompactStreamReader(input, true)
            
            if (reader.read() == JsonConstants.TYPE_ARRAY.toInt()) {
                return reader.readArray()
            }
        } else if (typeByte == JsonConstants.TYPE_ARRAY.toInt()) {
            val reader = JSONStreamReader(input)
            
            return reader.readArray()
        }
        
        return null
    }

    @Throws(IOException::class)
    override fun deserializeToList(input: InputStream): List<Any?>? {
        val typeByte = extractFirstTypeByte(input)
        
        if (typeByte == JsonConstants.TYPE_REF.toInt()) {
            val reader = JSONCompactStreamReader(input, true)
            
            if (reader.read() == JsonConstants.TYPE_ARRAY.toInt()) {
                return reader.readList()
            }
        } else if (typeByte == JsonConstants.TYPE_ARRAY.toInt()) {
            val reader = JSONStreamReader(input)
            
            return reader.readList()
        }
        
        return null
    }

    @Throws(IOException::class)
    override fun deserializeToMap(input: InputStream): Map<String, Any?>? {
        val typeByte = extractFirstTypeByte(input)
        
        if (typeByte == JsonConstants.TYPE_REF.toInt()) {
            val reader = JSONCompactStreamReader(input, true)
            
            if (reader.read() == JsonConstants.TYPE_OBJECT.toInt()) {
                return reader.readMap()
            }
        } else if (typeByte == JsonConstants.TYPE_OBJECT.toInt()) {
            val reader = JSONStreamReader(input)
            
            return reader.readMap()
        }
        
        return null
    }
}