package com.lectricas.curriencies.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class CurrencyContainer : LinearLayout {

    private var currentOrder: Int = 0

    private val DRAW_ORDERS = arrayOf(intArrayOf(0, 1, 2), intArrayOf(2, 1, 0))

    constructor(context: Context) : super(context) {
        isChildrenDrawingOrderEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        isChildrenDrawingOrderEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId) {
        isChildrenDrawingOrderEnabled = true
    }

    fun setDrawOrder(order: Int) {
        currentOrder = order
        invalidate()
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return DRAW_ORDERS[currentOrder][i]
    }
}