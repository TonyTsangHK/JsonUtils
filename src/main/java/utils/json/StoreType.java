package utils.json;

public enum StoreType {
    NORMAL("normal"), BINARY("binary"), COMPACT("compact");
    
    public final String desc;
    
    private StoreType(String desc) {
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return desc;
    }
}