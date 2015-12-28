package utils.json.parser;

import org.json.*;
import utils.string.StringUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-06-07
 * Time: 12:22
 */
public class JsonFormatter {
    private static JsonFormatter instance = new JsonFormatter();

    private JsonFormatter() {}

    public boolean formatBinary = false;

    public static JsonFormatter getInstance() {
        return instance;
    }

    public String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char b, c = 0;
        int i, len = string.length();
        StringBuilder builder = new StringBuilder(len + 4);
        String t;

        builder.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    builder.append('\\');
                    builder.append(c);
                    break;
                case '/':
                    if (b == '<') {
                        builder.append('\\');
                    }
                    builder.append(c);
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                            (c >= '\u2000' && c < '\u2100')) {
                        t = "000" + Integer.toHexString(c);
                        builder.append("\\u" + t.substring(t.length() - 4));
                    } else {
                        builder.append(c);
                    }
            }
        }
        builder.append('"');
        return builder.toString();
    }

    public String numberToString(Number n) {
        String s = n.toString();
        if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }

    public String valueToStringWithoutJson(Object value) {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof Boolean) {
            return (((Boolean) value).booleanValue())? "true" : "false";
        }
        if (value instanceof byte[]) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes((byte[])value);
            } else {
                return "[Binary data, length: "+((byte[])value).length+"]";
            }
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Date) {
            return quote(JSONTokener.dateFormat.format((Date) value));
        }
        if (value instanceof Map) {
            return format((Map<String, Object>)value);
        }
        if (value instanceof Collection) {
            return format((Collection) value);
        }
        return quote(value.toString());
    }

    public String valueToStringWithJson(Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object o;
            try {
                o = ((JSONString)value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
            if (o instanceof String) {
                return (String)o;
            }
            throw new JSONException("Bad value from toJSONString: " + o);
        }
        if (value instanceof Boolean) {
            return (((Boolean) value).booleanValue())? "true" : "false";
        }
        if (value instanceof byte[]) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes((byte[])value);
            } else {
                return "[Binary data, length: "+((byte[])value).length+"]";
            }
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Date) {
            return quote(JSONTokener.dateFormat.format((Date) value));
        }
        if (value instanceof Map) {
            return format((Map<String, Object>)value);
        }
        if (value instanceof Collection) {
            return format((Collection<? extends Object>)value);
        }
        return quote(value.toString());
    }

    private String valueToStringWithoutJson(Object value, int indentFactor, int indent) {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof byte[]) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes((byte[])value);
            } else {
                return "[Binary data, length: "+((byte[])value).length+"]";
            }
        }
        if (value instanceof Date) {
            return quote(JSONTokener.dateFormat.format((Date) value));
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map) {
            return format((Map<String, ? extends Object>)value, indentFactor, indent);
        }
        if (value instanceof Collection) {
            return format((Collection<? extends Object>)value, indentFactor, indent);
        }
        return quote(value.toString());
    }

    private String valueToStringWithJson(Object value, int indentFactor, int indent) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                Object o = ((JSONString)value).toJSONString();
                if (o instanceof String) {
                    return (String)o;
                }
            }
        } catch (Exception e) {
            /* forget about it */
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Date) {
            return quote(JSONTokener.dateFormat.format((Date)value));
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof byte[]) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes((byte[])value);
            } else {
                return "[Binary data, length: "+((byte[])value).length+"]";
            }
        }
        if (value instanceof JSONObject) {
            return format((JSONObject)value, indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return format((JSONArray)value, indentFactor, indent);
        }
        if (value instanceof Map) {
            return format((Map<String, ? extends Object>)value, indentFactor, indent);
        }
        if (value instanceof Collection) {
            return format((Collection<? extends Object>)value, indentFactor, indent);
        }
        return quote(value.toString());
    }

    public String joinWithoutJson(Collection<? extends Object> collection, String separator) {
        int len = collection.size();
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Object v : collection) {
            if (first) {
                first = false;
            } else {
                builder.append(separator);
            }
            builder.append(valueToStringWithoutJson(v));
        }
        return builder.toString();
    }

    public <T> String joinWithJson(T[] array, String separator) throws JSONException {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(separator);
            }
            builder.append(valueToStringWithJson(array[i]));
        }
        return builder.toString();
    }

    public String joinWithJson(Collection<? extends Object> collection, String separator) throws JSONException {
        int len = collection.size();
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Object v : collection) {
            if (first) {
                first = false;
            } else {
                builder.append(separator);
            }
            builder.append(valueToStringWithJson(v));
        }
        return builder.toString();
    }

    public String joinWithJson(JSONArray jsonArray, String separator) throws JSONException {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            if (i > 0) {
                builder.append(separator);
            }
            builder.append(valueToStringWithJson(jsonArray.get(i)));
        }
        return builder.toString();
    }

    public String format(Collection<? extends Object> collection) {
        return '[' + joinWithoutJson(collection, ",") + ']';
    }

    public String format(Map<String, ? extends Object> map) {
        StringBuilder builder = new StringBuilder("{");

        for (String key : map.keySet()) {
            if (builder.length() > 1) {
                builder.append(',');
            }
            builder.append(quote(key));
            builder.append(':');
            builder.append(valueToStringWithoutJson(map.get(key)));
        }
        builder.append('}');
        return builder.toString();
    }

    public String format(JSONObject json) throws JSONException {
        StringBuilder builder = new StringBuilder("{");

        for (String key : json.keySet()) {
            if (builder.length() > 1) {
                builder.append(',');
            }
            builder.append(quote(key));
            builder.append(':');
            builder.append(valueToStringWithJson(json.get(key)));
        }
        builder.append('}');
        return builder.toString();
    }

    public String format(JSONArray jsonArray) throws JSONException {
        return '[' + joinWithJson(jsonArray, ",") + ']';
    }

    public String format(Map<String, ? extends Object> map, int indentFactor) {
        return format(map, indentFactor, 0);
    }

    private String format(Map<String, ? extends Object> map, int indentFactor, int indent) {
        int j;
        int n = map.size();
        if (n == 0) {
            return "{}";
        }
        Iterator<String> keys = map.keySet().iterator();

        StringBuilder builder = new StringBuilder("{");

        int newindent = indent + indentFactor;

        String o;
        if (n == 1) {
            o = keys.next();
            builder.append(quote(o));
            builder.append(": ");
            builder.append(valueToStringWithoutJson(map.get(o), indentFactor, indent));
        } else {
            while (keys.hasNext()) {
                o = keys.next();
                if (builder.length() > 1) {
                    builder.append(",\n");
                } else {
                    builder.append('\n');
                }
                for (j = 0; j < newindent; j += 1) {
                    builder.append(' ');
                }
                builder.append(quote(o));
                builder.append(": ");
                builder.append(valueToStringWithoutJson(map.get(o), indentFactor, newindent));
            }
            if (builder.length() > 1) {
                builder.append('\n');
                for (j = 0; j < indent; j += 1) {
                    builder.append(' ');
                }
            }
        }
        builder.append('}');
        return builder.toString();
    }

    public String format(JSONObject json, int indentFactor) throws JSONException {
        return format(json, indentFactor, 0);
    }

    private String format(JSONObject json, int indentFactor, int indent) throws JSONException {
        int j;
        int n = json.length();
        if (n == 0) {
            return "{}";
        }
        Iterator<String> keys = json.sortedKeys();

        StringBuilder builder = new StringBuilder("{");

        int newindent = indent + indentFactor;

        String o;
        if (n == 1) {
            o = keys.next();
            builder.append(quote(o.toString()));
            builder.append(": ");
            builder.append(valueToStringWithJson(json.get(o.toString()), indentFactor, indent));
        } else {
            while (keys.hasNext()) {
                o = keys.next();
                if (builder.length() > 1) {
                    builder.append(",\n");
                } else {
                    builder.append('\n');
                }
                for (j = 0; j < newindent; j += 1) {
                    builder.append(' ');
                }
                builder.append(quote(o.toString()));
                builder.append(": ");
                builder.append(valueToStringWithJson(json.get(o.toString()), indentFactor, newindent));
            }
            if (builder.length() > 1) {
                builder.append('\n');
                for (j = 0; j < indent; j += 1) {
                    builder.append(' ');
                }
            }
        }
        builder.append('}');
        return builder.toString();
    }

    public String format(Collection<? extends Object> collection, int indentFactor) {
        return format(collection, indentFactor, 0);
    }

    private String format(Collection<? extends Object> collection, int indentFactor, int indent) {
        int len = collection.size();
        if (len == 0) {
            return "[]";
        }
        int i;
        StringBuilder builder = new StringBuilder("[");

        int newindent = indent + indentFactor;
        builder.append('\n');
        i = 0;
        for (Object v : collection) {
            boolean newLined = i == 0;
            if (i > 0) {
                builder.append(",");

                if (v instanceof JSONObject || v instanceof JSONArray) {
                    builder.append('\n');
                    newLined = true;
                }
            }

            if (newLined) {
                for (int j = 0; j < newindent; j += 1) {
                    builder.append(' ');
                }
            }

            builder.append(valueToStringWithoutJson(v, indentFactor, newindent));
            i++;
        }
        builder.append('\n');
        for (i = 0; i < indent; i += 1) {
            builder.append(' ');
        }

        builder.append(']');
        return builder.toString();
    }

    public String format(JSONArray jsonArray, int indentFactor) throws JSONException {
        return format(jsonArray, indentFactor, 0);
    }

    private String format(JSONArray jsonArray, int indentFactor, int indent) throws JSONException {
        int len = jsonArray.length();
        if (len == 0) {
            return "[]";
        }
        int i;
        StringBuilder builder = new StringBuilder("[");

        int newindent = indent + indentFactor;
        builder.append('\n');
        for (i = 0; i < len; i += 1) {
            Object v = jsonArray.get(i);

            boolean newLined = i == 0;
            if (i > 0) {
                builder.append(",");

                if (v instanceof JSONObject || v instanceof JSONArray) {
                    builder.append('\n');
                    newLined = true;
                }
            }

            if (newLined) {
                for (int j = 0; j < newindent; j += 1) {
                    builder.append(' ');
                }
            }

            builder.append(valueToStringWithJson(v, indentFactor, newindent));
        }
        builder.append('\n');
        for (i = 0; i < indent; i += 1) {
            builder.append(' ');
        }

        builder.append(']');
        return builder.toString();
    }
}
