package utils.json.serialize

/**
 * Json constants

 * @author Tony Tsang
 */
object JsonConstants {
    @JvmField
    val TYPE_NULL: Byte = 0x01
    @JvmField
    val TYPE_REF: Byte = 0x02
    @JvmField
    val TYPE_BOOLEAN_TRUE: Byte = 0x03
    @JvmField
    val TYPE_BOOLEAN_FALSE: Byte = 0x04
    @JvmField
    val TYPE_INT8: Byte = 0x05
    @JvmField
    val TYPE_INT16: Byte = 0x06
    @JvmField
    val TYPE_INT24: Byte = 0x07
    @JvmField
    val TYPE_INT32: Byte = 0x08
    @JvmField
    val TYPE_INT40: Byte = 0x09
    @JvmField
    val TYPE_INT48: Byte = 0x0A
    @JvmField
    val TYPE_INT56: Byte = 0x0B
    @JvmField
    val TYPE_INT64: Byte = 0x0C
    @JvmField
    val TYPE_N_INT8: Byte = 0x15
    @JvmField
    val TYPE_N_INT16: Byte = 0x16
    @JvmField
    val TYPE_N_INT24: Byte = 0x17
    @JvmField
    val TYPE_N_INT32: Byte = 0x18
    @JvmField
    val TYPE_N_INT40: Byte = 0x19
    @JvmField
    val TYPE_N_INT48: Byte = 0x1A
    @JvmField
    val TYPE_N_INT56: Byte = 0x1B
    @JvmField
    val TYPE_N_INT64: Byte = 0x1C
    @JvmField
    val TYPE_BIGINTEGER: Byte = 0x20
    @JvmField
    val TYPE_BIGDECIMAL: Byte = 0x21
    @JvmField
    val TYPE_DATE: Byte = 0x22
    @JvmField
    val TYPE_SINGLE: Byte = 0x0D
    @JvmField
    val TYPE_DOUBLE: Byte = 0x0E
    @JvmField
    val TYPE_SINGLE_ZERO: Byte = 0x1D
    @JvmField
    val TYPE_DOUBLE_ZERO: Byte = 0x1E
    @JvmField
    val TYPE_STRING: Byte = 0x0F
    @JvmField
    val TYPE_ARRAY: Byte = 0x10
    @JvmField
    val TYPE_OBJECT: Byte = 0x11
    @JvmField
    val TYPE_BINARY: Byte = 0x55
}
