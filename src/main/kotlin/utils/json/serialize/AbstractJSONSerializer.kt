package utils.json.serialize

import utils.json.core.JSONArray
import utils.json.core.JSONObject

import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-05-30
 * Time: 11:11
 */
abstract class AbstractJSONSerializer: JSONSerializer {
    @Throws(IOException::class)
    override fun serializeToBytes(list: List<Any?>): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(list, bos)
        return bos.toByteArray()
    }

    @Throws(IOException::class)
    override fun serializeToBytes(map: Map<String, Any?>): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(map, bos)
        return bos.toByteArray()
    }

    @Throws(IOException::class)
    override fun serializeToBytes(jsonArray: JSONArray): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(jsonArray, bos)
        return bos.toByteArray()
    }

    @Throws(IOException::class)
    override fun serializeToBytes(json: JSONObject): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(json, bos)
        return bos.toByteArray()
    }
}
