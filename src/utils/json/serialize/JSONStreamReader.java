package utils.json.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.stream.SerializeUtils;

/**
 * JSONStreamReader handles all the reading process during serialization.
 * 
 * @author Tony Tsang
 *
 */
public class JSONStreamReader {


    /**
     * Underlying input stream
     */
    private InputStream in;
    
    /**
     * Construct a JSONStream reader with specified input stream
     * 
     * @param in target input stream
     */
    public JSONStreamReader(InputStream in) {
        this.in = in;
    }
    
    /**
     * Set the underlying input stream
     * 
     * @param in target input stream.
     */
    public void setInputStream(InputStream in) throws IOException {
        this.in = in;
    }
    
    /**
     * Read a byte from the underlying input stream, use it with caution.
     * 
     * @return byte value
     * 
     * @throws IOException
     */
    public int read() throws IOException {
        return in.read();
    }
    
    /**
     * Read bytes and fill the data to the specified byte array, use it with caution.
     *
     * @param bytes byte array holder
     *
     * @return length read
     *
     * @throws IOException
     */
    public int readStream(byte[] bytes) throws IOException {
        return SerializeUtils.readStream(in, bytes);
    }
    
    /**
     * Read bytes up to the specified length
     * 
     * @param length data length to be read
     * 
     * @return read data bytes
     * 
     * @throws IOException
     */
    public byte[] readStream(int length) throws IOException {
        return SerializeUtils.readStream(in, length);
    }
    
    /**
     * Read length data (not the available length of the input stream but Binary JSON length data)
     * 
     * @return length
     * 
     * @throws IOException
     */
    public int readLength() throws IOException {
        return readLength(in);
    }
    
    /**
     * Read an integer value
     * 
     * @param byteCount number of bytes of the underlying integer
     * @return integer value
     * 
     * @throws IOException
     */
    public Integer readInt(int byteCount) throws IOException {
        return readInt(in, byteCount);
    }
    
    /**
     * Read an integer value, byte count will be determined by type byte, non integer type will raise IOException
     * 
     * @return integer value
     * 
     * @throws IOException
     */
    public Integer readInt() throws IOException {
        int type = in.read();
        
        switch (type) {
            case JsonConstants.TYPE_INT8:
                return readInt(1);
            case JsonConstants.TYPE_INT16:
                return readInt(2);
            case JsonConstants.TYPE_INT24:
                return readInt(3);
            case JsonConstants.TYPE_INT32:
                return readInt(4);
            case JsonConstants.TYPE_N_INT8:
                return -(readInt(1));
            case JsonConstants.TYPE_N_INT16:
                return -(readInt(2));
            case JsonConstants.TYPE_N_INT24:
                return -(readInt(3));
            case JsonConstants.TYPE_N_INT32:
                return -(readInt(4));
            default:
                throw new IOException(
                        "Type format error, expected int type was not found (" + Integer.toHexString(type) + ")!"
                );
        }
    }
    
    /**
     * Read an integer / long value, type will be determined by byte length and actual decoded value
     * 
     * @param byteCount number of bytes of the underlying data
     * @return value in integer / long
     * 
     * @throws IOException
     */
    public Object readIntOrLong(int byteCount) throws IOException {
        return readIntOrLong(in, byteCount);
    }
    
