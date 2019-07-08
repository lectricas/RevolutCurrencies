package com.lectricas.curriencies.ui

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.transition.addListener
import androidx.core.view.GestureDetectorCompat
import com.lectricas.curriencies.R
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import kotlinx.android.synthetic.main.item_currency.view.currencyText
import timber.log.Timber
import java.text.DecimalFormat

class CurrencyContainer : LinearLayout {

    private val auto: Transition = AutoTransition()
    private var formatter = DecimalFormat("#0.000")
    private lateinit var listener: CurrencyListener
    private val watcher = Watcher()
    private lateinit var singleTapDetector: GestureDetectorCompat
    private val items = mutableListOf<CurrencyItem>()
    private lateinit var inflater: LayoutInflater
    private lateinit var firstView: ViewGroup

    constructor(context: Context) : super(context) { initialize() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize() }
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId) { initialize() }

    private fun initialize() {
        inflater = LayoutInflater.from(context)
        singleTapDetector = GestureDetectorCompat(context, SingleTapDetector())
        auto.addListener(onEnd = {
            val text = prepareFirstForInput()
            listener.onAmountChanged(text)
        })
    }

    private fun prepareFirstForInput(): String {
        (parent as ScrollView).smoothScrollTo(0,0)
        val editText = firstView.getChildAt(1) as AppCompatEditText
        editText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, 0)
        editText.setSelection(editText.text?.length?: 0)
        return editText.text.toString()
    }

    fun setListener(listener: CurrencyListener) {
        this.listener = listener
    }

    private inner class SingleTapDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?) = true
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            singleTap(e)
            return true
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) = true

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return singleTapDetector.onTouchEvent(ev)
    }

    private fun singleTap(event: MotionEvent) {
        val view = getViewByCoordinates(event.x, event.y) as ViewGroup
        if (indexOfChild(view) == 0) {
            notifyItemMoved(0,0)
        } else {
            listener.onNewCurrencyClicked(indexOfChild(view))
        }
    }

    private fun ViewGroup.getViewByCoordinates(x: Float, y: Float): View? {
        (this.childCount - 1 downTo 0)
            .map { this.getChildAt(it) }
            .forEach {
                val bounds = Rect()
                it.getHitRect(bounds)
                if (bounds.contains(x.toInt(), y.toInt())) {
                    return it
                }
            }
        return null
    }

    private inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.onAmountChanged(s.toString())
        }
    }

    fun updateContent(newItems: Pair<List<CurrencyItem>, Int>) {
        Timber.d(newItems.toString())
        if (items.isEmpty()) {
            items.addAll(newItems.first)
            notifyDataSetChanged()
            return
        }
        if (newItems.second == 0) {
            items.clear()
            items.addAll(newItems.first)
            notifyItemRangeChanged(1,items.size - 1)
            return
        }
        items.clear()
        items.addAll(newItems.first)
        notifyItemMoved(newItems.second, 0)
    }

    private fun notifyDataSetChanged() {
        items.forEach {
            val view = inflater.inflate(R.layout.item_currency, null)
            view.currencyId.text = it.id
            view.currencyText.setText(formatter.format(it.amount))
            addView(view)
        }
        firstView = getChildAt(0) as ViewGroup
    }

    private fun notifyItemRangeChanged(start: Int, size: Int) {
        (start..size).forEach {index ->
            val currencyItem = items[index]
            val title = (getChildAt(index) as ViewGroup).getChildAt(0) as TextView
            val amount = (getChildAt(index) as ViewGroup).getChildAt(1) as EditText
            title.text = currencyItem.id
            amount.setText(formatter.format(currencyItem.amount))
        }
    }

    private fun notifyItemMoved(from: Int, to: Int) {
        val childFrom = getChildAt(from)
        TransitionManager.beginDelayedTransition(this, auto)
        removeView(childFrom)
        ((getChildAt(0) as ViewGroup).getChildAt(1) as EditText)
            .removeTextChangedListener(watcher)
        ((childFrom as ViewGroup).getChildAt(1) as EditText)
            .addTextChangedListener(watcher)
        addView(childFrom, to)
        firstView = childFrom
    }

    interface CurrencyListener {
        fun onNewCurrencyClicked(newCurrency: Int)
        fun onAmountChanged(amount: String)
    }
}