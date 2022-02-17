package ptsiogas.gr.securebox

class DecryptedResult {
    var result: ByteArray? = null
    var needsMigration: Boolean = false

    fun isValid(): Boolean {
        return result != null
    }

    fun getStringResult(): String? {
        return if (result != null) {
            String(result!!)
        } else {
            null
        }
    }
}