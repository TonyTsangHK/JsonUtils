package utils.json.serialize.compact;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.json.serialize.JSONStreamWriter;
import utils.json.serialize.JsonConstants;
import utils.stream.SerializeUtils;

/**
 * Compact binary JSON stream writer
 *  
 * @author Tony Tsang
 *
 */
public class JSONCompactStreamWriter extends JSONStreamWriter {
    private JSONInfoHolder jsonInfoHolder;
    
    /**
     * Construct an JSONCompactStreamWriter with specified output stream
     * @param out
     */
    public JSONCompactStreamWriter(OutputStream out, JSONInfoHolder jsonInfoHolder) {
        super(out);
        this.jsonInfoHolder = jsonInfoHolder;
    }
    
    /**
     * Set the json info holder
     * 
     * @param jsonInfoHolder target jsonInfoHolder
     */
    public void setJsonInfoHolder(JSONInfoHolder jsonInfoHolder) {
        this.jsonInfoHolder = jsonInfoHolder;
    }
    
    /**
     * Write JSONInfoHolder as JSONArrays
     * 
     */
    public void writeJSONInfoHolder() throws IOException {
        int l = 0;
        
        if (jsonInfoHolder.stringSize() > 0) {
            l++;
        }
        if (jsonInfoHolder.bigIntegerSize() > 0) {
            l++;
        }
        if (jsonInfoHolder.bigDecimalSize() > 0) {
            l++;
        }
        if (jsonInfoHolder.dateSize() > 0) {
            l++;
        }
        
        writeToStream(JsonConstants.TYPE_REF);
        writeToStream(l);
        
        if (jsonInfoHolder.stringSize() > 0) {
            writeToStream(JsonConstants.TYPE_STRING);
            writeLength(jsonInfoHolder.stringSize());
            
            for (int i = 0; i < jsonInfoHolder.stringSize(); i++) {
                writeStringImpl(jsonInfoHolder.getString(i));
            }
        }
        if (jsonInfoHolder.bigIntegerSize() > 0) {
            writeToStream(JsonConstants.TYPE_BIGINTEGER);
            writeLength(jsonInfoHolder.bigIntegerSize());
            
            for (int i = 0; i < jsonInfoHolder.bigIntegerSize(); i++) {
                writeBigIntegerImpl(jsonInfoHolder.getBigInteger(i));
            }
        }
        if (jsonInfoHolder.bigDecimalSize() > 0) {
            writeToStream(JsonConstants.TYPE_BIGDECIMAL);
            writeLength(jsonInfoHolder.bigDecimalSize());
            
            for (int i = 0; i < jsonInfoHolder.bigDecimalSize(); i++) {
                writeBigDecimalImpl(jsonInfoHolder.getBigDecimal(i));
            }
        }
        if (jsonInfoHolder.dateSize() > 0) {
            writeToStream(JsonConstants.TYPE_DATE);
            writeLength(jsonInfoHolder.dateSize());
            
            for (int i = 0; i < jsonInfoHolder.dateSize(); i++) {
                writeToStream(SerializeUtils.longToByteArrayStrict(jsonInfoHolder.getDate(i).getTime()));
            }
        }
    }
    
    /**
     * Write a JSONObject
     * 
     * @param json target json object
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Override
    public void writeJSONObject(JSONObject json) throws JSONException, IOException {
        writeToStream(JsonConstants.TYPE_OBJECT);
        
        writeLength(json.length());
        
        Iterator<String> iter = json.keys();
        
        while (iter.hasNext()) {
            String key = iter.next();
            
            writeRefImpl(jsonInfoHolder.indexOf(key), jsonInfoHolder.byteSize());
            write(json.get(key));
        }
    }
    
    /**
     * Write a Map
     * 
     * @param map target map
     *
     * @throws IOException
     */
    @Override
    public void writeMap(Map<String, ?> map) throws IOException {
        writeToStream(JsonConstants.TYPE_OBJECT);
        
        writeLength(map.size());
        
        for (String key : map.keySet()) {
            writeRefImpl(jsonInfoHolder.indexOf(key), jsonInfoHolder.byteSize());
            writeWithOutJson(map.get(key));
        }
    }
    
