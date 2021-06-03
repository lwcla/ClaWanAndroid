package cn.fhstc.utils.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.io.File


/**
 * Created by midFang on 2021/5/26.
 * Useful:
 */
object ImageUtil {

}


fun Context.asDrawable(@DrawableRes res: Int) = ContextCompat.getDrawable(this, res)
fun Int.asDrawable(context: Context) = ContextCompat.getDrawable(context, this)


fun Drawable.drawable2Bitmap(): Bitmap? {
    if (this is BitmapDrawable) {
        val bitmapDrawable = this
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
        }
    }
    val bitmap: Bitmap = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1, 1,
            if (this.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
    } else {
        Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            if (this.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
    }
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Bitmap?.bitmap2Drawable(context: Context): Drawable? {
    return if (this == null) null else BitmapDrawable(context.resources, this)
}

fun getBitmap(file: File?): Bitmap? {
    return if (file == null) null else BitmapFactory.decodeFile(file.absolutePath)
}

inline fun ImageView.load(
    url: Any,
    placeholder: Drawable? = null,
    errorDrawable: Drawable? = null,
    @IntRange(from = 0) frame: Long = 0,
    requestBuilder: RequestBuilder<Drawable>.() -> Unit = { this.fitCenter() }
) {
    load(url, placeholder, errorDrawable, frame, 0, 0, requestBuilder)
}

inline fun ImageView.load(
    url: Any,
    placeholder: Drawable? = null,
    errorDrawable: Drawable? = null,
    @IntRange(from = 0) frame: Long = 0,
    overrideWidth: Int,
    overrideHeight: Int,
    requestBuilder: RequestBuilder<Drawable>.() -> Unit = { this.fitCenter() }
) {
    Glide.with(this)
        .load(url)
        .frame(frame)
        .placeholder(placeholder)
        .error(errorDrawable)
        .apply {
            if (overrideHeight != 0 && overrideWidth != 0) {
                this.override(overrideWidth, overrideHeight)
            }
        }
        .apply { requestBuilder(this) }
        .into(this)
}


fun String.asBitmap(context: Context, width: Int, height: Int) = Glide.with(context)
    .asBitmap()
    .load(this)
    .submit(width, height)
    .get()


/**
 * 加载圆形图片
 */
fun ImageView.loadCircle(
    url: Any,
    placeholder: Drawable? = null,
    errorDrawable: Drawable? = null
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(errorDrawable)
        .circleCrop()
        .into(this)
}

/**
 * 加载圆角图片
 */
fun ImageView.loadRoundCircle(
    url: Any,
    placeholder: Drawable? = null,
    errorDrawable: Drawable? = null,
    roundingRadius: Int = 20
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(errorDrawable)
        .transform(RoundedCorners(roundingRadius))
        .into(this)
}

/**
 * 加载圆角图片 --- （图片上、下、左、右四个角度任意定义）
 */
fun ImageView.loadRoundCircle(
    url: Any,
    placeholder: Drawable? = null,
    errorDrawable: Drawable? = null,
    topLeft: Float = 0.0f,
    topRight: Float = 0.0f,
    bottomRight: Float = 0.0f,
    bottomLeft: Float = 0.0f,
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(errorDrawable)
        .transform(GranularRoundedCorners(topLeft, topRight, bottomRight, bottomLeft))
        .into(this)
}
















