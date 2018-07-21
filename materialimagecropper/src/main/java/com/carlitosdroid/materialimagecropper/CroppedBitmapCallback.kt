package com.spidev.materialimagecropper

import android.graphics.Bitmap

/**
 * @author carlosleonardocamilovargashuaman on 7/6/18.
 */
interface CroppedBitmapCallback {
    fun onCroppedBitmapReady(croppedBitmap: Bitmap)
}