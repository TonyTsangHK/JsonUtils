package utils.json.serialize

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import java.io.IOException
import java.io.OutputStream

/**
 * JSONSerializer interface.
 * 
 * Defining method required to serialize JSON objects.
 * 
 * @author Tony Tsang
 */
interface JSONSerializer {
    /**
     * Serialize a JSONObject to the specified output stream
     * 
     * @param json target json object
     * @param out output stream
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(json: JSONObject, output: OutputStream)

    /**
     * Serialize a JSONObject to the specified output stream
     *
     * @param json target json object
     * @param out output stream
     * @param flushWhenFinished whether to flush when finished
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(json: JSONObject, output: OutputStream, flushWhenFinished: Boolean)
    
    /**
     * Serialize a JSONArray to the specified output stream
     * 
     * @param jsonArray target json array
     * @param out output stream
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(jsonArray: JSONArray, output: OutputStream)

    /**
     * Serialize a JSONArray to the specified output stream
     *
     * @param jsonArray target json array
     * @param out output stream
     * @param flushWhenFinised whether to flush when finished
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(jsonArray: JSONArray, output: OutputStream, flushWhenFinished: Boolean)
    
    /**
     * Serialize a list, formated as JSONArray, to the specified output stream
     * 
     * @param list target list
     * @param out output stream
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(list: List<Any?>, output: OutputStream)

    /**
     * Serialize a list, formated as JSONArray, to the specified output stream
     *
     * @param list target list
     * @param out output stream
     * @param flushWhenFinished whether to flush when finished
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(list: List<Any?>, output: OutputStream, flushWhenFinished: Boolean)
    
    /**
     * Serialize a map, formatted as JSONObject, to the specified output stream
     * 
     * @param map target map
     * @param out output stream
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(map: Map<String, Any?>, output: OutputStream)

    /**
     * Serialize a map, formatted as JSONObject, to the specified output stream
     *
     * @param map target map
     * @param out output stream
     * @param flushWhenFinished whether to flush when finished
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serialize(map: Map<String, Any?>, output: OutputStream, flushWhenFinished: Boolean)

    /**
     * Serialize a map object to the specified output stream
     * 
     * @param mapObject map object
     * @param output output stream
     */
    @Throws(IOException::class)
    fun serialize(mapObject: Any, output: OutputStream)

    /**
     * Serialize a MapObject to the specified output stream
     * 
     * @param mapObject map object
     * @param output output stream
     * @param flushWhenFinished whether to flush when finished
     */
    @Throws(IOException::class)
    fun serialize(mapObject: Any, output: OutputStream, flushWhenFinished: Boolean)

    /**
     * Serialize a JSONObject to bytes.
     *
     * @param json target json object
     * @return encoded bytes
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serializeToBytes(json: JSONObject): ByteArray

    /**
     * Serialize a JSONArray to bytes.
     *
     * @param jsonArray target json array
     * @return encoded bytes
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serializeToBytes(jsonArray: JSONArray): ByteArray

    /**
     * Serialize a map, formatted as JSONObject, to bytes.
     *
     * @param map target map
     * @return encoded bytes
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serializeToBytes(map: Map<String, Any?>): ByteArray

    /**
     * Serialize a list, formated as JSONArray, to bytes.
     *
     * @param list target list
     * @return encoded bytes
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun serializeToBytes(list: List<Any?>): ByteArray

    /**
     * Serialize a MapObject to bytes
     * 
     * @param mapObject map object
     */
    fun serializeToBytes(mapObject: Any): ByteArray
}