    /**
     * Read an integer / long value, type will be determined by type byte and actual decoded value
     * Invalid type will raise IOException
     * 
     * @return value in integer / long
     * 
     * @throws IOException
     */
    public Object readIntOrLong() throws IOException {
        int type = in.read();
        
        switch (type) {
            case JsonConstants.TYPE_INT8:
                return readInt(1);
            case JsonConstants.TYPE_INT16:
                return readInt(2);
            case JsonConstants.TYPE_INT24:
                return readInt(3);
            case JsonConstants.TYPE_INT32:
                return readIntOrLong(4);
            case JsonConstants.TYPE_INT40:
                return readLong(5);
            case JsonConstants.TYPE_INT48:
                return readLong(6);
            case JsonConstants.TYPE_INT56:
                return readLong(7);
            case JsonConstants.TYPE_INT64:
                return readLong(8);
            case JsonConstants.TYPE_N_INT8:
                return -(readInt(1));
            case JsonConstants.TYPE_N_INT16:
                return -(readInt(2));
            case JsonConstants.TYPE_N_INT24:
                return -(readInt(3));
            case JsonConstants.TYPE_N_INT32:
                Object obj = readIntOrLong(4);
                if (obj instanceof Integer) {
                    return -(Integer) obj;
                } else {
                    Long l = (Long)obj;
                    if (-l == Integer.MIN_VALUE) {
                        return Integer.MIN_VALUE;
                    } else {
                        return -l;
                    }
                }
            case JsonConstants.TYPE_N_INT40:
                return -(readLong(5));
            case JsonConstants.TYPE_N_INT48:
                return -(readLong(6));
            case JsonConstants.TYPE_N_INT56:
                return -(readLong(7));
            case JsonConstants.TYPE_N_INT64:
                return -(readLong(8));
            default:
                throw new IOException(
                        "Type format error, expected int/long type was not found (" + Integer.toHexString(type) + ")!"
                );
        }
    }
    
    /**
     * Read a long value
     * 
     * @param byteCount number of bytes of the underlying data
     * 
     * @return long value
     * 
     * @throws IOException
     */
    public Long readLong(int byteCount) throws IOException {
        return readLong(in, byteCount);
    }
    
    /**
     * Read a single floating point value
     * 
     * @return single floating point value
     * 
     * @throws IOException
     */
    public Float readFloat() throws IOException {
        return readFloat(in);
    }
    
    /**
     * Read a double floating point value
     * 
     * @return double floating point value
     * 
     * @throws IOException
     */
    public Double readDouble() throws IOException {
        return readDouble(in);
    }
    
    /**
     * Read a date value
     * 
     * @return date value
     * 
     * @throws IOException
     */
    public Date readDate() throws IOException {
        return readDate(in);
    }
    
    /**
     * Read a BigInteger value
     * 
     * @return BigInteger value
     * 
     * @throws IOException
     */
    public BigInteger readBigInteger() throws IOException {
        return readBigInteger(in);
    }
    
    /**
     * Read BigDecimal value
     * 
     * @return BigDecimal value
     * 
     * @throws IOException
     */
    public BigDecimal readBigDecimal() throws IOException {
        return readBigDecimal(in);
    }
    
    /**
     * Read a string
     * 
     * @return String value
     * 
     * @throws IOException
     */
    public String readString() throws IOException {
        return readString(in);
    }

    /**
     * Read binary data
     *
     * @return binary byte array
     *
     * @throws IOException
     */
    public byte[] readBinary() throws IOException {
        return readBinary(in);
    }
    
    /**
     * Read a JSONObject
     * 
     * @return JSONObject
     * 
     * @throws JSONException
     * @throws IOException
     */
    public JSONObject readObject() throws JSONException, IOException {
        return readObject(in);
    }
    
    /**
     * Read a map
     * 
     * @return Map value
     * 
     * @throws IOException
     */
    public Map<String, Object> readMap() throws IOException {
        return readMap(in);
    }
    
    /**
     * Read a JSONArray
     * 
     * @return JSONArray value
     * 
     * @throws JSONException
     * @throws IOException
     */
    public JSONArray readArray() throws JSONException, IOException {
        return readArray(in);
    }
    
    /**
     * Read a list
     * 
     * @return List value
     * 
     * @throws IOException
     */
    public List<Object> readList() throws IOException {
        return readList(in);
    }
    
