package utils.json.serialize;

/**
 * JSON & binary interchange serializer
 * 
 * Binary JSON format defined by Kai Jäger
 * http://kaijaeger.com/articles/introducing-bison-binary-interchange-standard.html
 * 
 * Some modification was made:
 * Adding BigDecimal, BigInteger type, Negative integer type
 * Implementation details are defined below, actually I am not following the specification ... sorry
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Serialize JSON into binary format
 * 
 *  specification:
 *  - Every document consist optional HEADER BYTE: BiJ (Binary Json) / FMB (Binary message format)
 *  - Every element prefixed by an id byte
 *      TYPE                  BYTE   REPRESENT                         BYTE FORMAT
 *      - TYPE_NULL:          0x01   Null element                      [id] no content
 *      - TYPE_BOOLEAN_TRUE:  0x03   Boolean true                      [id] no content
 *      - TYPE_BOOLEAN_FALSE: 0x04   Boolean false                     [id] no content
 *      - TYPE_INT8:          0x05   1 byte positive integer           [id] [1 byte data]
 *      - TYPE_INT16:         0x06   2 byte positive integer           [id] [2 byte data]
 *      - TYPE_INT24:         0x07   3 byte positive integer           [id] [3 byte data]
 *      - TYPE_INT32:         0x08   4 byte positive integer           [id] [4 byte data]
 *      - TYPE_INT40:         0x09   5 byte positive integer           [id] [5 byte data]
 *      - TYPE_INT48:         0x0A   6 byte positive integer           [id] [6 byte data]
 *      - TYPE_INT56:         0x0B   7 byte positive integer           [id] [7 byte data]
 *      - TYPE_INT64:         0x0C   8 byte positive integer           [id] [8 byte data]
 *      As negative always occupy 4 byte for integer & 8 byte for long, negative types is added to avoid this problem.
 *      No need to handle Integer.MIN_VALUE or Long.MIN_VALUE, as -Integer.MIN_VALUE == Integer.MIN_VALUE
 *      - TYPE_N_INT8:        0x15   1 byte negative integer           [id] [1 byte data(same as positive integer)]
 *      - TYPE_N_INT16:       0x16   2 byte negative integer           [id] [2 byte data(same as positive integer)]
 *      - TYPE_N_INT24:       0x17   3 byte negative integer           [id] [3 byte data(same as positive integer)]
 *      - TYPE_N_INT32:       0x18   4 byte negative integer           [id] [4 byte data(same as positive integer)]
 *      - TYPE_N_INT40:       0x19   5 byte negative integer           [id] [5 byte data(same as positive integer)]
 *      - TYPE_N_INT48:       0x1A   6 byte negative integer           [id] [6 byte data(same as positive integer)]
 *      - TYPE_N_INT56:       0x1B   7 byte negative integer           [id] [7 byte data(same as positive integer)]
 *      - TYPE_N_INT64:       0x1C   8 byte negative integer           [id] [8 byte data(same as positive integer)]
 *      - TYPE_BIGINTEGER:    0x20   Big integer                       [id] [byte length (1-254)] [variable length byte datas]
 *                                                                     [id] [255] [4 byte integer data representing byte length] [variable length byte datas] 
 *      - TYPE_BIGDECIMAL:    0x21   Big decimal                       [id] [scale 1-254] [Big integer data encoding format]
 *                                                                     [id] [255] [4 byte integer data representing scale] [Big integer data encoding format]
 *      - TYPE_DATE:          0x22   Date                              [id] [8 byte integer data representing time millis]
 *      - TYPE_SINGLE:        0x0D   Single precision floating number  [id] [4 byte floating point number]
 *      - TYPE_SINGLE_ZERO:   0x1D   Zero Single precision             [id] no content
 *      - TYPE_DOUBLE:        0x0E   Double precision floating number  [id] [8 byte floating point number]
 *      - TYPE_DOUBLE_ZERO:   0x1E   Zero double precision             [id] no content
 *      - TYPE_STRING:        0x0F   String                            [id] [byte length (1-254)] [variable length datas]
 *                                                                     [id] [255] [4 byte integer data representing data length] [variable length datas]
 *                                                                     Maximum string length will be 2^31-1
 *      - TYPE_ARRAY:         0x10   Array                             [id] [member count (1-254)] [member data encodings]
 *                                                                     [id] [255] [4 byte integer data representing array length] [member data encodings]
 *      - TYPE_OBJECT:        0x11   Object                            [id] [member count (1-254)] [String data encoding format] [member data encodings]
 *                                                                     [id] [255] [4 byte integer data representing number of members] [String data encoding format] [member data encodings]
 *      - TYPE_BINARY         0x55   Binary data                       [id] [byte length 1-254] [binary byte data]
 *                                                                     [id] [255] [4 byte int data representing number bytes] [binary byte data]
 */
public final class JSONBinarySerializer extends AbstractJSONSerializer {
    private static JSONBinarySerializer instance = new JSONBinarySerializer();

    /**
     * Get a singleton instance of JSONBinarySerializer
     * @return
     */
    public static JSONBinarySerializer getInstance() {
        return instance;
    }

    /**
     * JSON stream writer
     */
    private JSONStreamWriter writer;
    
    /**
     * Construct a JSON serializer
     */
    private JSONBinarySerializer() {}
    
    private void setupOutputStream(OutputStream out) throws IOException {
        if (writer != null) {
            writer.setOutputStream(out);
        } else {
            writer = new JSONStreamWriter(out);
        }
    }

    @Override
    public void serialize(JSONObject json, OutputStream out) throws IOException, JSONException {
        serialize(json, out, true);
    }
    
    @Override
    public void serialize(JSONObject json, OutputStream out, boolean flushWhenFinished) throws IOException, JSONException {
        setupOutputStream(out);
        writer.writeJSONObject(json);
        if (flushWhenFinished) {
            writer.flush();
        }
    }

    @Override
    public void serialize(Map<String, ? extends Object> map, OutputStream out) throws IOException {
        serialize(map, out, true);
    }

    @Override
    public void serialize(Map<String, ? extends Object> map, OutputStream out, boolean flushWhenFinished) throws IOException {
        setupOutputStream(out);
        writer.writeMap(map);
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
        setupOutputStream(out);
        writer.writeJSONArray(jsonArray);
        if (flushWhenFinished) {
            writer.flush();
        }
    }

    @Override
    public void serialize(List<? extends Object> list, OutputStream out) throws IOException {
        serialize(list, out, true);
    }
    
    @Override
    public void serialize(List<? extends Object> list, OutputStream out, boolean flushWhenFinished) throws IOException {
        setupOutputStream(out);
        writer.writeList(list);
        if (flushWhenFinished) {
            writer.flush();
        }
    }
}
