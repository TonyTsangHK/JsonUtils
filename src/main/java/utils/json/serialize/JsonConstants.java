package utils.json.serialize;

/**
 * Json constants
 * 
 * @author Tony Tsang
 *
 */
public class JsonConstants {
    private JsonConstants() {}
    
    public static final int
        TYPE_NULL          = 0x01,
        TYPE_REF           = 0x02,
        TYPE_BOOLEAN_TRUE  = 0x03,
        TYPE_BOOLEAN_FALSE = 0x04,
        TYPE_INT8          = 0x05,
        TYPE_INT16         = 0x06,
        TYPE_INT24         = 0x07,
        TYPE_INT32         = 0x08,
        TYPE_INT40         = 0x09,
        TYPE_INT48         = 0x0A,
        TYPE_INT56         = 0x0B,
        TYPE_INT64         = 0x0C,
        TYPE_N_INT8        = 0x15,
        TYPE_N_INT16       = 0x16,
        TYPE_N_INT24       = 0x17,
        TYPE_N_INT32       = 0x18,
        TYPE_N_INT40       = 0x19,
        TYPE_N_INT48       = 0x1A,
        TYPE_N_INT56       = 0x1B,
        TYPE_N_INT64       = 0x1C,
        TYPE_BIGINTEGER    = 0x20,
        TYPE_BIGDECIMAL    = 0x21,
        TYPE_DATE          = 0x22,
        TYPE_SINGLE        = 0x0D,
        TYPE_DOUBLE        = 0x0E,
        TYPE_SINGLE_ZERO   = 0x1D,
        TYPE_DOUBLE_ZERO   = 0x1E,
        TYPE_STRING        = 0x0F,
        TYPE_ARRAY         = 0x10,
        TYPE_OBJECT        = 0x11,
        TYPE_BINARY        = 0x55;
}
