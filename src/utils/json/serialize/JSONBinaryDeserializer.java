package utils.json.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import utils.json.core.JSONArray;
import utils.json.core.JSONException;
import utils.json.core.JSONObject;

/**
 * Deserialize binary stream into JSONObject
 * 
 * @author Tony Tsang
 */
public class JSONBinaryDeserializer extends AbstractJSONDeserializer {
    private static JSONBinaryDeserializer instance = new JSONBinaryDeserializer();

    /**
     * Get a singleton instance of JSONBinaryDeserializer
     * @return instanceof of JSONBinaryDeserializer
     */
    public static JSONBinaryDeserializer getInstance() {
        return instance;
    }

    private JSONStreamReader reader;
    
    private JSONBinaryDeserializer() {}
    
    private void setupReader(InputStream in) throws IOException {
        if (reader != null) {
            reader.setInputStream(in);
        } else {
            reader = new JSONStreamReader(in);
        }
    }
    
    @Override
    public JSONObject deserialize(InputStream in) throws IOException, JSONException {
        setupReader(in);
        if (reader.checkHeader(JsonConstants.TYPE_OBJECT)) {
            return reader.readObject();
        } else {
            return null;
        }
    }
    
    @Override
    public JSONArray deserializeToJSONArray(InputStream in) throws IOException, JSONException {
        setupReader(in);
        if (reader.checkHeader(JsonConstants.TYPE_ARRAY)) {
            return reader.readArray();
        } else {
            return null;
        }
    }
    
    @Override
    public Map<String, Object> deserializeToMap(InputStream in) throws IOException {
        setupReader(in);
        if (reader.checkHeader(JsonConstants.TYPE_OBJECT)) {
            return reader.readMap();
        } else {
            return null;
        }
    }
    
    @Override
    public List<Object> deserializeToList(InputStream in) throws IOException {
        setupReader(in);
        if (reader.checkHeader(JsonConstants.TYPE_ARRAY)) {
            return reader.readList();
        } else {
            return null;
        }
    }
}
