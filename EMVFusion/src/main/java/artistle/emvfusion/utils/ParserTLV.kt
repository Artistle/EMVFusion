package artistle.emvfusion.utils

import artistle.emvfusion.entity.TlvConstructed
import artistle.emvfusion.entity.TlvSingle
import artistle.emvfusion.entity.TlvTree
import artistle.emvfusion.utils.hex.fromHexStringToByteArray

fun ByteArray.parseTlvTree(startIndex: Int = 0, endIndex: Int = this.size): List<TlvTree> {
    val result = mutableListOf<TlvTree>()
    var index = startIndex

    while (index < endIndex) {
        val (tag, tagLength) = readTag(this, index)
        index += tagLength

        val (length, lengthLength) = readLength(this, index)
        index += lengthLength

        if (index + length > endIndex) {
            throw IllegalArgumentException("Not enough bytes for value at index $index, length=$length, endIndex=$endIndex")
        }

        val value = this.copyOfRange(index, index + length)
        index += length

        if (tag.isConstructed()) {
            // Рекурсивно парсим вложенные TLV
            val children = value.parseTlvTree(0, value.size)
            result.add(TlvConstructed(tag, children))
        } else {
            result.add(TlvSingle(tag, value))
        }
    }

    return result
}

fun readTag(data: ByteArray, startIndex: Int): Pair<ByteArray, Int> {
    val tagBytes = mutableListOf<Byte>()
    var index = startIndex
    if (index >= data.size) throw IllegalArgumentException("No data to read tag at index $index")

    tagBytes.add(data[index])
    index++

    if ((tagBytes[0].toInt() and 0x1F) == 0x1F) {
        while (index < data.size) {
            val b = data[index]
            tagBytes.add(b)
            index++
            if ((b.toInt() and 0x80) == 0) break
            if (index == data.size && (b.toInt() and 0x80) != 0) {
                throw IllegalArgumentException("Tag seems incomplete: expecting more bytes but reached end of data")
            }
        }
    }

    return Pair(tagBytes.toByteArray(), tagBytes.size)
}


fun readLength(data: ByteArray, startIndex: Int): Pair<Int, Int> {
    if (startIndex >= data.size) throw IllegalArgumentException("Start index $startIndex is out of data range ${data.size}")

    val firstByte = data[startIndex].toInt() and 0xFF

    return if (firstByte < 0x80) {
        // Короткая форма длины
        Pair(firstByte, 1)
    } else {
        // Длинная форма длины
        val numLengthBytes = firstByte and 0x7F

        if (startIndex + numLengthBytes >= data.size) {
            throw IllegalArgumentException("Not enough bytes to read length: needed $numLengthBytes bytes at index $startIndex")
        }

        var length = 0
        for (i in 1..numLengthBytes) {
            length = (length shl 8) or (data[startIndex + i].toInt() and 0xFF)
        }
        Pair(length, 1 + numLengthBytes)
    }
}

/**
 * Проверяем 6-й бит первого байта (0x20) — признак constructed
 */
private fun ByteArray.isConstructed(): Boolean = (this[0].toInt() and 0x20) != 0

fun List<TlvTree>.findTagValue(searchTag: ByteArray): ByteArray? {
    for (tlv in this) {
        when (tlv) {
            is TlvSingle -> {
                if (tlv.tag.contentEquals(searchTag)) {
                    return tlv.value
                }
            }

            is TlvConstructed -> {
                if (tlv.tag.contentEquals(searchTag)) {
                    // иногда хотят значение самого constructed — возвращаем всю вложенность как байты (или null)
                    return null
                } else {
                    val result = tlv.tlvList.findTagValue(searchTag)
                    if (result != null) return result
                }
            }
        }
    }
    return null
}

fun List<TlvTree>.findTagValue(tagHex: String): ByteArray? {
    val searchTag = tagHex.fromHexStringToByteArray()
    return findTagValue(searchTag)
}



