package utils.json.serialize

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

import utils.json.core.JSONArray
import utils.json.core.JSONObject
import java.io.IOException
import java.io.OutputStream

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
class JSONBinarySerializer private constructor() : AbstractJSONSerializer() {
    companion object {
        private val _instance = JSONBinarySerializer()
        
        /**
         * Get a singleton instance of JSONBinarySerializer
         * @return
         */
        @JvmStatic
        fun getInstance(): JSONBinarySerializer {
            return _instance
        }
    }

    /**
     * JSON stream writer
     */
    private var writer: JSONStreamWriter? = null

    @Throws(IOException::class)
    fun setupOutputStream(output: OutputStream) {
        if (writer != null) {
            writer!!.setOutputStream(output)
        } else {
            writer = JSONStreamWriter(output)
        }
    }

    @Throws(IOException::class)
    override fun serialize(json: JSONObject, output: OutputStream) {
        serialize(json, output, true)
    }
    
    @Throws(IOException::class)
    override fun serialize(json: JSONObject, output: OutputStream, flushWhenFinished: Boolean) {
        setupOutputStream(output)
        writer!!.writeJSONObject(json)
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
        setupOutputStream(output)
        writer!!.writeMap(map)
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
        setupOutputStream(output)
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
        setupOutputStream(output)
        writer!!.writeList(list)
        if (flushWhenFinished) {
            writer!!.flush()
        }
    }
}
