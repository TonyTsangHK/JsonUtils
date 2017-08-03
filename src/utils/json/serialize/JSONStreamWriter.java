package utils.json.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import utils.json.core.JSONArray;
import utils.json.core.JSONException;
import utils.json.core.JSONObject;
import utils.stream.SerializeUtils;

/**
 * JSONStreamWriter handles all the writing process during serialization.
 * 
 * @author Tony Tsang
 */
public class JSONStreamWriter {
    /**
     * Underlying output stream
     */
    private OutputStream out;
    
    /**
     * Construct a JSONStreamWriter with specified output stream
     * 
     * @param out target output stream
     */
    public JSONStreamWriter(OutputStream out) {
        this.out = out;
    }
    
    protected void writeToStream(int byt) throws IOException {
        this.out.write(byt);
    }
    
    protected void writeToStream(byte[] bytes) throws IOException {
        this.out.write(bytes);
    }
    
    /**
     * Write an integer value
     * 
     * @param v integer value
     * 
     * @throws IOException
     */
    public void writeInt(int v) throws IOException {
        byte[] bytes = SerializeUtils.intToByteArray(Math.abs(v));

        int type;

        if (v > 0) {
            type = bytes.length + 4;
        } else {
            type = bytes.length + 0x14;
        }
        out.write(type);
        
        out.write(bytes);
    }
    
    /**
     * Write an integer value
     * 
     * @param v integer value
     * 
     * @throws IOException
     */
    public void writeInt(Integer v) throws IOException {
        writeInt(v.intValue());
    }
    
    /**
     * Write a long value
     * 
     * @param v target long value
     * 
     * @throws IOException
     */
    public void writeLong(long v) throws IOException {
        byte[] bytes = SerializeUtils.longToByteArray(Math.abs(v));

        // Add padding bytes to avoid long to int degrade issue.
        if (bytes.length < 5) {
            byte[] nbytes = new byte[bytes.length+4];
            nbytes[0] = nbytes[1] = nbytes[2] = nbytes[3] = 0;
            System.arraycopy(bytes, 0, nbytes, 4, bytes.length);
            bytes = nbytes;
        }

        int type;

        if (v > 0) {
            type = bytes.length + 4;
        } else {
            type = bytes.length + 0x14;
        }
        out.write(type);
        
        out.write(bytes);
    }
    
    /**
     * Write a long value
     * 
     * @param v target long value
     * 
     * @throws IOException
     */
    public void writeLong(Long v) throws IOException {
        writeLong(v.longValue());
    }
    
    /**
     * Write a single floating point value
     * 
     * @param v target single floating point value
     * 
     * @throws IOException
     */
    public void writeFloat(float v) throws IOException {
        if (v == 0) {
            out.write(JsonConstants.TYPE_SINGLE_ZERO);
        } else {
            out.write(JsonConstants.TYPE_SINGLE);
            
            byte[] bytes = SerializeUtils.floatToByteArray(v);
            
            out.write(bytes);
        }
    }
    
    /**
     * Write a single floating point value
     * 
     * @param v target single floating point value
     * 
     * @throws IOException
     */
    public void writeFloat(Float v) throws IOException {
        writeFloat(v.floatValue());
    }
    
    /**
     * Write a double floating point value
     * 
     * @param v target double floating point value
     * 
     * @throws IOException
     */
    public void writeDouble(double v) throws IOException {
        if (v == 0) {
            out.write(JsonConstants.TYPE_DOUBLE_ZERO);
        } else {
            out.write(JsonConstants.TYPE_DOUBLE);
            
            byte[] bytes = SerializeUtils.doubleToByteArray(v);
            
            out.write(bytes);
        }
    }
    
    /**
     * Write a double floating poiint value
     * 
     * @param v target double floating point value
     * 
     * @throws IOException
     */
    public void writeDouble(Double v) throws IOException {
        writeDouble(v.doubleValue());
    }
    
    /**
     * Write a date (formatted as long)
     * 
     * @param date target date value
     * 
     * @throws IOException
     */
    public void writeDate(Date date) throws IOException {
        out.write(JsonConstants.TYPE_DATE);
        
        out.write(SerializeUtils.longToByteArrayStrict(date.getTime()));
    }
    
