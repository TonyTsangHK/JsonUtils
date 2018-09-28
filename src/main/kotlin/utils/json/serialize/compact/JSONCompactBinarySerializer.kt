package utils.json.serialize.compact

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import utils.json.serialize.AbstractJSONSerializer
import java.io.IOException
import java.io.OutputStream

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
class JSONCompactBinarySerializer private constructor() : AbstractJSONSerializer() {
    companion object {
        private val _instance = JSONCompactBinarySerializer()
        
        /**
         * Get a singleton instance of JSONCompactBinarySerializer
         * @return instance of JSONCompactBinarySerializer
         */
        @JvmStatic
        fun getInstance(): JSONCompactBinarySerializer {
            return _instance
        }
    }
    
    private var writer: JSONCompactStreamWriter? = null

    /**
     * Set up writer.
     * 
     * @param out underlying output stream
     * @param jsonInfoHolder JSON info holder, holding intermediate datas
     */
    private fun setup(output: OutputStream, jsonInfoHolder: JSONInfoHolder) {
        if (writer != null) {
            writer!!.setOutputStream(output)
            writer!!.setJsonInfoHolder(jsonInfoHolder)
        } else {
            writer = JSONCompactStreamWriter(output, jsonInfoHolder)
        }
    }

    @Throws(IOException::class)
    override fun serialize(json: JSONObject, output: OutputStream) {
        serialize(json, output, true)
    }

    @Throws(IOException::class)
    override fun serialize(json: JSONObject, output: OutputStream, flushWhenFinished: Boolean) {
        setup(output, JSONInfoHolder(json))
        
        writer!!.writeJSONInfoHolder()
        
        writer!!.writeJSONObject(json)

        if (flushWhenFinished) {
            writer!!.flush()
        }
    }

    @Throws(IOException::class)
    override fun serialize(jsonArray: JSONArray, output: OutputStream) {
        serialize(jsonArray, output, true)
    }

    @Throws(IOException::class)
    override fun serialize(jsonArray: JSONArray, output: OutputStream, flushWhenFinished: Boolean) {
        setup(output, JSONInfoHolder(jsonArray))
        
        writer!!.writeJSONInfoHolder()
        
        writer!!.writeJSONArray(jsonArray)

        if (flushWhenFinished) {
            writer!!.flush()
        }
    }

    @Throws(IOException::class)
    override fun serialize(list: List<Any?>, output: OutputStream) {
        serialize(list, output, true)
    }

    @Throws(IOException::class)
    override fun serialize(list: List<Any?>, output: OutputStream, flushWhenFinished: Boolean) {
        setup(output, JSONInfoHolder(list))
        
        writer!!.writeJSONInfoHolder()
        
        writer!!.writeList(list)

        if (flushWhenFinished) {
            writer!!.flush()
        }
    }

    @Throws(IOException::class)
    override fun serialize(map: Map<String, Any?>, output: OutputStream) {
        serialize(map, output, true)
    }

    @Throws(IOException::class)
    override fun serialize(map: Map<String, Any?>, output: OutputStream, flushWhenFinished: Boolean) {
        setup(output, JSONInfoHolder(map))
        
        writer!!.writeJSONInfoHolder()
        
        writer!!.writeMap(map)

        if (flushWhenFinished) {
            writer!!.flush()
        }
    }
}
