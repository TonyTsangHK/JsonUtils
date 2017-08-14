package utils.json

class JsonStreamFormat(val base64: Boolean, val gzipped: Boolean, val storeType: StoreType) {
    // Auto getter for base64 / gzipped is getBase64/getGzipped
    // These function keep the same api as java implementation
    fun isBase64(): Boolean {
        return base64
    }
    
    fun isGzipped(): Boolean {
        return gzipped
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is JsonStreamFormat) {
            return other.base64 == base64 && other.gzipped == gzipped && other.storeType == storeType
        } else {
            return false
        }
    }
    
    override fun hashCode(): Int {
        var hash = if (base64) 8 else 0
        
        if (gzipped) {
            hash = hash.or(4)
        }
        
        when (storeType) {
            StoreType.NORMAL -> {
                hash = hash.or(1)
            }
            StoreType.BINARY -> {
                hash = hash.or(2)
            }
            StoreType.COMPACT -> {
                hash = hash.or(3)
            }
        }
        
        return hash
    }
    
    override fun toString(): String {
        return "Format > base64: ${if (base64) "on" else "off"}, gzip: ${if (gzipped) "on" else "off"}, storeType: $storeType"
    }
}
