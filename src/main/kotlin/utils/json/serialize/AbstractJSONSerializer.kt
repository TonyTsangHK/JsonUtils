package utils.json.serialize

import utils.data.MapObjectHelper
import utils.json.core.JSONArray
import utils.json.core.JSONObject

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

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
        serialize(list, bos, true)
        return bos.toByteArray()
    }

    @Throws(IOException::class)
    override fun serializeToBytes(map: Map<String, Any?>): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(map, bos, true)
        return bos.toByteArray()
    }

    @Throws(IOException::class)
    override fun serializeToBytes(jsonArray: JSONArray): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(jsonArray, bos, true)
        return bos.toByteArray()
    }

    @Throws(IOException::class)
    override fun serializeToBytes(json: JSONObject): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(json, bos, true)
        return bos.toByteArray()
    }
    
    @Throws(IOException::class)
    override fun serializeToBytes(mapObject: Any): ByteArray {
        val bos = ByteArrayOutputStream()
        serialize(mapObject, bos, true)
        return bos.toByteArray()
    }

    override fun serialize(mapObject: Any, output: OutputStream) {
        return serialize(MapObjectHelper.toMap(mapObject, true), output)
    }

    override fun serialize(mapObject: Any, output: OutputStream, flushWhenFinished: Boolean) {
        return serialize(MapObjectHelper.toMap(mapObject, true), output, flushWhenFinished)
    }
}
