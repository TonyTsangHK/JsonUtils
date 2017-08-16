package utils.test

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2017-08-15
 * Time: 15:24
 */
data class VerifyResult(val pass: Boolean, val reason: String = "") {
    companion object {
        val PASSED = VerifyResult(true)
        
        fun fail(reason: String): VerifyResult {
            return VerifyResult(false, reason)
        }
    }
}