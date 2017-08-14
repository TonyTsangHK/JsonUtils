package utils.json

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream

import utils.base64.Base64Convertor
import utils.base64.Base64InputStream
import utils.json.serialize.JsonConstants

object JsonStreamFormatDetector {
    @JvmField
    val GZIP_HEADER = byteArrayOf(31,-117,8,0)
    @JvmField
    val STD_B64_GZIP_HEADER = byteArrayOf('H'.toByte(), '4'.toByte(), 's'.toByte(), 'I'.toByte())
                                
    /*
     * Only support standard base64 encoding scheme, other including url_safe will fail the detection.
     * 
     * ORD_B64_GZIP_HEADER = {'6', 's', 'g', '7'}
     * MY_B64_GZIP_HEADER = {'5', 's', 'g', '6'}
    */
    
    @Throws(IOException::class)
    @JvmStatic
    fun detect(file: File): JsonStreamFormat? {
        // Getting the first 4 bytes is enough
        var firstFourBytes = readFirstFourByte(false, null, file)
        
        var gzip = false
        var base64 = false
        
        if (isByteArrayEquals(firstFourBytes, GZIP_HEADER)) {
            // gzipped stream always starts with the same header
            gzip = true
            firstFourBytes = readFirstFourByte(true, null, file)
        } else if (isByteArrayEquals(firstFourBytes, STD_B64_GZIP_HEADER)) {
            // as gzipped stream will have the same header so as the base64 encoded gzipped stream
            gzip = true 
            base64 = true
            firstFourBytes = readFirstFourByte(true, Base64Convertor.Convertor.STANDARD, file)
        } else if (isBase64Suspect(firstFourBytes)) {
            // Not gzipped at least, check if it is a base64 suspect
            base64 = true
            firstFourBytes = readFirstFourByte(false, Base64Convertor.Convertor.STANDARD, file)
        }
        
        val storeType = detectStoreType(firstFourBytes)
        
        if (storeType == null) {
            return null
        } else {
            return JsonStreamFormat(base64, gzip, storeType)
        }
    }
    
    private fun detectStoreType(firstFourBytes: ByteArray): StoreType? {
        if (firstFourBytes[0] == '{'.toByte() || firstFourBytes[0] == '['.toByte()) {
            return StoreType.NORMAL
        } else if (
                (firstFourBytes[0] == 'B'.toByte() && firstFourBytes[1] == 'i'.toByte() && firstFourBytes[2] == 'J'.toByte()) || 
                (firstFourBytes[0] == 'F'.toByte() && firstFourBytes[1] == 'M'.toByte() && firstFourBytes[2] == 'B'.toByte())
        ) {
            if (firstFourBytes[3] == JsonConstants.TYPE_REF) {
                return StoreType.COMPACT
            } else if (firstFourBytes[3] == JsonConstants.TYPE_OBJECT || firstFourBytes[0] == JsonConstants.TYPE_ARRAY.toByte()) {
                return StoreType.BINARY
            }
        } else {
            if (firstFourBytes[0] == JsonConstants.TYPE_REF) {
                return StoreType.COMPACT
            } else if (firstFourBytes[0] == JsonConstants.TYPE_OBJECT || firstFourBytes[0] == JsonConstants.TYPE_ARRAY) {
                return StoreType.BINARY
            }
        }
        
        // Unkown??
        return null
    }
    
    @Throws(IOException::class)
    private fun readFirstFourByte(
        gzip: Boolean, convertor: Base64Convertor.Convertor?, file: File
    ): ByteArray {
        var input: InputStream = FileInputStream(file)
        
        if (convertor != null) {
            input = Base64InputStream(input, convertor)
        }
        if (gzip) {
            input = GZIPInputStream(input)
        }
        
        val bytes = ByteArray(4)
        
        readStream(input, bytes)
        
        input.close()
        
        return bytes
    }
    
    private fun isByteArrayEquals(bytes1: ByteArray, bytes2: ByteArray): Boolean {
        if (bytes1.size != bytes2.size) {
            return false
        } else {
            for (i in 0 .. bytes1.size-1) {
                if (bytes1[i] != bytes2[i]) {
                    return false
                }
            }
            return true
        }
    }
    
    private fun isBase64Suspect(bytes: ByteArray): Boolean {
        for (b in bytes) {
            if (!Base64Convertor.Convertor.STANDARD.isEncodedByte(b)) {
                return false
            }
        }
        return true
    }
    
    @Throws(IOException::class)
    private fun readStream(input: InputStream, bytes: ByteArray): Int {
        var len = 0
        var offset = 0
        
        do {
            len = input.read(bytes, offset, bytes.size - offset)
            offset += if (len != -1) len else 0
        } while (len != -1 && offset < bytes.size)
        
        return offset
    }
}
