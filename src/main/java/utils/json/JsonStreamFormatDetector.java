package utils.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import utils.base64.Base64Convertor;
import utils.base64.Base64InputStream;
import utils.json.serialize.JsonConstants;

public class JsonStreamFormatDetector {
    private static final byte[] GZIP_HEADER = {31,-117,8,0},
                                STD_B64_GZIP_HEADER = {'H', '4', 's', 'I'};
                                
    /*
     * Only support standard base64 encoding scheme, other including url_safe will fail the detection.
     * 
     * ORD_B64_GZIP_HEADER = {'6', 's', 'g', '7'},
     * MY_B64_GZIP_HEADER = {'5', 's', 'g', '6'};
    */
    
    private JsonStreamFormatDetector() {}
    
    public static JsonStreamFormat detect(File file) throws IOException {
        // Getting the first 4 bytes is enough
        byte[] firstFourBytes = readFirstFourByte(false, null, file);
        
        boolean gzip = false, base64 = false;
        
        if (isByteArrayEquals(firstFourBytes, GZIP_HEADER)) {
            // gzipped stream always starts with the same header
            gzip = true;
            firstFourBytes = readFirstFourByte(true, null, file);
        } else if (isByteArrayEquals(firstFourBytes, STD_B64_GZIP_HEADER)) {
            // as gzipped stream will have the same header so as the base64 encoded gzipped stream
            gzip = base64 = true;
            firstFourBytes = readFirstFourByte(true, Base64Convertor.Convertor.STANDARD, file);
        } else if (isBase64Suspect(firstFourBytes)) {
            // Not gzipped at least, check if it is a base64 suspect
            base64 = true;
            firstFourBytes = readFirstFourByte(false, Base64Convertor.Convertor.STANDARD, file);
        }
        
        StoreType storeType = detectStoreType(firstFourBytes);
        
        if (storeType == null) {
            return null;
        } else {
            return new JsonStreamFormat(base64, gzip, storeType);
        }
    }
    
    private static StoreType detectStoreType(byte[] firstFourBytes) {
        if (firstFourBytes[0] == '{' || firstFourBytes[0] == '[') {
            return StoreType.NORMAL;
        } else if (
                (firstFourBytes[0] == 'B' && firstFourBytes[1] == 'i' && firstFourBytes[2] == 'J') || 
                (firstFourBytes[0] == 'F' && firstFourBytes[1] == 'M' && firstFourBytes[2] == 'B')
        ) {
            if (firstFourBytes[3] == JsonConstants.TYPE_REF) {
                return StoreType.COMPACT;
            } else if (firstFourBytes[3] == JsonConstants.TYPE_OBJECT || firstFourBytes[0] == JsonConstants.TYPE_ARRAY) {
                return StoreType.BINARY;
            }
        } else {
            if (firstFourBytes[0] == JsonConstants.TYPE_REF) {
                return StoreType.COMPACT;
            } else if (firstFourBytes[0] == JsonConstants.TYPE_OBJECT || firstFourBytes[0] == JsonConstants.TYPE_ARRAY) {
                return StoreType.BINARY;
            }
        }
        
        // Unkown??
        return null;
    }
    
    private static byte[] readFirstFourByte(
            boolean gzip, Base64Convertor.Convertor convertor, File file
    ) throws IOException {
        InputStream in = new FileInputStream(file);
        
        if (convertor != null) {
            in = new Base64InputStream(in, convertor);
        }
        if (gzip) {
            in = new GZIPInputStream(in);
        }
        
        byte[] bytes = new byte[4];
        
        readStream(in, bytes);
        
        return bytes;
    }
    
    private static boolean isByteArrayEquals(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length != bytes2.length) {
            return false;
        } else {
            for (int i = 0; i < bytes1.length; i++) {
                if (bytes1[i] != bytes2[i]) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static boolean isBase64Suspect(byte[] bytes) {
        for (byte b : bytes) {
            if (!Base64Convertor.Convertor.STANDARD.isEncodedByte(b)) {
                return false;
            }
        }
        return true;
    }
    
    private static int readStream(InputStream in, byte[] bytes) throws IOException {
        int len = 0, offset = 0;
        do {
            len = in.read(bytes, offset, bytes.length - offset);
            offset += (len != -1)? len : 0;
        } while (len != -1 && offset < bytes.length);
        
        return offset;
    }
}
