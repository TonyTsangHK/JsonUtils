package utils.json.serialize.compact

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

import utils.json.core.JSONArray
import utils.json.core.JSONObject

import utils.data.SortedList
import utils.data.SortedListAvl

/**
 * JSON intermediate data holder
 * 
 * This holder holds all the string (including key), BigInteger, BigDecimal, Date values
 * contained in JSONObject/JSONArray/Map/List.
 * 
 * @author Tony Tsang
 */
class JSONInfoHolder {
    /**
     * List of string datas
     */
    private val stringList: SortedList<String>
    /**
     * List of BigInteger datas
     */
    private val bigIntegerList: SortedList<BigInteger>
    /**
     * List of BigDecimal datas
     */
    private val bigDecimalList: SortedList<BigDecimal>
    /**
     * List of Date datas
     */
    private val dateList: SortedList<Date>
    
    /**
     * Construct a empty holder
     */
    private constructor() {
        stringList = SortedListAvl()
        bigIntegerList = SortedListAvl()
        bigDecimalList = SortedListAvl()
        dateList = SortedListAvl()
    }
    
    /**
     * Construct a JSONInfoHolder with specified JSONObject
     * 
     * @param json target json object
     */
    public constructor(json: JSONObject): this() {
        extractInfos(json)
    }
    
    /**
     * Construct a JSONInfoHolder with specified JSONArray
     * 
     * @param jsonArray target json array
     * 
     * @throws JSONException
     */
    public constructor(jsonArray: JSONArray): this() {
        extractInfos(jsonArray)
    }
    
    /**
     * Construct a JSONInfoHolder with specified Map
     * 
     * @param map target map
     */
    public constructor(map: Map<String, Any?>): this() {
        extractInfos(map)
    }
    
    /**
     * Construct a JSONInfoHolder with specified List
     * 
     * @param list target list
     */
    public constructor(list: List<Any?>): this() {
        extractInfos(list)
    }
    
    /**
     * Add a string if not already contained
     * 
     * @param str target string
     */
    protected fun addString(str: String) {
        if (!stringList.contains(str)) {
            stringList.add(str)
        }
    }
    
    /**
     * Add a BigInteger if not already contained
     * 
     * @param bigInteger target BigInteger
     */
    protected fun addBigInteger(bigInteger: BigInteger) {
        if (!bigIntegerList.contains(bigInteger)) {
            bigIntegerList.add(bigInteger)
        }
    }
    
    /**
     * Add a BigDecimal if not already contained
     * 
     * @param bigDecimal target BigDecimal
     */
    protected fun addBigDecimal(bigDecimal: BigDecimal) {
        if (!bigDecimalList.contains(bigDecimal)) {
            bigDecimalList.add(bigDecimal)
        }
    }
    
    /**
     * Add a Date value if not already contained
     * 
     * @param date target date value
     */
    protected fun addDate(date: Date) {
        if (!dateList.contains(date)) {
            dateList.add(date)
        }
    }
    
    /**
     * Add an object if it is an instanceof String/BigInteger/BigDecimal/Date & not contained already
     * 
     * @param obj object value
     */
    protected fun addObject(obj: Any) {
        if (obj is BigInteger) {
            addBigInteger(obj)
        } else if (obj is BigDecimal) {
            addBigDecimal(obj)
        } else if (obj is String) {
            addString(obj)
        } else if (obj is Date) {
            addDate(obj)
        }
    }
    
    /**
     * Extract intermediate datas from specified JSONObject
     * 
     * @param json target json object
     * 
     * @throws JSONException
     */
    private fun extractInfos(json: JSONObject) {
        for (key in json.keySet()) {
            addString(key)
            
            val obj = json.get(key)
            
            if (obj is JSONObject) {
                extractInfos(obj)
            } else if (obj is JSONArray) {
                extractInfos(obj)
            } else if (obj != null) {
                addObject(obj)
            }
        }
    }
    
