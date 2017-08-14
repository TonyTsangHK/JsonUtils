package utils.json.serialize

import utils.json.core.JSONArray
import utils.json.core.JSONObject

import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-05-30
 * Time: 11:15
 */
abstract class AbstractJSONDeserializer: JSONDeserializer {
    @Throws(IOException::class)
    override fun deserialize(bytes: ByteArray): JSONObject? {
        return deserialize(ByteArrayInputStream(bytes))
    }

    @Throws(IOException::class)
    override fun deserializeToJSONArray(bytes: ByteArray): JSONArray? {
        return deserializeToJSONArray(ByteArrayInputStream(bytes))
    }

    @Throws(IOException::class)
    override fun deserializeToMap(bytes: ByteArray): Map<String, Any?>? {
        return deserializeToMap(ByteArrayInputStream(bytes))
    }

    @Throws(IOException::class)
    override fun deserializeToList(bytes: ByteArray): List<Any?>? {
        return deserializeToList(ByteArrayInputStream(bytes))
    }
}
