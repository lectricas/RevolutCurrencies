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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.transition.addListener
import androidx.core.view.GestureDetectorCompat
import timber.log.Timber

class CurrencyContainer : LinearLayout {

    private val textWatcher = Watcher()

    private val mDetector: GestureDetectorCompat

    constructor(context: Context) : super(context) {
        mDetector = GestureDetectorCompat(context, MyGestureListener())
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mDetector = GestureDetectorCompat(context, MyGestureListener())
    }

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId) {
        mDetector = GestureDetectorCompat(context, MyGestureListener())
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?) = true

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            singleTap(e)
            return true
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) = true

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return mDetector.onTouchEvent(ev)
    }

    fun singleTap(event: MotionEvent) {

        val view = getViewByCoordinates(event.x, event.y) as ViewGroup
        val auto: Transition = AutoTransition()

        auto.addListener(onEnd = {
            Timber.d("OnEnd")
            (parent as ScrollView).smoothScrollTo(0,0)
            (view.childCount - 1 downTo 1).forEach {
                (view.getChildAt(it) as AppCompatEditText).requestFocus()

            }
        })

        TransitionManager.beginDelayedTransition(this, auto)

        removeView(view)

        ((getChildAt(0) as ViewGroup).getChildAt(1) as EditText)
            .removeTextChangedListener(textWatcher)

        ((view as ViewGroup).getChildAt(1) as EditText)
            .addTextChangedListener(textWatcher)

        addView(view, 0)

    }

    fun ViewGroup.getViewByCoordinates(x: Float, y: Float): View? {
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

    inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Timber.d("Changed $s")

            (childCount - 1 downTo 1)
                .map { getChildAt(it) }
                .forEach {
                    ((it as ViewGroup).getChildAt(1) as EditText).setText(s.toString())
                }
        }
    }

//    fun View.showkeyboard() {
//        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                post {
//                    if (requestFocus()) {
//                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                        imm.showSoftInput(this@showkeyboard, 0)
//                    }
//                }
//                viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        })
//    }
}