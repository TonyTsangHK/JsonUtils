package utils.json.serialize.compact;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.json.serialize.AbstractJSONDeserializer;
import utils.json.serialize.JsonConstants;

/**
 * Deserialize compact binary JSON stream into JSON object
 * 
 * @author Tony Tsang
 *
 */
public class JSONCompactBinaryDeserializer extends AbstractJSONDeserializer {
    private static JSONCompactBinaryDeserializer instance = new JSONCompactBinaryDeserializer();

    /**
     * Get a singleton instance of JSONCompactBinaryDeserializer
     * @return instance of JSONCompactBinaryDeserializer
     */
    public static JSONCompactBinaryDeserializer getInstance() {
        return instance;
    }

    private JSONCompactStreamReader reader;
    
    private JSONCompactBinaryDeserializer() {}

    /**
     * Setup stream reader
     * 
     * @param in underlying input stream
     */
    private void setup(InputStream in) throws IOException {
        if (reader == null) {
            reader = new JSONCompactStreamReader(in);
        } else {
            reader.setInputStream(in);
        }
    }
    
    @Override
    public JSONObject deserialize(InputStream in) throws IOException, JSONException {
        setup(in);
        
        if (reader.read() == JsonConstants.TYPE_OBJECT) {
            return reader.readObject();
        } else {
            return null;
        }
    }
    
    @Override
    public JSONArray deserializeToJSONArray(InputStream in) throws IOException, JSONException {
        setup(in);
        
        if (reader.read() == JsonConstants.TYPE_ARRAY) {
            return reader.readArray();
        } else {
            return null;
        }
    }

    @Override
    public List<Object> deserializeToList(InputStream in) throws IOException {
        setup(in);
        
        if (reader.read() == JsonConstants.TYPE_ARRAY) {
            return reader.readList();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> deserializeToMap(InputStream in) throws IOException {
        setup(in);
        
        if (reader.read() == JsonConstants.TYPE_OBJECT) {
            return reader.readMap();
        } else {
            return null;
        }
    }


}
