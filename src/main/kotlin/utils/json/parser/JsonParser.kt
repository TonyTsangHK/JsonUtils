package utils.json.parser

import utils.json.core.JSONArray
import utils.json.core.JSONException
import utils.json.core.JSONObject
import utils.json.core.JSONTokener

import java.util.ArrayList
import java.util.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-06-05
 * Time: 09:53
 */
class JsonParser private constructor() {
    companion object {
        private val _instance = JsonParser()
        
        @JvmStatic
        fun getInstance(): JsonParser {
            return _instance
        }
    }

    fun parseJson(jsonString: String): JSONObject? {
        try {
            return JSONObject(jsonString)
        } catch (je: JSONException) {
            return null
        }
    }

    fun parseJsonArray(jsonString: String): JSONArray? {
        try {
            return JSONArray(jsonString)
        } catch (je: JSONException) {
            return null
        }
    }

    fun <E> parseMap(jsonString: String): Map<String, E>? {
        return parseMutableMap(jsonString)
    }
    
    fun <E> parseMutableMap(jsonString: String): MutableMap<String, E>? {
        try {
            val map = HashMap<String, E>()
            val jsonTokener = JSONTokener(jsonString)
            jsonTokener.nextObject(map)
            return map
        } catch (je: JSONException) {
            return null
        }
    }

    fun <E> parseList(jsonString: String): List<E>? {
        return parseMutableList(jsonString)
    }
    
    fun <E> parseMutableList(jsonString: String): MutableList<E>? {
        try {
            val list = ArrayList<E>()
            val jsonTokener = JSONTokener(jsonString)
            jsonTokener.nextArray(list)
            return list
        } catch (je: JSONException) {
            return null
        }
    }
}
