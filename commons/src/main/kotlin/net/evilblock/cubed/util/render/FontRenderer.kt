package net.evilblock.cubed.util.render

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.ceil

object FontRenderer {

    private val charOffsets = IntArray(256)
    private val charWidths = IntArray(256)
    private val textureHeight: Int
    private val textureWidth: Int

    init {
        val fontTexture: BufferedImage = ImageIO.read(this.javaClass.classLoader.getResourceAsStream("default.png"))
        val width = fontTexture.width
        val height = fontTexture.height
        textureWidth = width
        textureHeight = height
        calculateCharWidths(fontTexture, width, height)
    }

    private fun calculateCharWidths(fontTexture: BufferedImage, width: Int, height: Int) {
        val fontData = IntArray(width * height)
        fontTexture.getRGB(0, 0, width, height, fontData, 0, width)
        val maxCharWidth = width / 16
        val maxCharHeight = height / 16
        for (character in 0..127) {
            val col = character % 16
            val row = character / 16
            val offset = col * maxCharWidth + row * maxCharHeight * width
            if (character == 32) {

                charWidths[32] = maxCharWidth / 3
            } else {

                
                var chStart = 0
                for (c in 0 until maxCharWidth) {
                    chStart = c
                    if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
                        break
                    }
                }

                var chEnd = maxCharWidth - 1
                for (c in maxCharWidth - 1 downTo chStart) {
                    chEnd = c
                    if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
                        break
                    }
                }
                charOffsets[character] = chStart
                charWidths[character] = chEnd - chStart + 1
            }
        }
    }

    fun getWidth(text: String): Int {
        if (text.isEmpty()) {
            return 0
        }

        val charWidthScale = 128F / textureWidth

        var width = 0F
        var j = 0
        while (j < text.length) {
            val charCode = text[j].toInt()
            if (charCode == '&'.toInt()) {
                j++
            } else {
                if (charCode >= charWidths.size) {
                    width += charWidths[' '.toInt()]
                } else {
                    width += charWidths[charCode] * charWidthScale + 1
                }
            }
            j++
        }

        return ceil(width.toDouble()).toInt()
    }

    fun getHeight(): Int {
        return ceil(textureHeight.toDouble()).toInt()
    }

    private fun isColEmpty(imgData: IntArray, offset: Int, imageWidth: Int, maxCharHeight: Int): Boolean {

        for (row in 0 until maxCharHeight) {
            val rowOffset = offset + row * imageWidth
            if (imgData[rowOffset] shr 24 and 0xFF > 128) {

                return false
            }
        }
        return true
    }

}