    /**
     * Write a JSONArray
     * 
     * @param jsonArray target json array
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Override
    public void writeJSONArray(JSONArray jsonArray) throws IOException, JSONException{
        writeToStream(JsonConstants.TYPE_ARRAY);
        
        writeLength(jsonArray.length());
        
        for (int i = 0; i < jsonArray.length(); i++) {
            write(jsonArray.get(i));
        }
    }
    
    /**
     * Write a list
     * 
     * @param list target list
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Override
    public void writeList(List<?> list) throws IOException {
        writeToStream(JsonConstants.TYPE_ARRAY);
        
        writeLength(list.size());
        
        for (Object obj : list) {
            writeWithOutJson(obj);
        }
    }
    
    /**
     * Write reference index
     * 
     * @param index reference index
     * @param byteSize target byteSize of the reference index
     * 
     * @throws IOException
     */
    private void writeRef(int index, int byteSize) throws IOException {
        if (index < 0) {
            return;
        }
        writeToStream(JsonConstants.TYPE_REF);
        writeRefImpl(index, byteSize);
    }
    
    /**
     * Write reference index without writing the type byte
     * 
     * @param index reference index
     * @param byteSize target byteSize of the reference index
     * 
     * @throws IOException
     */
    private void writeRefImpl(int index, int byteSize) throws IOException {
        if (index < 0) {
            return;
        }
        byte[] bytes = SerializeUtils.intToByteArrayStrict(index);
        if (byteSize == 4) {
            writeToStream(bytes);
        } else {
            byte[] targetBytes = new byte[byteSize];
            System.arraycopy(bytes, 4-byteSize, targetBytes, 0, byteSize);
            writeToStream(targetBytes);
        }
    }

    public void writeWithOutJson(Object o) throws IOException {
        if (o == null) {
            writeToStream(JsonConstants.TYPE_NULL);
        } else if (o instanceof Boolean) {
            Boolean b = (Boolean) o;
            if (b) {
                writeToStream(JsonConstants.TYPE_BOOLEAN_TRUE);
            } else {
                writeToStream(JsonConstants.TYPE_BOOLEAN_FALSE);
            }
        } else if (o instanceof Integer) {
            writeInt((Integer)o);
        } else if (o instanceof Long) {
            writeLong((Long)o);
        } else if (o instanceof Float) {
            writeFloat((Float)o);
        } else if (o instanceof Double) {
            writeDouble((Double)o);
        } else if (o instanceof BigInteger || o instanceof BigDecimal || o instanceof Date || o instanceof String) {
            writeRef(jsonInfoHolder.indexOf(o), jsonInfoHolder.byteSize());
        } else if (o instanceof Map) {
            writeMap((Map<String, ?>)o);
        } else if (o instanceof List) {
            writeList((List<?>) o);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported data type detected: " + o.getClass().getName() + ", toString: " + String.valueOf(o)
            );
        }
    }
    
    /**
     * Write an object, object type determined before actual writing<br>
     * type of String/BigInteger/BigDecimal/Date will written as TYPE_REF
     * 
     * @param o target object
     * 
     * @throws JSONException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(Object o) throws JSONException, IOException {
        if (o == null || o == JSONObject.NULL) {
            writeToStream(JsonConstants.TYPE_NULL);
        } else if (o instanceof Boolean) {
            Boolean b = (Boolean) o;
            if (b) {
                writeToStream(JsonConstants.TYPE_BOOLEAN_TRUE);
            } else {
                writeToStream(JsonConstants.TYPE_BOOLEAN_FALSE);
            }
        } else if (o instanceof Integer) {
            writeInt((Integer)o);
        } else if (o instanceof Long) {
            writeLong((Long)o);
        } else if (o instanceof Float) {
            writeFloat((Float)o);
        } else if (o instanceof Double) {
            writeDouble((Double)o);
        } else if (o instanceof BigInteger || o instanceof BigDecimal || o instanceof Date || o instanceof String) {
            writeRef(jsonInfoHolder.indexOf(o), jsonInfoHolder.byteSize());
        } else if (o instanceof JSONObject) {
            writeJSONObject((JSONObject) o);
        } else if (o instanceof JSONArray) {
            writeJSONArray((JSONArray) o);
        } else if (o instanceof Map) {
            writeMap((Map<String, ?>)o);
        } else if (o instanceof List) {
            writeList((List<?>) o);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported data type detected: " + o.getClass().getName() + ", toString: " + String.valueOf(o)
            );
        }
    }
}
