package ru.netology.nmedia.service

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class CircleTransformation : BitmapTransformation() {
    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val diameter = Math.min(toTransform.width, toTransform.height)
        val x = (toTransform.width - diameter) / 2
        val y = (toTransform.height - diameter) / 2

        val squared = Bitmap.createBitmap(toTransform, x, y, diameter, diameter)
        val result = pool.get(diameter, diameter, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true

        val radius = diameter / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("circle".toByteArray(Key.CHARSET))
    }
}