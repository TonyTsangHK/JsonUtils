package utils.json.serialize.compact

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import utils.json.serialize.AbstractJSONDeserializer
import utils.json.serialize.JsonConstants
import java.io.IOException
import java.io.InputStream

/**
 * Deserialize compact binary JSON stream into JSON object
 * 
 * @author Tony Tsang
 *
 */
class JSONCompactBinaryDeserializer private constructor() : AbstractJSONDeserializer() {
    companion object {
        private val _instance = JSONCompactBinaryDeserializer()
        
        /**
         * Get a singleton instance of JSONCompactBinaryDeserializer
         * @return instance of JSONCompactBinaryDeserializer
         */
        @JvmStatic
        fun getInstance(): JSONCompactBinaryDeserializer {
            return _instance
        }
    }

    private var reader: JSONCompactStreamReader? = null

    /**
     * Setup stream reader
     * 
     * @param in underlying input stream
     */
    @Throws(IOException::class)
    private fun setup(input: InputStream) {
        if (reader == null) {
            reader = JSONCompactStreamReader(input)
        } else {
            reader!!.setInputStream(input)
        }
    }
    
    @Throws(IOException::class)
    override fun deserialize(input: InputStream): JSONObject? {
        setup(input)
        
        if (reader!!.read() == JsonConstants.TYPE_OBJECT.toInt()) {
            return reader!!.readObject()
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    override fun deserializeToJSONArray(input: InputStream): JSONArray? {
        setup(input)
        
        if (reader!!.read() == JsonConstants.TYPE_ARRAY.toInt()) {
            return reader!!.readArray()
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    override fun deserializeToList(input: InputStream): List<Any?>? {
        setup(input)
        
        if (reader!!.read() == JsonConstants.TYPE_ARRAY.toInt()) {
            return reader!!.readList()
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    override fun deserializeToMap(input: InputStream): Map<String, Any?>?{
        setup(input)
        
        if (reader!!.read() == JsonConstants.TYPE_OBJECT.toInt()) {
            return reader!!.readMap()
        } else {
            return null
        }
    }
}
