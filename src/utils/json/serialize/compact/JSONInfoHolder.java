package utils.json.serialize.compact;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import utils.json.core.JSONArray;
import utils.json.core.JSONException;
import utils.json.core.JSONObject;

import utils.data.SortedList;
import utils.data.SortedListAvl;

/**
 * JSON intermediate data holder
 * 
 * This holder holds all the string (including key), BigInteger, BigDecimal, Date values
 * contained in JSONObject/JSONArray/Map/List.
 * 
 * @author Tony Tsang
 */
public class JSONInfoHolder {
    /**
     * List of string datas
     */
    private SortedList<String> stringList;
    /**
     * List of BigInteger datas
     */
    private SortedList<BigInteger> bigIntegerList;
    /**
     * List of BigDecimal datas
     */
    private SortedList<BigDecimal> bigDecimalList;
    /**
     * List of Date datas
     */
    private SortedList<Date> dateList;
    
    /**
     * Construct a empty holder
     */
    protected JSONInfoHolder() {
        stringList = new SortedListAvl<String>();
        bigIntegerList = new SortedListAvl<BigInteger>();
        bigDecimalList = new SortedListAvl<BigDecimal>();
        dateList = new SortedListAvl<Date>();
    }
    
    /**
     * Construct a JSONInfoHolder with specified JSONObject
     * 
     * @param json target json object
     * 
     * @throws JSONException
     */
    public JSONInfoHolder(JSONObject json) throws JSONException {
        this();
        
        extractInfos(json);
    }
    
    /**
     * Construct a JSONInfoHolder with specified JSONArray
     * 
     * @param jsonArray target json array
     * 
     * @throws JSONException
     */
    public JSONInfoHolder(JSONArray jsonArray) throws JSONException {
        this();
        extractInfos(jsonArray);
    }
    
    /**
     * Construct a JSONInfoHolder with specified Map
     * 
     * @param map target map
     */
    public JSONInfoHolder(Map<String, ?> map) {
        this();
        
        extractInfos(map);
    }
    
    /**
     * Construct a JSONInfoHolder with specified List
     * 
     * @param list target list
     */
    public JSONInfoHolder(List<?> list) {
        this();
        
        extractInfos(list);
    }
    
    /**
     * Add a string if not already contained
     * 
     * @param str target string
     */
    protected void addString(String str) {
        if (!stringList.contains(str)) {
            stringList.add(str);
        }
    }
    
    /**
     * Add a BigInteger if not already contained
     * 
     * @param bigInteger target BigInteger
     */
    protected void addBigInteger(BigInteger bigInteger) {
        if (!bigIntegerList.contains(bigInteger)) {
            bigIntegerList.add(bigInteger);
        }
    }
    
    /**
     * Add a BigDecimal if not already contained
     * 
     * @param bigDecimal target BigDecimal
     */
    protected void addBigDecimal(BigDecimal bigDecimal) {
        if (!bigDecimalList.contains(bigDecimal)) {
            bigDecimalList.add(bigDecimal);
        }
    }
    
    /**
     * Add a Date value if not already contained
     * 
     * @param date target date value
     */
    protected void addDate(Date date) {
        if (!dateList.contains(date)) {
            dateList.add(date);
        }
    }
    
    /**
     * Add an object if it is an instanceof String/BigInteger/BigDecimal/Date & not contained already
     * 
     * @param obj object value
     */
    protected void addObject(Object obj) {
        if (obj instanceof BigInteger) {
            addBigInteger((BigInteger)obj);
        } else if (obj instanceof BigDecimal) {
            addBigDecimal((BigDecimal)obj);
        } else if (obj instanceof String) {
            addString((String)obj);
        } else if (obj instanceof Date) {
            addDate((Date)obj);
        }
    }
    
    /**
     * Extract intermediate datas from specified JSONObject
     * 
     * @param json target json object
     * 
     * @throws JSONException
     */
    private void extractInfos(JSONObject json) throws JSONException {
        for (String key : json.keySet()) {
            addString(key);
            
            Object obj = json.get(key);
            
            if (obj instanceof JSONObject) {
                extractInfos((JSONObject) obj);
            } else if (obj instanceof JSONArray) {
                extractInfos((JSONArray)obj);
            } else if (obj != null) {
                addObject(obj);
            }
        }
    }
    
    /**
     * Extract intermediate datas from specified Map
     * 
     * @param map target map
     */
    @SuppressWarnings("unchecked")
    private void extractInfos(Map<String, ?> map) {
        for (String key : map.keySet()) {
            addString(key);
            
            Object obj = map.get(key);
            
            if (obj instanceof Map) {
                extractInfos((Map<String, ?>) obj);
            } else if (obj instanceof List<?>) {
                extractInfos((List<?>) obj);
            } else if (obj != null) {
                addObject(obj);
            }
        }
    }
    
