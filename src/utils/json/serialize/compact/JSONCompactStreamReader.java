package utils.json.serialize.compact;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.json.core.JSONArray;
import utils.json.core.JSONException;
import utils.json.core.JSONObject;

import utils.json.serialize.JSONStreamReader;
import utils.json.serialize.JsonConstants;

/**
 * Compact binary JSON stream reader
 * 
 * @author Tony Tsang
 *
 */
public class JSONCompactStreamReader extends JSONStreamReader {
    private List<String> stringList;
    private List<BigInteger> bigIntegerList;
    private List<BigDecimal> bigDecimalList;
    private List<Date> dateList;
    
    public JSONCompactStreamReader(InputStream in) throws IOException {
        super(in);
        initialize(false);
    }
    
    public JSONCompactStreamReader(InputStream in, boolean skipTypeCheck) throws IOException {
        super(in);
        initialize(skipTypeCheck);
    }
    
    @Override
    public void setInputStream(InputStream in) throws IOException {
        super.setInputStream(in);
        initialize(false);
    }
    
    public void setInputStream(InputStream in, boolean skipTypeCheck) throws IOException {
        super.setInputStream(in);
        initialize(skipTypeCheck);
    }
    
    /**
     * Read a JSONObject
     * 
     * @return JSONObject
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Override
    public JSONObject readObject() throws JSONException, IOException {
        JSONObject json = new JSONObject();
        int memberCount = readLength();
        
        if (memberCount == 0) {
            return json;
        }
        
        for (int i = 0; i < memberCount; i++) {
            String k = readRef().toString();
            Object v = readValueFromStream(true);
            
            json.put(k, v);
        }
        
        return json;
    }
    
    /**
     * Read a map
     * 
     * @return Map value
     * 
     * @throws IOException
     */
    @Override
    public Map<String, Object> readMap() throws IOException {
        Map<String, Object> map = new HashMap<>();
        
        int memberCount = readLength();
        
        if (memberCount == 0) {
            return map;
        }
        
        for (int i = 0; i < memberCount; i++) {
            String k = readRef().toString();
            Object v = readValueFromStream(false);
            
            map.put(k, v);
        }
        
        return map;
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
        int memberCount = readLength();
        
        if (memberCount == 0) {
            return new JSONArray();
        } else {
            JSONArray array = new JSONArray();
            
            for (int i = 0; i < memberCount; i++) {
                Object v = readValueFromStream(true);

                array.put(v);
            }

            return array;
        }
    }
    
    /**
     * Read a list
     *
     * @return List value
     *
     * @throws IOException
     */
    public List<Object> readList() throws IOException {
        int memberCount = readLength();
        
        if (memberCount == 0) {
            return new ArrayList<>();
        } else {
            List<Object> list = new ArrayList<>(memberCount);

            for (int i = 0; i < memberCount; i++) {
                Object v = readValueFromStream(false);
                
                list.add(v);
            }
            
            return list;
        }
    }
    
    /**
     * Check whether stream is starting with TYPE_REF
     * 
     * @return check result
     * 
     * @throws IOException
     */
    private boolean typeCheck() throws IOException {
        int type = read();
        
        if (type == 'B' && read() == 'i' && read() == 'J') {
            type = read();
        } else if (type == 'F' && read() == 'M' && read() == 'B') {
            type = read();
        }
        
        return type == JsonConstants.TYPE_REF;
    }
    
    /**
     * Read the json intermediate datas holder
     * 
     */
    private void initialize(boolean skipTypeCheck) throws IOException {
        stringList = new ArrayList<>();
        bigIntegerList = new ArrayList<>();
        bigDecimalList = new ArrayList<>();
        dateList = new ArrayList<>();
        
        if (!skipTypeCheck && !typeCheck()) {
            return;
        }
        
        int len = read();
        
        for (int i = 0; i < len; i++) {
            int type = read();
            int typeLength = readLength();
            
            if (type == JsonConstants.TYPE_STRING) {
                for (int j = 0; j < typeLength; j++) {
                    stringList.add(readString());
                }
            } else if (type == JsonConstants.TYPE_BIGINTEGER) {
                for (int j = 0; j < typeLength; j++) {
                    bigIntegerList.add(readBigInteger());
                }
            } else if (type == JsonConstants.TYPE_BIGDECIMAL) {
                for (int j = 0; j < typeLength; j++) {
                    bigDecimalList.add(readBigDecimal());
                }
            } else if (type == JsonConstants.TYPE_DATE) {
                for (int j = 0; j < typeLength; j++) {
                    dateList.add(readDate());
                }
            }
        }
    }
    
    private int byteSize() {
        int s = stringList.size() + bigIntegerList.size() + bigDecimalList.size() + dateList.size();
        
        if (s <= 0xFF) {
            return 1;
        } else if (s <= 0xFFFF) {
            return 2;
        } else if (s <= 0xFFFFFF) {
            return 3;
        } else {
            return 4;
        }
    }
    
    private Object getRef(int refIndex) {
        if (refIndex < 0) {
            return null;
        } else {
            if (refIndex < stringList.size()) {
                return stringList.get(refIndex);
            } else {
                refIndex -= stringList.size();
                if (refIndex < bigIntegerList.size()) {
                    return bigIntegerList.get(refIndex);
                } else {
                    refIndex -= bigIntegerList.size();
                    if (refIndex < bigDecimalList.size()) {
                        return bigDecimalList.get(refIndex);
                    } else {
                        refIndex -= bigDecimalList.size();
                        if (refIndex < dateList.size()) {
                            return dateList.get(refIndex);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Read an object from the reference
     * 
     * @return object from reference
     * 
     * @throws IOException
     */
    public Object readRef() throws IOException {
        return getRef(readInt(byteSize()));
    }
    
    /**
     * Read a value from the underlying stream, type will be determined by type bytes
     * 
     * @param jsonOutput flag indicating result should be in JSON or Map form
     * @return Value read from the underlying stream
     * 
     * @throws IOException
     */
    @Override
    public Object readValueFromStream(boolean jsonOutput) throws IOException {
        int id = read();
        
        try {
            switch (id) {
                case JsonConstants.TYPE_NULL:
                    return JSONObject.NULL;
                case JsonConstants.TYPE_REF:
                    return readRef();
                case JsonConstants.TYPE_BOOLEAN_TRUE:
                    return Boolean.TRUE;
                case JsonConstants.TYPE_BOOLEAN_FALSE:
                    return Boolean.FALSE;
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
                case JsonConstants.TYPE_BIGINTEGER:
                    return readBigInteger();
                case JsonConstants.TYPE_BIGDECIMAL:
                    return readBigDecimal();
                case JsonConstants.TYPE_SINGLE:
                    return readFloat();
                case JsonConstants.TYPE_SINGLE_ZERO:
                    return 0f;
                case JsonConstants.TYPE_DOUBLE:
                    return readDouble();
                case JsonConstants.TYPE_DOUBLE_ZERO:
                    return 0d;
                case JsonConstants.TYPE_DATE:
                    return readDate();
                case JsonConstants.TYPE_STRING:
                    return readString();
                case JsonConstants.TYPE_ARRAY:
                    if (jsonOutput) {
                        return readArray();
                    } else {
                        return readList();
                    }
                case JsonConstants.TYPE_OBJECT:
                    if (jsonOutput) {
                        return readObject();
                    } else {
                        return readMap();
                    }
                default:
                    return null;
            }
        } catch (JSONException je) {
            return null;
        }
    }
}
