package utils.json.serialize.compact;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.json.serialize.AbstractJSONSerializer;

/**
 * Serialize JSON into compact binary format
 * 
 * This serializer is intended to reduce space used for repetitive data (String/BigInteger/BigDecimal/Date),
 * if the data does not contains any repetitive data,
 * the serialized data will always be greater than normal Binary JSON serializer.
 * 
 * Format:
 * - Document starts with BiJ/FMB (optional header bytes)
 * - Header array encodes all String/BigInteger/BigDecimal/Date data for later reference
 *   Format:
 *      [TYPE_REF][length 0~4] < length indicating how many data array encoded (write directly with OutputStream.write(int))
 *          [type byte][encoded data length][encoded data without type byte] < repeat for String, BigInteger, BigDecimal & date data
 * - Content encoding, same as normal binary JSON encoding except for String/BigInteger/BigDecimal/Date.
 * - String, BigInteger, BigDecimal & Date data encoded as TYPE_REF:
 *      [TYPE_REF][1~4 bytes integer]
 * - Object key is encoded as reference index without type byte
 * - Byte length for reference index is determined by total size of header array
 *      1: size <= 0xFF
 *      2: size <= 0xFFFF
 *      3: size <= 0xFFFFFF
 *      4: size > 0xFFFFFF & < 0x7FFFFFFF
 * 
 * e.g.
 * This JSON data:
 * {
 *      "string": "abc",
 *      "bigInteger": 100000000000000000,
 *      "bigDecimal": 100000000000000000.000000000000000001,
 *      "date": "2010-11-11 11:11:11.111",
 *      "array": [
 *          {"string":"abc"},
 *          {"bigInteger": 100000000000000000},
 *          {"bigDecimal": 100000000000000000.000000000000000001},
 *          {"date": "2010-11-11 11:11:11.111"}
 *      ],
 *      "booleanTrue": true,
 *      "booleanFalse": false
 * }
 * 
 * will be encoded as following byte stream:
 * 
 * [BiJ]
 *  [TYPE_REF][4 < type array length]
 *      [TYPE_STRING][8 < component length]
 *          [3 < string length of "abc"]["abc"][5]["array"][12]["booleanFalse"][11]["booleanTrue"][10]["bigDecimal"]
 *          [10]["bigInteger"][4]["date"][5]["string"]
 *      [TYPE_BIGINTEGER][1][big integer byte length][100000000000000000]
 *      [TYPE_BIGDECIMAL][1][scale of big decimal][big integer byte length][100000000000000000000000000000000001]
 *      [TYPE_DATE][1][time millis of 2010-11-11 11:11:11.111]
 *  [TYPE_OBJECT][7]
 *      [7 < index of "string"][TYPE_REF][0 < index of "abc"]
 *      [5][TYPE_REF][8 < index of 100000000000000000]
 *      [4][TYPE_REF][9 < index of 100000000000000000.000000000000000001]
 *      [6][TYPE_REF][10 < index of 2010-11-11 11:11:11.111]
 *      [1][TYPE_ARRAY][4]
 *          [TYPE_OBJECT][1][7][TYPE_REF][0]
 *          [TYPE_OBJECT][1][5][TYPE_REF][8]
 *          [TYPE_OBJECT][1][4][TYPE_REF][9]
 *          [TYPE_OBJECT][1][6][TYPE_REF][10]
 *      [3][TYPE_BOOLEAN_TRUE]
 *      [2][TYPE_BOOLEAN_FALSE]
 *      
 * @author Tony Tsang
 *
 */
public final class JSONCompactBinarySerializer extends AbstractJSONSerializer {
    private static JSONCompactBinarySerializer instance = new JSONCompactBinarySerializer();

    /**
     * Get a singleton instance of JSONCompactBinarySerializer
     * @return instance of JSONCompactBinarySerializer
     */
    public static JSONCompactBinarySerializer getInstance() {
        return instance;
    }

    private JSONCompactStreamWriter writer;
    
    private JSONCompactBinarySerializer() {}
    
    /**
     * Set up writer.
     * 
     * @param out underlying output stream
     * @param jsonInfoHolder JSON info holder, holding intermediate datas
     */
    private void setup(OutputStream out, JSONInfoHolder jsonInfoHolder) {
        if (writer != null) {
            writer.setOutputStream(out);
            writer.setJsonInfoHolder(jsonInfoHolder);
        } else {
            writer = new JSONCompactStreamWriter(out, jsonInfoHolder);
        }
    }

    @Override
    public void serialize(JSONObject json, OutputStream out) throws IOException, JSONException {
        serialize(json, out, true);
    }

    @Override
    public void serialize(JSONObject json, OutputStream out, boolean flushWhenFinished) throws IOException, JSONException {
        setup(out, new JSONInfoHolder(json));
        
        writer.writeJSONInfoHolder();
        
        writer.writeJSONObject(json);

        if (flushWhenFinished) {
            writer.flush();
        }
    }

    @Override
    public void serialize(JSONArray jsonArray, OutputStream out) throws IOException, JSONException {
        serialize(jsonArray, out, true);
    }
    
    @Override
    public void serialize(JSONArray jsonArray, OutputStream out, boolean flushWhenFinished) throws IOException, JSONException {
        setup(out, new JSONInfoHolder(jsonArray));
        
        writer.writeJSONInfoHolder();
        
        writer.writeJSONArray(jsonArray);

        if (flushWhenFinished) {
            writer.flush();
        }
    }

    @Override
    public void serialize(List<?> list, OutputStream out) throws IOException {
        serialize(list, out, true);
    }
    
    @Override
    public void serialize(List<?> list, OutputStream out, boolean flushWhenFinished) throws IOException {
        setup(out, new JSONInfoHolder(list));
        
        writer.writeJSONInfoHolder();
        
        writer.writeList(list);

        if (flushWhenFinished) {
            writer.flush();
        }
    }

    @Override
    public void serialize(Map<String, ?> map, OutputStream out) throws IOException {
        serialize(map, out, true);
    }

    @Override
    public void serialize(Map<String, ?> map, OutputStream out, boolean flushWhenFinished) throws IOException {
        setup(out, new JSONInfoHolder(map));
        
        writer.writeJSONInfoHolder();
        
        writer.writeMap(map);

        if (flushWhenFinished) {
            writer.flush();
        }
    }
}
