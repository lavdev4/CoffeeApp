package com.example.coffeeapp.presentation.adapters.decorators

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalMarginItemDecoration(
    private val spaceSizeDp: Float
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val spaceSizePx = convertDpToPixel(spaceSizeDp, view.context)
        if (parent.getChildAdapterPosition(view) != 0) outRect.top = spaceSizePx.toInt()
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        val displayPixelDensity = context.resources.displayMetrics.densityDpi
        return dp * (displayPixelDensity.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}