    /**
     * Write a string
     * 
     * @param str target string
     * 
     * @throws IOException
     */
    public void writeString(String str) throws IOException {
        out.write(JsonConstants.TYPE_STRING);
        
        writeStringImpl(str);
    }

    /**
     * Write binary data
     *
     * @
     */
    public void writeBinary(byte[] binaryData) throws IOException {
        out.write(JsonConstants.TYPE_BINARY);

        writeBinaryImpl(binaryData);
    }
    
    /**
     * Write a string without type byte
     * 
     * @param str target string
     * 
     * @throws IOException
     */
    public void writeStringImpl(String str) throws IOException {
        if (str.length() == 0) {
            out.write((byte)0);
            return;
        }
        
        byte[] bytes = str.getBytes(SerializeUtils.DEFAULT_CHARSET);
        
        writeLength(bytes.length);
        
        out.write(bytes);
    }

    /**
     * Write binary data without type byte
     *
     * @param bytes target binary data
     *
     * @throws IOException
     */
    public void writeBinaryImpl(byte[] bytes) throws IOException {
        if (bytes.length == 0) {
            out.write((byte)0);
            return;
        }

        writeLength(bytes.length);
        out.write(bytes);
    }
    
    /**
     * Write a BigInteger value
     * 
     * @param v target BigInteger value
     * 
     * @throws IOException
     */
    public void writeBigInteger(BigInteger v) throws IOException {
        out.write(JsonConstants.TYPE_BIGINTEGER);
        writeBigIntegerImpl(v);
    }
    
    /**
     * Write a BigInteger value without type byte
     * 
     * @param v target BigInteger value
     * 
     * @throws IOException
     */
    public void writeBigIntegerImpl(BigInteger v) throws IOException {
        byte[] bytes = v.toByteArray();
        writeLength(bytes.length);
        out.write(bytes);
    }
    
    /**
     * Write a BigDecimal value
     * 
     * @param v target BigDecimal value
     * 
     * @throws IOException
     */
    public void writeBigDecimal(BigDecimal v) throws IOException {
        out.write(JsonConstants.TYPE_BIGDECIMAL);
        
        writeBigDecimalImpl(v);
    }
    
    /**
     * Write a BigDecimal value without type byte
     * 
     * @param v target BigDecimal value
     * 
     * @throws IOException
     */
    public void writeBigDecimalImpl(BigDecimal v) throws IOException {
        BigInteger bi = v.unscaledValue();
        int scale = v.scale();
        writeLength(scale);
        writeBigIntegerImpl(bi);
    }
    
    /**
     * Write a JSONObject
     * 
     * @param json target JSONObject
     * 
     * @throws JSONException
     * @throws IOException
     */
    public void writeJSONObject(JSONObject json) throws JSONException, IOException {
        out.write(JsonConstants.TYPE_OBJECT);
        
        writeLength(json.length());
        
        Iterator<String> iter = json.keys();
        
        while (iter.hasNext()) {
            String key = iter.next();
            
            writeStringImpl(key);
            write(json.get(key));
        }
    }
    
