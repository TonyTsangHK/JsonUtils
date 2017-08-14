package utils.json.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import utils.json.core.JSONArray;
import utils.json.core.JSONException;
import utils.json.core.JSONObject;

/**
 * JSONSerializer interface.
 * 
 * Defining method required to serialize JSON objects.
 * 
 * @author Tony Tsang
 */
public interface JSONSerializer {
    /**
     * Serialize a JSONObject to the specified output stream
     * 
     * @param json target json object
     * @param out output stream
     * 
     * @throws IOException
     * @throws JSONException
     */
    void serialize(JSONObject json, OutputStream out) throws IOException, JSONException;

    /**
     * Serialize a JSONObject to the specified output stream
     *
     * @param json target json object
     * @param out output stream
     * @param flushWhenFinished whether to flush when finished
     *
     * @throws IOException
     * @throws JSONException
     */
    void serialize(JSONObject json, OutputStream out, boolean flushWhenFinished) throws IOException, JSONException;
    
    /**
     * Serialize a JSONArray to the specified output stream
     * 
     * @param jsonArray target json array
     * @param out output stream
     * 
     * @throws IOException
     * @throws JSONException
     */
    void serialize(JSONArray jsonArray, OutputStream out) throws IOException, JSONException;

    /**
     * Serialize a JSONArray to the specified output stream
     *
     * @param jsonArray target json array
     * @param out output stream
     * @param flushWhenFinised whether to flush when finished
     *
     * @throws IOException
     * @throws JSONException
     */
    void serialize(JSONArray jsonArray, OutputStream out, boolean flushWhenFinised) throws IOException, JSONException;
    
    /**
     * Serialize a list, formated as JSONArray, to the specified output stream
     * 
     * @param list target list
     * @param out output stream
     * 
     * @throws IOException
     */
    void serialize(List<? extends Object> list, OutputStream out) throws IOException;

    /**
     * Serialize a list, formated as JSONArray, to the specified output stream
     *
     * @param list target list
     * @param out output stream
     * @param flushWhenFinished whether to flush when finished
     *
     * @throws IOException
     */
    void serialize(List<? extends Object> list, OutputStream out, boolean flushWhenFinished) throws IOException;
    
    /**
     * Serialize a map, formatted as JSONObject, to the specified output stream
     * 
     * @param map target map
     * @param out output stream
     *
     * @throws IOException
     */
    void serialize(Map<String, ? extends Object> map, OutputStream out) throws IOException;

    /**
     * Serialize a map, formatted as JSONObject, to the specified output stream
     *
     * @param map target map
     * @param out output stream
     * @param flushWhenFinished whether to flush when finished
     *
     * @throws IOException
     */
    void serialize(Map<String, ? extends Object> map, OutputStream out, boolean flushWhenFinished) throws IOException;

    /**
     * Serialize a JSONObject to bytes.
     *
     * @param json target json object
     * @return encoded bytes
     *
     * @throws JSONException
     * @throws IOException
     */
    byte[] serializeToBytes(JSONObject json) throws JSONException, IOException;

    /**
     * Serialize a JSONArray to bytes.
     *
     * @param jsonArray target json array
     * @return encoded bytes
     *
     * @throws JSONException
     * @throws IOException
     */
    byte[] serializeToBytes(JSONArray jsonArray) throws JSONException, IOException;

    /**
     * Serialize a map, formatted as JSONObject, to bytes.
     *
     * @param map target map
     * @return encoded bytes
     *
     * @throws IOException
     */
    byte[] serializeToBytes(Map<String, ? extends Object> map) throws IOException;

    /**
     * Serialize a list, formated as JSONArray, to bytes.
     *
     * @param list target list
     * @return encoded bytes
     *
     * @throws IOException
     */
    byte[] serializeToBytes(List<? extends Object> list) throws IOException;
}