    /**
     * Extract intermediate datas from specified JSONArray
     * 
     * @param jsonArray target json array
     * 
     * @throws JSONException
     */
    private void extractInfos(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object obj = jsonArray.get(i);
            
            if (obj instanceof JSONObject) {
                extractInfos((JSONObject) obj);
            } else if (obj instanceof JSONArray) {
                extractInfos((JSONArray) obj);
            } else if (obj != null) {
                addObject(obj);
            }
        }
    }
    
    /**
     * Extract intermediate datas from specified List
     * 
     * @param list target list
     */
    @SuppressWarnings("unchecked")
    private void extractInfos(List<?> list) {
        for (Object obj : list) {
            if (obj instanceof Map) {
                extractInfos((Map<String, ?>) obj);
            } else if (obj instanceof List<?>) {
                extractInfos((List<?>) obj);
            } else if (obj != null) {
                addObject(obj);
            }
        }
    }
    
    /**
     * Get the size of this holder
     * 
     * @return size
     */
    public int size() {
        return stringList.size() + bigIntegerList.size() + bigDecimalList.size() + dateList.size();
    }
    
    /**
     * Get the byte size, i.e. minimum number of byte to hold the size data
     * 
     * @return byte size
     */
    public int byteSize() {
        int s = size();
        
        if (s <= 0xFF) {
            return 1;
        } else if (s <= 0xFFFF) {
            return 2;
        } else if (s <= 0xFFFFFF) {
            return 3;
        } else {
            // The maximum size of compacted components will be 2^31-1,
            // size greater than this is not recommended to use compact serialization
            // or even Binary JSON, it is intended to serialize a structured data in binary form,
            // but not to handle large scale data, although data with 2^31-1 components is considerably large.
            return 4;
        }
    }
    
    /**
     * Get the size of string datas
     * 
     * @return size of string datas
     */
    public int stringSize() {
        return stringList.size();
    }
    
    /**
     * Get the size of BigInteger datas
     * 
     * @return size of BigInteger datas
     */
    public int bigIntegerSize() {
        return bigIntegerList.size();
    }
    
    /**
     * Get the size of BigDecimal datas
     * 
     * @return size of BigDecimal datas
     */
    public int bigDecimalSize() {
        return bigDecimalList.size();
    }
    
    /**
     * Get the size of Date datas
     * 
     * @return size of date datas
     */
    public int dateSize() {
        return dateList.size();
    }
    
    /**
     * Get a String from this holder with specified index
     * 
     * @param index target string index
     * @return string data
     */
    public String getString(int index) {
        if (index >= 0 && index < stringList.size()) {
            return stringList.get(index);
        } else {
            return null;
        }
    }
    
    /**
     * Get a BigInteger from this holder with specified index
     * 
     * @param index target BigInteger index
     * @return BigInteger data
     */
    public BigInteger getBigInteger(int index) {
        if (index >= 0 && index < bigIntegerList.size()) {
            return bigIntegerList.get(index);
        } else {
            return null;
        }
    }
    
    /**
     * Get a BigDecimal from this holder with specified index
     * 
     * @param index target BigDecimal index
     * @return BigDecimal data
     */
    public BigDecimal getBigDecimal(int index) {
        if (index >= 0 && index < bigDecimalList.size()) {
            return bigDecimalList.get(index);
        } else {
            return null;
        }
    }
    
    /**
     * Get a Date from this holder with specified index
     * 
     * @param index target date index
     * @return Date data
     */
    public Date getDate(int index) {
        if (index >= 0 && index < dateList.size()) {
            return dateList.get(index);
        } else {
            return null;
        }
    }
    
    /**
     * Get a value from this holder with specified index (compound index: string > bigInteger > bigDecimal > date)
     * 
     * @param index target index
     * 
     * @return value of the target index, null if index out of bound
     */
    public Object get(int index) {
        if (index >= 0) {
            if (index < stringList.size()) {
                return stringList.get(index);
            } else if (index < bigIntegerList.size() + stringList.size()) {
                return bigIntegerList.get(index-stringList.size());
            } else if (index < bigDecimalList.size() + bigIntegerList.size() + stringList.size()) {
                return bigDecimalList.get(index-bigIntegerList.size()-stringList.size());
            } else if (index < size()) {
                return dateList.get(index-bigDecimalList.size()-bigIntegerList.size()-stringList.size());
            }
        }
        return null;
    }
    
    /**
     * Get the index of the specified object
     * 
     * @param obj target object
     * 
     * @return index of the specified object
     */
    public int indexOf(Object obj) {
        if (obj == null) {
            return -1;
        } else if (obj instanceof String) {
            return stringList.indexOf(obj);
        } else if (obj instanceof BigInteger) {
            return stringList.size() + bigIntegerList.indexOf(obj);
        } else if (obj instanceof BigDecimal) {
            return stringList.size() + bigIntegerList.size() + bigDecimalList.indexOf(obj);
        } else if (obj instanceof Date) {
            return stringList.size() + bigIntegerList.size() + bigDecimalList.size() + dateList.indexOf(obj);
        } else {
            return -1;
        }
    }
}