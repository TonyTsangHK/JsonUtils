package utils.json

enum class StoreType(val desc: String) {
    NORMAL("normal"), BINARY("binary"), COMPACT("compact");
    
    override fun toString(): String {
        return desc
    }
}