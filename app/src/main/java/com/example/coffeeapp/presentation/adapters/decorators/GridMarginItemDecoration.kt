package com.example.coffeeapp.presentation.adapters.decorators

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridMarginItemDecoration(
    private val spaceSizeDp: Float,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        val spaceSizePx = convertDpToPixel(spaceSizeDp, view.context).toInt()

        if (position >= 0) {
            val column = position % spanCount
            outRect.left = spaceSizePx - column * spaceSizePx / spanCount
            outRect.right = (column + 1) * spaceSizePx / spanCount
            if (position < spanCount) outRect.top = spaceSizePx
            outRect.bottom = spaceSizePx
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        val displayPixelDensity = context.resources.displayMetrics.densityDpi
        return dp * (displayPixelDensity.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}