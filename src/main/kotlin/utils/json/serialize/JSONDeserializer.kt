package utils.json.serialize

import java.io.IOException
import java.io.InputStream

import utils.json.core.JSONArray
import utils.json.core.JSONObject

/**
 * JSON deserializer interface
 * 
 * Defining required method for JSON deserializer. 
 * 
 * @author Tony Tsang
 */
interface JSONDeserializer {
    /**
     * Deserialize JSONObject from the specified input stream<br>
     * 
     * @param in target input stream
     * 
     * @return deserialized JSONObject, null with failed deserialization.
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserialize(input: InputStream): JSONObject?
    
    /**
     * Deserialize JSONArray from the specified input stream
     * 
     * @param in target input stream
     * @return deserialized JSONArray, null with failed deserialization
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserializeToJSONArray(input: InputStream): JSONArray?
    
    /**
     * Deserialize list (same as JSONArray) from the specified input stream
     * 
     * @param in target input stream
     * 
     * @return deserialized list, null with failed deserialization
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserializeToList(input: InputStream): List<Any?>?
    
    /**
     * Deserialize map (same as JSONObject) from the specified input stream
     * 
     * @param in target input stream
     * 
     * @return deserialized map, null with failed deserialization
     * 
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserializeToMap(input: InputStream): Map<String, Any?>?
    
    /**
     * Deserialize JSONObject from the specified bytes.
     *
     * @param bytes encoded bytes
     * @return deserialized json object
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserialize(bytes: ByteArray): JSONObject?

    /**
     * Deserialize JSONArray from the specified bytes.
     *
     * @param bytes encoded bytes
     * @return deserialized json array
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserializeToJSONArray(bytes: ByteArray): JSONArray?

    /**
     * Deserialize a Map (same as JSONObject) from the specified bytes
     *
     * @param bytes encoded bytes
     * @return deserialized map
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deserializeToMap(bytes: ByteArray): Map<String, Any?>?

    /**
     * Deserialize a list (same as JSONArray) from the specified bytes
     *
     * @param bytes encoded bytes
     * @return deserialized list
     *
     * @throws IOException
     */
    fun deserializeToList(bytes: ByteArray): List<Any?>?
}
