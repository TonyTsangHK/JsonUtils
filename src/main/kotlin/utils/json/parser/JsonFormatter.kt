package utils.json.parser

import utils.json.core.*
import utils.string.StringUtil
import java.text.DecimalFormat

import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2012-06-07
 * Time: 12:22
 */
class JsonFormatter private constructor() {
    companion object {
        private val _instance = JsonFormatter()
        
        @JvmStatic
        fun getInstance(): JsonFormatter {
            return _instance
        }
    }

    val formatBinary = false
    
    fun quote(string: String?): String {
        if (string == null || string.isEmpty()) {
            return "\"\""
        }

        var b: Char
        var c: Char = 0.toChar()
        
        val len = string.length
        
        val builder = StringBuilder(len + 4)
        var t: String

        builder.append('"')
        for (i in 0 .. len -1) {
            b = c
            c = string.get(i)
            when (c) {
                '\\', '"' -> {
                    builder.append('\\')
                    builder.append(c)
                }
                '/' -> {
                    if (b == '<') {
                        builder.append('\\')
                    }
                    builder.append(c)
                }
                '\b' -> {
                    builder.append("\\b");
                }
                '\t' -> {
                    builder.append("\\t")
                }
                '\n' -> {
                    builder.append("\\n")
                }
                '\u000C' -> {
                    builder.append("\\f")
                }
                '\r' -> {
                    builder.append("\\r")
                }
                else -> {
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                        (c >= '\u2000' && c < '\u2100')) {
                        t = "000" + Integer.toHexString(c.toInt())
                        builder.append("\\u" + t.substring(t.length - 4))
                    } else {
                        builder.append(c)
                    }
                }
            }
        }
        builder.append('"')
        return builder.toString()
    }

    fun numberToString(n: Number): String {
        if (n is Float || n is Double) {
            val decimalFormat = DecimalFormat.getInstance()
            decimalFormat.isGroupingUsed = false
            if (n is Float) {
                // around 7 decimal places for 32 bits single precision floating number
                decimalFormat.maximumFractionDigits = 7
            } else {
                // around 16 decimal places for 64 bits double precision floating number
                decimalFormat.maximumFractionDigits = 16
            }
            
            return decimalFormat.format(n)
        } else {
            // use toString of BigInteger / BigDecimal
            return n.toString()
        }
    }

    fun valueToStringWithoutJson(value: Any?): String {
        if (value == null) {
            return "null"
        }
        if (value is Boolean) {
            return if (value) "true" else "false"
        }
        if (value is ByteArray) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes(value)
            } else {
                return "[Binary data, length: ${value.size}]"
            }
        }
        if (value is Number) {
            return numberToString(value);
        }
        if (value is Date) {
            return quote(JSONTokener.dateFormat.format(value))
        }
        if (value is Map<*, *>) {
            return format(value as Map<String, Any?>)
        }
        if (value is Collection<*>) {
            return format(value)
        }
        return quote(value.toString());
    }

    fun valueToStringWithJson(value: Any?): String {
        if (value == null) {
            return "null"
        }
        if (value is JSONString) {
            val o = value.toJSONString()
            
            if (o is String) {
                return o
            }
            
            throw JSONException("Bad value from toJSONString: $o")
        }
        if (value is Boolean) {
            return if (value) "true" else "false"
        }
        if (value is ByteArray) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes(value)
            } else {
                return "[Binary data, length: ${value.size}]"
            }
        }
        if (value is Number) {
            return numberToString(value)
        }
        if (value is Date) {
            return quote(JSONTokener.dateFormat.format(value))
        }
        if (value is Map<*, *>) {
            return format(value as Map<String, Any?>)
        }
        if (value is Collection<*>) {
            return format(value)
        }
        return quote(value.toString());
    }

    private fun valueToStringWithoutJson(value: Any?, indentFactor: Int, indent: Int): String {
        if (value == null) {
            return "null"
        }
        if (value is Number) {
            return numberToString(value)
        }
        if (value is ByteArray) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes(value)
            } else {
                return "[Binary data, length: ${value.size}]"
            }
        }
        if (value is Date) {
            return quote(JSONTokener.dateFormat.format(value))
        }
        if (value is Boolean) {
            return value.toString()
        }
        if (value is Map<*, *>) {
            return format(value as Map<String, Any?>, indentFactor, indent)
        }
        if (value is Collection<*>) {
            return format(value, indentFactor, indent)
        }
        if (value.javaClass.isArray) {
            return formatGenericArray(value, indentFactor, indent)
        }
        return quote(value.toString())
    }

    private fun valueToStringWithJson(value: Any?, indentFactor: Int, indent: Int): String {
        if (value == null) {
            return "null"
        }
        try {
            if (value is JSONString) {
                val o = value.toJSONString()
                
                if (o is String) {
                    return o
                }
            }
        } catch (e: Exception) {
            /* forget about it */
        }
        if (value is Number) {
            return numberToString(value)
        }
        if (value is Date) {
            return quote(JSONTokener.dateFormat.format(value))
        }
        if (value is Boolean) {
            return value.toString()
        }
        if (value is ByteArray) {
            if (formatBinary) {
                return "\\hex\\" + StringUtil.getHexStringFromBytes(value)
            } else {
                return "[Binary data, length: ${value.size}]"
            }
        }
        if (value is JSONObject) {
            return format(value, indentFactor, indent)
        }
        if (value is JSONArray) {
            return format(value, indentFactor, indent)
        }
        if (value is Map<*, *>) {
            return format(value as Map<String, Any?>, indentFactor, indent)
        }
        if (value is Collection<*>) {
            return format(value, indentFactor, indent)
        }
        if (value.javaClass.isArray) {
            return formatGenericArray(value, indentFactor, indent)
        }
        return quote(value.toString())
    }

    fun joinWithoutJson(collection: Collection<Any?>, separator: String): String {
        val len = collection.size
        val builder = StringBuilder()

        var first = true
        for (v in collection) {
            if (first) {
                first = false
            } else {
                builder.append(separator)
            }
            builder.append(valueToStringWithoutJson(v))
        }
        return builder.toString()
    }

    fun <T> joinWithJson(array: Array<T>, separator: String): String {
        val builder = StringBuilder()

        for (i in 0 .. array.size - 1) {
            if (i > 0) {
                builder.append(separator)
            }
            builder.append(valueToStringWithJson(array[i]))
        }
        return builder.toString()
    }

    fun joinWithJson(collection: Collection<Any?>, separator: String): String {
        val len = collection.size
        
        val builder = StringBuilder()

        var first = true
        for (v in collection) {
            if (first) {
                first = false
            } else {
                builder.append(separator)
            }
            builder.append(valueToStringWithJson(v))
        }
        return builder.toString()
    }

    fun joinWithJson(jsonArray: JSONArray, separator: String): String {
        val builder = StringBuilder()

        for (i in 0 .. jsonArray.length() - 1) {
            if (i > 0) {
                builder.append(separator)
            }
            builder.append(valueToStringWithJson(jsonArray.get(i)))
        }
        
        return builder.toString()
    }

    fun format(collection: Collection<Any?>): String {
        return "[${joinWithoutJson(collection, ",")}]"
    }

    fun format(map: Map<String, Any?>): String {
        val builder = StringBuilder("{")

        for (key in map.keys) {
            if (builder.length > 1) {
                builder.append(',')
            }
            builder.append(quote(key))
            builder.append(':')
            builder.append(valueToStringWithoutJson(map.get(key)))
        }
        builder.append('}')
        return builder.toString()
    }

    fun format(json: JSONObject): String {
        val builder = StringBuilder("{")

        for (key in json.keySet()) {
            if (builder.length > 1) {
                builder.append(',')
            }
            builder.append(quote(key))
            builder.append(':')
            builder.append(valueToStringWithJson(json.get(key)))
        }
        builder.append('}')
        return builder.toString()
    }

    fun format(jsonArray: JSONArray): String {
        return "[${joinWithJson(jsonArray, ",")}]"
    }

    fun format(map: Map<String, Any?>, indentFactor: Int): String {
        return format(map, indentFactor, 0)
    }

    private fun format(map: Map<String, Any?>, indentFactor: Int, indent: Int): String {
        val n = map.size
        
        if (n == 0) {
            return "{}"
        }
        val keyIter = map.keys.iterator()

        val builder = StringBuilder("{")

        val newIndent = indent + indentFactor

        var o: String
        
        if (n == 1) {
            o = keyIter.next()
            builder.append(quote(o))
            builder.append(": ")
            builder.append(valueToStringWithoutJson(map.get(o), indentFactor, indent))
        } else {
            while (keyIter.hasNext()) {
                o = keyIter.next()
                if (builder.length > 1) {
                    builder.append(",\n")
                } else {
                    builder.append('\n')
                }
                for (j in 0 .. newIndent - 1) {
                    builder.append(' ')
                }
                builder.append(quote(o))
                builder.append(": ");
                builder.append(valueToStringWithoutJson(map.get(o), indentFactor, newIndent));
            }
            if (builder.length > 1) {
                builder.append('\n')
                for (j in 0 .. indent - 1) {
                    builder.append(' ')
                }
            }
        }
        builder.append('}')
        return builder.toString()
    }

    fun format(json: JSONObject, indentFactor: Int): String {
        return format(json, indentFactor, 0)
    }

    private fun format(json: JSONObject, indentFactor: Int, indent: Int): String {
        val n = json.length()
        
        if (n == 0) {
            return "{}"
        }
        
        val keyIter = json.sortedKeys()

        val builder = StringBuilder("{")

        val newIndent = indent + indentFactor

        var o: String
        if (n == 1) {
            o = keyIter.next()
            builder.append(quote(o))
            builder.append(": ")
            builder.append(valueToStringWithJson(json.get(o.toString()), indentFactor, indent))
        } else {
            while (keyIter.hasNext()) {
                o = keyIter.next();
                if (builder.length > 1) {
                    builder.append(",\n")
                } else {
                    builder.append('\n')
                }
                for (j in 0 .. newIndent - 1) {
                    builder.append(' ');
                }
                builder.append(quote(o))
                builder.append(": ")
                builder.append(valueToStringWithJson(json.get(o), indentFactor, newIndent))
            }
            if (builder.length > 1) {
                builder.append('\n');
                for (j in 0 .. indent - 1) {
                    builder.append(' ')
                }
            }
        }
        builder.append('}');
        return builder.toString();
    }

    fun format(collection: Collection<Any?>, indentFactor: Int): String {
        return format(collection, indentFactor, 0)
    }

    private fun format(collection: Collection<Any?>, indentFactor: Int, indent: Int): String {
        val len = collection.size
        
        if (len == 0) {
            return "[]"
        }
        
        var i: Int = 0
        
        val builder = StringBuilder("[")

        val newIndent = indent + indentFactor
        
        builder.append('\n')
        
        for (v in collection) {
            var newLined = i == 0
            if (i > 0) {
                builder.append(",")

                if (v is JSONObject || v is JSONArray) {
                    builder.append('\n')
                    newLined = true
                }
            }

            if (newLined) {
                for (j in 0 .. newIndent -1) {
                    builder.append(' ');
                }
            }

            builder.append(valueToStringWithoutJson(v, indentFactor, newIndent))
            i++
        }
        builder.append('\n')
        for (j in 0 .. indent - 1) {
            builder.append(' ')
        }

        builder.append(']')
        return builder.toString()
    }

    private fun formatGenericArray(arr: Any?, indentFactor: Int, indent: Int): String {
        if (arr == null) {
            return "null"
        } else {
            val len = java.lang.reflect.Array.getLength(arr)
            if (len == 0) {
                return "[]"
            } else {
                val builder = StringBuilder("[")

                val newIndent = indent + indentFactor
                builder.append('\n')
                for (i in 0 .. len - 1) {
                    val v = java.lang.reflect.Array.get(arr, i)
                    var newLined = i == 0
                    if (i > 0) {
                        builder.append(",")

                        if (v is JSONObject || v is JSONArray) {
                            builder.append('\n')
                            newLined = true
                        }
                    }

                    if (newLined) {
                        for (j in 0 .. newIndent - 1) {
                            builder.append(' ');
                        }
                    }
                    
                    builder.append(valueToStringWithoutJson(v, indentFactor, newIndent))
                }
                builder.append('\n');
                for (i in 0 .. indent - 1) {
                    builder.append(' ')
                }

                builder.append(']')
                return builder.toString()
            }
        }
    }

    fun format(jsonArray: JSONArray, indentFactor: Int): String {
        return format(jsonArray, indentFactor, 0)
    }

    private fun format(jsonArray: JSONArray, indentFactor: Int, indent: Int): String {
        val len = jsonArray.length()
        
        if (len == 0) {
            return "[]"
        }
        
        val builder = StringBuilder("[")

        val newIndent = indent + indentFactor
        builder.append('\n')
        for (i in 0 .. len - 1) {
            val v = jsonArray.get(i)

            var newLined = i == 0
            if (i > 0) {
                builder.append(",")

                if (v is JSONObject || v is JSONArray) {
                    builder.append('\n')
                    newLined = true
                }
            }

            if (newLined) {
                for (j in 0 .. newIndent - 1) {
                    builder.append(' ')
                }
            }

            builder.append(valueToStringWithJson(v, indentFactor, newIndent))
        }
        builder.append('\n')
        for (i in 0 .. indent - 1) {
            builder.append(' ')
        }

        builder.append(']')
        return builder.toString()
    }
}
