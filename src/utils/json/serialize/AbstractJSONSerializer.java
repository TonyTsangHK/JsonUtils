package utils.json.serialize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-05-30
 * Time: 11:11
 */
public abstract class AbstractJSONSerializer implements JSONSerializer {
    @Override
    public byte[] serializeToBytes(List<? extends Object> list) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(list, bos);
        return bos.toByteArray();
    }

    @Override
    public byte[] serializeToBytes(Map<String, ? extends Object> map) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(map, bos);
        return bos.toByteArray();
    }

    @Override
    public byte[] serializeToBytes(JSONArray jsonArray) throws JSONException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(jsonArray, bos);
        return bos.toByteArray();
    }

    @Override
    public byte[] serializeToBytes(JSONObject json) throws JSONException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(json, bos);
        return bos.toByteArray();
    }
}
