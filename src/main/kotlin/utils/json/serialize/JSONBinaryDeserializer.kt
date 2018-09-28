package utils.json.serialize

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import java.io.IOException
import java.io.InputStream

/**
 * Deserialize binary stream into JSONObject
 * 
 * @author Tony Tsang
 */
class JSONBinaryDeserializer private constructor() : AbstractJSONDeserializer() {
    companion object {
        private val _instance = JSONBinaryDeserializer()
        
        /**
         * Get a singleton instance of JSONBinaryDeserializer
         * @return instanceof of JSONBinaryDeserializer
         */
        @JvmStatic
        fun getInstance(): JSONBinaryDeserializer {
            return _instance
        }
    }

    private var reader: JSONStreamReader? = null

    @Throws(IOException::class)
    private fun setupReader(input: InputStream) {
        if (reader != null) {
            reader!!.setInputStream(input)
        } else {
            reader = JSONStreamReader(input)
        }
    }

    @Throws(IOException::class)
    override fun deserialize(input: InputStream): JSONObject? {
        setupReader(input)
        if (reader!!.checkHeader(JsonConstants.TYPE_OBJECT.toInt())) {
            return reader!!.readObject()
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    override fun deserializeToJSONArray(input: InputStream): JSONArray? {
        setupReader(input)
        if (reader!!.checkHeader(JsonConstants.TYPE_ARRAY.toInt())) {
            return reader!!.readArray()
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    override fun deserializeToMap(input: InputStream): Map<String, Any?>? {
        setupReader(input)
        if (reader!!.checkHeader(JsonConstants.TYPE_OBJECT.toInt())) {
            return reader!!.readMap()
        } else {
            return null
        }
    }

    @Throws(IOException::class)
    override fun deserializeToList(input: InputStream): List<Any?>? {
        setupReader(input)
        if (reader!!.checkHeader(JsonConstants.TYPE_ARRAY.toInt())) {
            return reader!!.readList()
        } else {
            return null
        }
    }
}
