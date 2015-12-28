package utils.json.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-06-05
 * Time: 09:53
 */
public class JsonParser {
    private static JsonParser instance = new JsonParser();

    private JsonParser() {}

    public static JsonParser getInstance() {
        return instance;
    }

    public JSONObject parseJson(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException je) {
            return null;
        }
    }

    public JSONArray parseJsonArray(String jsonString) {
        try {
            return new JSONArray(jsonString);
        } catch (JSONException je) {
            return null;
        }
    }

    public <E> Map<String, E> parseMap(String jsonString) {
        try {
            Map<String, E> map = new HashMap<String, E>();
            JSONTokener jsonTokener = new JSONTokener(jsonString);
            jsonTokener.nextObject(map);
            return map;
        } catch (JSONException je) {
            return null;
        }
    }

    public <E> List<E> parseList(String jsonString) {
        try {
            List<E> list = new ArrayList<E>();
            JSONTokener jsonTokener = new JSONTokener(jsonString);
            jsonTokener.nextArray(list);
            return list;
        } catch (JSONException je) {
            return null;
        }
    }
}
