package utils.json;

public class JsonStreamFormat {
    private boolean base64, gzipped;
    private StoreType storeType;
    
    public JsonStreamFormat(boolean base64, boolean gzipped, StoreType storeType) {
        this.base64 = base64;
        this.gzipped = gzipped;
        this.storeType = storeType;
    }
    
    public boolean isBase64() {
        return base64;
    }
    
    public boolean isGzipped() {
        return gzipped;
    }
    
    public StoreType getStoreType() {
        return storeType;
    }
    
    public void setBase64(boolean base64) {
        this.base64 = base64;
    }
    
    public void setGzipped(boolean gzipped) {
        this.gzipped = gzipped;
    }
    
    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof JsonStreamFormat) {
            JsonStreamFormat format = (JsonStreamFormat) o;
            
            return format.base64 == base64 && format.gzipped == gzipped && format.storeType == storeType;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int hash = (base64)? 8 : 0;
        
        if (gzipped) {
            hash |= 4;
        }
        
        switch (storeType) {
            case NORMAL:
                hash |= 1;
                break;
            case BINARY:
                hash |= 2;
                break;
            case COMPACT:
                hash |= 3;
                break;
        }
        
        return hash;
    }
    
    @Override
    public String toString() {
        return "Format > base64: " + ((base64)? "on" : "off") + ", gzip: " + ((gzipped)? "on" : "off") + 
            ", storeType: " + storeType.desc;
    }
}