    /**
     * Write a map (same as JSONObject)
     * 
     * @param map target map
     *
     * @throws IOException
     */
    public void writeMap(Map<String, ?> map) throws IOException {
        out.write(JsonConstants.TYPE_OBJECT);
        
        writeLength(map.size());
        
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            writeStringImpl(entry.getKey());
            writeWithOutJson(entry.getValue());
        }
    }
    
    /**
     * Write a JSONArray
     * 
     * @param jsonArray target JSONArray
     * 
     * @throws JSONException
     * @throws IOException
     */
    public void writeJSONArray(JSONArray jsonArray) 
            throws JSONException, IOException {
        out.write(JsonConstants.TYPE_ARRAY);
        
        writeLength(jsonArray.length());
        
        for (int i = 0; i < jsonArray.length(); i++) {
            write(jsonArray.get(i));
        }
    }
    
    /**
     * Write a list (same as JSONArray)
     * 
     * @param list target list
     *
     * @throws IOException
     */
    public void writeList(List<?> list) throws IOException {
        out.write(JsonConstants.TYPE_ARRAY);
        
        writeLength(list.size());
        
        for (Object obj : list) {
            writeWithOutJson(obj);
        }
    }

    public void writeWithOutJson(Object o) throws IOException {
        if (o == null) {
            out.write(JsonConstants.TYPE_NULL);
        } else if (o instanceof Boolean) {
            Boolean b = (Boolean) o;
            if (b) {
                out.write(JsonConstants.TYPE_BOOLEAN_TRUE);
            } else {
                out.write(JsonConstants.TYPE_BOOLEAN_FALSE);
            }
        } else if (o instanceof Integer) {
            writeInt((Integer)o);
        } else if (o instanceof Long) {
            writeLong((Long)o);
        } else if (o instanceof Float) {
            writeFloat((Float)o);
        } else if (o instanceof Double) {
            writeDouble((Double)o);
        } else if (o instanceof BigInteger) {
            writeBigInteger((BigInteger)o);
        } else if (o instanceof BigDecimal) {
            writeBigDecimal((BigDecimal)o);
        } else if (o instanceof Date) {
            writeDate((Date)o);
        } else if (o instanceof String) {
            writeString(o.toString());
        } else if (o instanceof Map) {
            writeMap((Map<String, Object>)o);
        } else if (o instanceof List) {
            writeList((List<Object>) o);
        } else if (o instanceof byte[]) {
            writeBinary((byte[]) o);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported data type detected: " + o.getClass().getName() + ", toString: " + String.valueOf(o)
            );
        }
    }
    
    /**
     * Write an object, object type will be determined before writing, unsupported type will be ignored
     * 
     * @param o target object
     * 
     * @throws JSONException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void write(Object o) throws JSONException, IOException {
        if (o == null || o == JSONObject.NULL) {
            out.write(JsonConstants.TYPE_NULL);
        } else if (o instanceof Boolean) {
            Boolean b = (Boolean) o;
            if (b) {
                out.write(JsonConstants.TYPE_BOOLEAN_TRUE);
            } else {
                out.write(JsonConstants.TYPE_BOOLEAN_FALSE);
            }
        } else if (o instanceof Integer) {
            writeInt((Integer)o);
        } else if (o instanceof Long) {
            writeLong((Long)o);
        } else if (o instanceof Float) {
            writeFloat((Float)o);
        } else if (o instanceof Double) {
            writeDouble((Double)o);
        } else if (o instanceof BigInteger) {
            writeBigInteger((BigInteger)o);
        } else if (o instanceof BigDecimal) {
            writeBigDecimal((BigDecimal)o);
        } else if (o instanceof Date) {
            writeDate((Date)o);
        } else if (o instanceof String) {
            writeString(o.toString());
        } else if (o instanceof JSONObject) {
            writeJSONObject((JSONObject) o);
        } else if (o instanceof JSONArray) {
            writeJSONArray((JSONArray) o);
        } else if (o instanceof Map) {
            writeMap((Map<String, Object>)o);
        } else if (o instanceof List) {
            writeList((List<Object>) o);
        } else if (o instanceof byte[]) {
            writeBinary((byte[])o);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported data type detected: " + o.getClass().getName() + ", toString: " + String.valueOf(o)
            );
        }
    }
    
    /**
     * Write length data, length from 1 to 254 > 1 byte, length greater than or equals to 255 > [255, length in 4 byte]
     * 
     * @param len target length
     * @throws IOException
     */
    public void writeLength(int len) throws IOException {
        if (len < 255) {
            out.write(len);
        } else {
            out.write(255);
            out.write(SerializeUtils.intToByteArrayStrict(len));
        }
    }
    
    /**
     * Flush all buffered bytes to be written out.
     * 
     * @throws IOException
     */
    public void flush() throws IOException {
        out.flush();
    }
    
    /**
     * Set the underlying output stream
     * @param out output stream
     */
    public void setOutputStream(OutputStream out) {
        this.out = out;
    }
}