    /**
     * Read a value from the underlying stream, type will be determined by type bytes
     * 
     * @param jsonOutput flag indicating result should be in JSON or Map form
     * @return Value read from the underlying stream
     * 
     * @throws IOException
     */
    public Object readValueFromStream(boolean jsonOutput) throws IOException {
        return readValueFromStream(in, jsonOutput);
    }
    
    /**
     * Check the header of the underlying stream
     * 
     * @param type first expected type
     * @return first expected match result
     * 
     * @throws IOException
     */
    public boolean checkHeader(int type) throws IOException {
        int byt = in.read();
        if (byt == type) {
            return true;
        } else if (byt == 'B') {
            return in.read() == 'i' && in.read() == 'J' && in.read() == type;
        } else {
            return byt == 'F' && in.read() == 'M' && in.read() == 'B' && in.read() == type;
        }
    }

    public static Integer readInt(InputStream in, int byteCount) throws IOException {
        return SerializeUtils.byteArrayToInt(SerializeUtils.readStream(in, byteCount));
    }

    public static Long readLong(InputStream in, int byteCount) throws IOException {
        return SerializeUtils.byteArrayToLong(SerializeUtils.readStream(in, byteCount));
    }

    public static BigInteger readBigInteger(InputStream in) throws IOException {
        int l = readLength(in);

        return new BigInteger(SerializeUtils.readStream(in, l));
    }

    public static BigDecimal readBigDecimal(InputStream in) throws IOException {
        int scale = readLength(in);

        BigInteger bigInteger = readBigInteger(in);

        return new BigDecimal(bigInteger, scale);
    }

    public static int readLength(InputStream in) throws IOException {
        int len = in.read();

        if (len == 255) {
            len = readInt(in, 4);
        }

        return len;
    }

    public static Object readIntOrLong(InputStream in, int byteCount) throws IOException {
        long v = SerializeUtils.byteArrayToLong(SerializeUtils.readStream(in, byteCount));
        if (v > 0) {
            if (v > Integer.MAX_VALUE) {
                return v;
            } else {
                return (int) v;
            }
        } else {
            if (v < Integer.MIN_VALUE) {
                return v;
            } else {
                return (int) v;
            }
        }
    }

    public static Float readFloat(InputStream in) throws IOException {
        return SerializeUtils.byteArrayToFloat(SerializeUtils.readStream(in, 4));
    }

    public static Double readDouble(InputStream in) throws IOException {
        return SerializeUtils.byteArrayToDouble(SerializeUtils.readStream(in, 8));
    }

    public static Date readDate(InputStream in) throws IOException {
        return new Date(SerializeUtils.byteArrayToLong(SerializeUtils.readStream(in, 8)));
    }

    public static String readString(InputStream in) throws IOException {
        int len = readLength(in);

        if (len == 0) {
            return "";
        }

        return new String(SerializeUtils.readStream(in, len), SerializeUtils.DEFAULT_CHARSET);
    }

    public static byte[] readBinary(InputStream in) throws IOException {
        int len = readLength(in);

        if (len == 0) {
            return new byte[0];
        }

        return SerializeUtils.readStream(in, len);
    }

    public static JSONArray readArray(InputStream in) throws IOException {
        JSONArray jsonArray = new JSONArray();

        int memberCount = readLength(in);

        if (memberCount == 0) {
            return jsonArray;
        }

        for (int i = 0; i < memberCount; i++) {
            Object v = readValueFromStream(in, true);

            jsonArray.put(v);
        }

        return jsonArray;
    }

    public static List<Object> readList(InputStream in) throws IOException {
        List<Object> list = new ArrayList<>();

        int memberCount = readLength(in);

        if (memberCount == 0) {
            return list;
        }

        for (int i = 0; i < memberCount; i++) {
            list.add(readValueFromStream(in, false));
        }

        return list;
    }

    public static JSONObject readObject(InputStream in) throws IOException, JSONException {
        JSONObject json = new JSONObject();
        int memberCount = readLength(in);

        if (memberCount == 0) {
            return json;
        }

        for (int i = 0; i < memberCount; i++) {
            String k = readString(in);
            Object v = readValueFromStream(in, true);

            json.put(k, v);
        }

        return json;
    }

