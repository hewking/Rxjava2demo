package com.hewking.rxjava2demo

import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat

/**
 * Created by test on 2018/1/21.
 */
fun <T : View> View.v(resid: Int): T {
    return findViewById(resid) as T
}

@ColorInt
fun View.getColor(@ColorRes resid: Int): Int {
    return ContextCompat.getColor(context, resid)
}


fun Paint.textHeight(): Float {
    return descent() - ascent()
}

fun Paint.textWidth(text: String): Float {
    return measureText(text)
}

fun View.setPaddingLeft(@Px size: Int) {
    setPadding(size, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingTop(@Px size: Int) {
    setPadding(paddingLeft, size, paddingRight, paddingBottom)
}

fun View.setPaddingRight(@Px size: Int) {
    setPadding(paddingLeft, size, paddingRight, paddingBottom)
}

fun View.setPaddingBottom(@Px size: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, size)
}


fun View.resoloveSize(size: Int, spec: Int): Int {
    val mode = View.MeasureSpec.getMode(spec)
    val specSize = View.MeasureSpec.getSize(spec)
    return when (mode) {
        View.MeasureSpec.AT_MOST -> {
            if (size > specSize) {
                specSize
            } else {
                size
            }
        }

        View.MeasureSpec.EXACTLY -> {
            specSize
        }

        else -> {
            size
        }
    }
}