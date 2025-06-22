package artistle.emvfusion.utils

import artistle.emvfusion.entity.TlvConstructed
import artistle.emvfusion.entity.TlvSingle
import artistle.emvfusion.entity.TlvTree
import artistle.emvfusion.utils.hex.toHexString


fun List<TlvTree>.printTlvTree(indentLevel: Int = 0) {
    val indent = "  ".repeat(indentLevel)
    for (tlv in this) {
        when (tlv) {
            is TlvSingle -> {
                println(
                    "${indent}Tag: ${
                        tlv.tag.toHexString().uppercase().padStart(2, '0')
                    }, Value: ${tlv.value.toHexString()}"
                )
            }

            is TlvConstructed -> {
                println(
                    "${indent}Tag: ${
                        tlv.tag.toHexString().uppercase().padStart(2, '0')
                    } (constructed)"
                )
                tlv.tlvList.printTlvTree(indentLevel + 1)
            }
        }
    }
}

fun TlvTree.printTlvTree(indentLevel: Int = 0) {
    val indent = "  ".repeat(indentLevel)
    when (this) {
        is TlvSingle -> {
            println(
                "${indent}Tag: ${
                    this.tag.toHexString().uppercase().padStart(2, '0')
                }, Value: ${this.value.toHexString()}"
            )
        }

        is TlvConstructed -> {
            println(
                "${indent}Tag: ${
                    this.tag.toHexString().uppercase().padStart(2, '0')
                } (constructed)"
            )
            for (child in this.tlvList) {
                child.printTlvTree(indentLevel + 1)
            }
        }
    }
}