    /**
     * Extract intermediate datas from specified Map
     * 
     * @param map target map
     */
    @SuppressWarnings("unchecked")
    private fun extractInfos(map: Map<String, Any?>) {
        for (key in map.keys) {
            addString(key)
            
            val obj = map.get(key)
            
            if (obj is Map<*, *>) {
                extractInfos(obj as Map<String, Any?>)
            } else if (obj is List<*>) {
                extractInfos(obj as List<Any?>)
            } else if (obj != null) {
                addObject(obj)
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
    private fun extractInfos(jsonArray: JSONArray) {
        for (i in 0 .. jsonArray.length()-1) {
            val obj = jsonArray.get(i)
            
            if (obj is JSONObject) {
                extractInfos(obj)
            } else if (obj is JSONArray) {
                extractInfos(obj)
            } else if (obj != null) {
                addObject(obj)
            }
        }
    }
    
    /**
     * Extract intermediate datas from specified List
     * 
     * @param list target list
     */
    private fun extractInfos(list: List<Any?>) {
        for (obj in list) {
            if (obj is Map<*, *>) {
                extractInfos(obj as Map<String, Any?>)
            } else if (obj is List<*>) {
                extractInfos(obj as List<Any?>)
            } else if (obj != null) {
                addObject(obj)
            }
        }
    }
    
    /**
     * Get the size of this holder
     * 
     * @return size
     */
    public fun size(): Int {
        return stringList.size + bigIntegerList.size + bigDecimalList.size + dateList.size
    }
    
    /**
     * Get the byte size, i.e. minimum number of byte to hold the size data
     * 
     * @return byte size
     */
    public fun byteSize(): Int {
        val s = size()
        
        if (s <= 0xFF) {
            return 1
        } else if (s <= 0xFFFF) {
            return 2
        } else if (s <= 0xFFFFFF) {
            return 3
        } else {
            // The maximum size of compacted components will be 2^31-1,
            // size greater than this is not recommended to use compact serialization
            // or even Binary JSON, it is intended to serialize a structured data in binary form,
            // but not to handle large scale data, although data with 2^31-1 components is considerably large.
            return 4
        }
    }
    
    /**
     * Get the size of string datas
     * 
     * @return size of string datas
     */
    public fun stringSize(): Int {
        return stringList.size
    }
    
    /**
     * Get the size of BigInteger datas
     * 
     * @return size of BigInteger datas
     */
    public fun bigIntegerSize(): Int {
        return bigIntegerList.size
    }
    
    /**
     * Get the size of BigDecimal datas
     * 
     * @return size of BigDecimal datas
     */
    public fun bigDecimalSize(): Int {
        return bigDecimalList.size
    }
    
    /**
     * Get the size of Date datas
     * 
     * @return size of date datas
     */
    public fun dateSize(): Int {
        return dateList.size
    }
    
    /**
     * Get a String from this holder with specified index
     * 
     * @param index target string index
     * @return string data
     */
    public fun getString(index: Int): String? {
        if (index >= 0 && index < stringList.size) {
            return stringList.get(index)
        } else {
            return null
        }
    }
    
    /**
     * Get a BigInteger from this holder with specified index
     * 
     * @param index target BigInteger index
     * @return BigInteger data
     */
    public fun getBigInteger(index: Int): BigInteger? {
        if (index >= 0 && index < bigIntegerList.size) {
            return bigIntegerList.get(index)
        } else {
            return null
        }
    }
    
    /**
     * Get a BigDecimal from this holder with specified index
     * 
     * @param index target BigDecimal index
     * @return BigDecimal data
     */
    public fun getBigDecimal(index: Int): BigDecimal? {
        if (index >= 0 && index < bigDecimalList.size) {
            return bigDecimalList.get(index)
        } else {
            return null
        }
    }
    
    /**
     * Get a Date from this holder with specified index
     * 
     * @param index target date index
     * @return Date data
     */
    public fun getDate(index: Int): Date? {
        if (index >= 0 && index < dateList.size) {
            return dateList.get(index)
        } else {
            return null
        }
    }
    
    /**
     * Get a value from this holder with specified index (compound index: string > bigInteger > bigDecimal > date)
     * 
     * @param index target index
     * 
     * @return value of the target index, null if index out of bound
     */
    public fun get(index: Int): Any? {
        if (index >= 0) {
            if (index < stringList.size) {
                return stringList.get(index)
            } else if (index < bigIntegerList.size + stringList.size) {
                return bigIntegerList.get(index-stringList.size)
            } else if (index < bigDecimalList.size + bigIntegerList.size + stringList.size) {
                return bigDecimalList.get(index-bigIntegerList.size-stringList.size)
            } else if (index < size()) {
                return dateList.get(index-bigDecimalList.size-bigIntegerList.size-stringList.size)
            }
        }
        return null
    }
    
    /**
     * Get the index of the specified object
     * 
     * @param obj target object
     * 
     * @return index of the specified object
     */
    public fun indexOf(obj: Any?): Int {
        if (obj == null) {
            return -1
        } else if (obj is String) {
            return stringList.indexOf(obj)
        } else if (obj is BigInteger) {
            return stringList.size + bigIntegerList.indexOf(obj)
        } else if (obj is BigDecimal) {
            return stringList.size + bigIntegerList.size + bigDecimalList.indexOf(obj)
        } else if (obj is Date) {
            return stringList.size + bigIntegerList.size + bigDecimalList.size + dateList.indexOf(obj)
        } else {
            return -1
        }
    }
}