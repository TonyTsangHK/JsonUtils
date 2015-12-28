package utils.json.serialize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-05-30
 * Time: 11:15
 */
public abstract class AbstractJSONDeserializer implements JSONDeserializer {
    @Override
    public JSONObject deserialize(byte[] bytes) throws IOException, JSONException {
        return deserialize(new ByteArrayInputStream(bytes));
    }

    @Override
    public JSONArray deserializeToJSONArray(byte[] bytes) throws IOException, JSONException {
        return deserializeToJSONArray(new ByteArrayInputStream(bytes));
    }

    @Override
    public Map<String, Object> deserializeToMap(byte[] bytes) throws IOException {
        return deserializeToMap(new ByteArrayInputStream(bytes));
    }

    @Override
    public List<Object> deserializeToList(byte[] bytes) throws IOException {
        return deserializeToList(new ByteArrayInputStream(bytes));
    }
}