    public static Map<String, Object> readMap(InputStream in) throws IOException {
        Map<String, Object> map = new HashMap<>();

        int memberCount = readLength(in);

        if (memberCount == 0) {
            return map;
        }

        for (int i = 0; i < memberCount; i++) {
            String k = readString(in);
            Object v = readValueFromStream(in, false);

            map.put(k, v);
        }

        return map;
    }

    public static Object readValueFromStream(InputStream in, boolean jsonOutput) throws IOException {
        int id = in.read();

        try {
            switch (id) {
                case JsonConstants.TYPE_NULL:
                    if (jsonOutput) {
                        return JSONObject.NULL;
                    } else {
                        return null;
                    }
                case JsonConstants.TYPE_BOOLEAN_TRUE:
                    return Boolean.TRUE;
                case JsonConstants.TYPE_BOOLEAN_FALSE:
                    return Boolean.FALSE;
                case JsonConstants.TYPE_INT8:
                    return readInt(in, 1);
                case JsonConstants.TYPE_INT16:
                    return readInt(in, 2);
                case JsonConstants.TYPE_INT24:
                    return readInt(in, 3);
                case JsonConstants.TYPE_INT32:
                    return readIntOrLong(in, 4);
                case JsonConstants.TYPE_INT40:
                    return readLong(in, 5);
                case JsonConstants.TYPE_INT48:
                    return readLong(in, 6);
                case JsonConstants.TYPE_INT56:
                    return readLong(in, 7);
                case JsonConstants.TYPE_INT64:
                    return readLong(in, 8);
                case JsonConstants.TYPE_N_INT8:
                    return -(readInt(in, 1));
                case JsonConstants.TYPE_N_INT16:
                    return -(readInt(in, 2));
                case JsonConstants.TYPE_N_INT24:
                    return -(readInt(in, 3));
                case JsonConstants.TYPE_N_INT32:
                    Object obj = readIntOrLong(in, 4);
                    if (obj instanceof Integer) {
                        return -(Integer) obj;
                    } else {
                        Long l = (Long)obj;
                        if (-l == Integer.MIN_VALUE) {
                            return Integer.MIN_VALUE;
                        } else {
                            return -l;
                        }
                    }
                case JsonConstants.TYPE_N_INT40:
                    return -(readLong(in, 5));
                case JsonConstants.TYPE_N_INT48:
                    return -(readLong(in, 6));
                case JsonConstants.TYPE_N_INT56:
                    return -(readLong(in, 7));
                case JsonConstants.TYPE_N_INT64:
                    return -(readLong(in, 8));
                case JsonConstants.TYPE_BIGINTEGER:
                    return readBigInteger(in);
                case JsonConstants.TYPE_BIGDECIMAL:
                    return readBigDecimal(in);
                case JsonConstants.TYPE_SINGLE:
                    return readFloat(in);
                case JsonConstants.TYPE_SINGLE_ZERO:
                    return 0f;
                case JsonConstants.TYPE_DOUBLE:
                    return readDouble(in);
                case JsonConstants.TYPE_DOUBLE_ZERO:
                    return 0d;
                case JsonConstants.TYPE_DATE:
                    return readDate(in);
                case JsonConstants.TYPE_STRING:
                    return readString(in);
                case JsonConstants.TYPE_ARRAY:
                    if (jsonOutput) {
                        return readArray(in);
                    } else {
                        return readList(in);
                    }
                case JsonConstants.TYPE_OBJECT:
                    if (jsonOutput) {
                        return readObject(in);
                    } else {
                        return readMap(in);
                    }
                case JsonConstants.TYPE_BINARY:
                    return readBinary(in);
                default:
                    return null;
            }
        } catch (JSONException je) {
            return null;
        }
    }
}
