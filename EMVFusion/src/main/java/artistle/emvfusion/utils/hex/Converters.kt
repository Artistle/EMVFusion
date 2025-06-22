package artistle.emvfusion.utils.hex

fun ByteArray.toHexString(): String {
    val hexDigits = "0123456789ABCDEF".toCharArray()
    return if (this.isNotEmpty()) {
        val hexChars = CharArray(this.size * 2)
        var v: Int
        for (i in this.indices) {
            v = this[i].toInt() and 0xFF
            hexChars[2 * i] = hexDigits.get(v ushr 4)
            hexChars[2 * i + 1] = hexDigits.get(v and 0xF)
        }
        String(hexChars)
    } else "empty"
}

fun String.fromHexStringToByteArray(): ByteArray {
    val hexStringNoSpace = this.replace(" ", "").trim { it <= ' ' }
    val adjustedHex: String = if (0 == hexStringNoSpace.length % 2) {
        hexStringNoSpace
    } else {
        "0$hexStringNoSpace"
    }
    val hexChars = adjustedHex.toCharArray()
    val data = ByteArray(hexChars.size / 2)
    var i = 0
    while (i < hexChars.size) {
        data[i / 2] = ((Character.digit(hexChars[i], 16) shl 4)
                + Character.digit(hexChars[i + 1], 16)).toByte()
        i += 2
    }
    return data
}
