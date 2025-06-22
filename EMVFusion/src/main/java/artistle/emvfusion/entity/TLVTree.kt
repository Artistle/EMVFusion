package artistle.emvfusion.entity

sealed interface TlvTree {

    val tag: ByteArray
}

data class TlvConstructed(override val tag: ByteArray, val tlvList: List<TlvTree>) : TlvTree {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TlvConstructed

        if (!tag.contentEquals(other.tag)) return false
        if (tlvList != other.tlvList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.contentHashCode()
        result = 31 * result + tlvList.hashCode()
        return result
    }
}


data class TlvSingle(override val tag: ByteArray, val value: ByteArray) : TlvTree {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TlvSingle

        if (!tag.contentEquals(other.tag)) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.contentHashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}