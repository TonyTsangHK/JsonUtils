package utils.json.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON deserializer interface
 * 
 * Defining required method for JSON deserializer. 
 * 
 * @author Tony Tsang
 */
public interface JSONDeserializer {
    /**
     * Deserialize JSONObject from the specified input stream<br>
     * 
     * @param in target input stream
     * 
     * @return deserialized JSONObject, null with failed deserialization.
     * 
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject deserialize(InputStream in) throws IOException, JSONException;
    
    /**
     * Deserialize JSONArray from the specified input stream
     * 
     * @param in target input stream
     * @return deserialized JSONArray, null with failed deserialization
     * 
     * @throws IOException
     * @throws JSONException
     */
    public JSONArray deserializeToJSONArray(InputStream in) throws IOException, JSONException;
    
    /**
     * Deserialize list (same as JSONArray) from the specified input stream
     * 
     * @param in target input stream
     * 
     * @return deserialized list, null with failed deserialization
     * 
     * @throws IOException
     * @throws JSONException
     */
    public List<Object> deserializeToList(InputStream in) throws IOException;
    
    /**
     * Deserialize map (same as JSONObject) from the specified input stream
     * 
     * @param in target input stream
     * 
     * @return deserialized map, null with failed deserialization
     * 
     * @throws IOException
     * @throws JSONException
     */
    public Map<String, Object> deserializeToMap(InputStream in) throws IOException;

    /**
     * Deserialize JSONObject from the specified bytes.
     *
     * @param bytes encoded bytes
     * @return deserialized json object
     *
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject deserialize(byte[] bytes) throws IOException, JSONException;

    /**
     * Deserialize JSONArray from the specified bytes.
     *
     * @param bytes encoded bytes
     * @return deserialized json array
     *
     * @throws IOException
     * @throws JSONException
     */
    public JSONArray deserializeToJSONArray(byte[] bytes) throws IOException, JSONException;

    /**
     * Deserialize a Map (same as JSONObject) from the specified bytes
     *
     * @param bytes encoded bytes
     * @return deserialized map
     *
     * @throws IOException
     * @throws JSONException
     */
    public Map<String, Object> deserializeToMap(byte[] bytes) throws IOException;

    /**
     * Deserialize a list (same as JSONArray) from the specified bytes
     *
     * @param bytes encoded bytes
     * @return deserialized list
     *
     * @throws IOException
     * @throws JSONException
     */
    public List<Object> deserializeToList(byte[] bytes) throws IOException;
}
