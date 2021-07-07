/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster

import java.awt.AlphaComposite
import java.awt.RenderingHints
import java.awt.image.BufferedImage

object PosterUtils {

    @JvmStatic
    fun resizeImage(image: BufferedImage) {
        val resizer = image.createGraphics()
        // Article said that it "increased image quality". Read: rip heap
        resizer.composite = AlphaComposite.Src
        resizer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        resizer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        resizer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        resizer.drawImage(image, 0, 0, 128, 128, null)
        resizer.dispose()
    }

    @JvmStatic
    fun resizeImageNoEditing(image: BufferedImage) {
        val resizer = image.createGraphics()
        resizer.drawImage(image, 0, 0, 128, 128, null)
        resizer.dispose()
    }

}