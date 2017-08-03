package utils.json.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import utils.json.core.JSONArray;
import utils.json.core.JSONException;
import utils.json.core.JSONObject;

import utils.json.serialize.compact.JSONCompactStreamReader;

/**
 * Universal deserializer for compacted or normal binary json stream.
 * 
 * As the first type byte can only be: TYPE_REF / TYPE_OBJECT / TYPE_ARRAY,
 * Reading the first byte is enough to determine the stream type (compact / normal).
 * 
 * @author Tony Tsang
 */
public class JSONUniversalBinaryDeserializer extends AbstractJSONDeserializer {
    private static JSONUniversalBinaryDeserializer instance = new JSONUniversalBinaryDeserializer();

    /**
     * Get a singleton instance of JSONUniversalBinaryDeserializer
     *
     * @return instance of JSONUniversalBinaryDeserializer
     */
    public static JSONUniversalBinaryDeserializer getInstance() {
        return instance;
    }

    private JSONUniversalBinaryDeserializer() {}

    private int extractFirstTypeByte(InputStream in) throws IOException {
        int byt = in.read();
        
        if ((byt == 'B' && in.read() == 'i' && in.read() == 'J') || (byt == 'F' && in.read() == 'M' && in.read() == 'B')) {
            byt = in.read();
        }
        
        return byt;
    }
    
    @Override
    public JSONObject deserialize(InputStream in) throws IOException, JSONException {
        int typeByte = extractFirstTypeByte(in);
        
        if (typeByte == JsonConstants.TYPE_REF) {
            JSONCompactStreamReader reader = new JSONCompactStreamReader(in, true);
            
            if (reader.read() == JsonConstants.TYPE_OBJECT) {
                return reader.readObject();
            }
        } else if (typeByte == JsonConstants.TYPE_OBJECT) {
            JSONStreamReader reader = new JSONStreamReader(in);
            
            return reader.readObject();
        }
        
        return null;
    }
    
    @Override
    public JSONArray deserializeToJSONArray(InputStream in) throws IOException, JSONException {
        int typeByte = extractFirstTypeByte(in);
        
        if (typeByte == JsonConstants.TYPE_REF) {
            JSONCompactStreamReader reader = new JSONCompactStreamReader(in, true);
            
            if (reader.read() == JsonConstants.TYPE_ARRAY) {
                return reader.readArray();
            }
        } else if (typeByte == JsonConstants.TYPE_ARRAY) {
            JSONStreamReader reader = new JSONStreamReader(in);
            
            return reader.readArray();
        }
        
        return null;
    }
    
    @Override
    public List<Object> deserializeToList(InputStream in) throws IOException {
        int typeByte = extractFirstTypeByte(in);
        
        if (typeByte == JsonConstants.TYPE_REF) {
            JSONCompactStreamReader reader = new JSONCompactStreamReader(in, true);
            
            if (reader.read() == JsonConstants.TYPE_ARRAY) {
                return reader.readList();
            }
        } else if (typeByte == JsonConstants.TYPE_ARRAY) {
            JSONStreamReader reader = new JSONStreamReader(in);
            
            return reader.readList();
        }
        
        return null;
    }
    
    @Override
    public Map<String, Object> deserializeToMap(InputStream in) throws IOException {
        int typeByte = extractFirstTypeByte(in);
        
        if (typeByte == JsonConstants.TYPE_REF) {
            JSONCompactStreamReader reader = new JSONCompactStreamReader(in, true);
            
            if (reader.read() == JsonConstants.TYPE_OBJECT) {
                return reader.readMap();
            }
        } else if (typeByte == JsonConstants.TYPE_OBJECT) {
            JSONStreamReader reader = new JSONStreamReader(in);
            
            return reader.readMap();
        }
        
        return null;
    }
}