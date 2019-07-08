package com.lectricas.curriencies.ui

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lectricas.curriencies.R
import kotlinx.android.synthetic.main.activity_new.containerView
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import timber.log.Timber

class NewCurrencyActivity: AppCompatActivity() {

    private val textWatcher = Watcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        (0..2).forEach {
            val view = layoutInflater.inflate(R.layout.item_currency, null)
            view.currencyId.text = it.toString()
            containerView.addView(view)
        }

        containerView.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                val view = containerView.getViewByCoordinates(event.x, event.y)
//                containerView.removeView(view)


//                ((containerView.getChildAt(0) as ViewGroup).getChildAt(1) as EditText)
//                    .removeTextChangedListener(textWatcher)
//
//                ((view as ViewGroup).getChildAt(1) as EditText)
//                    .addTextChangedListener(textWatcher)

                val view = layoutInflater.inflate(R.layout.item_currency, null)
                view.currencyId.text = "ADDED"

                containerView.addView(view, 0)
                view.requestFocus()
                return false
            }
        })
    }

    inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Timber.d("Changed $s")

            (containerView.childCount - 1 downTo 1)
                .map { containerView.getChildAt(it) }
                .forEach {
                    ((it as ViewGroup).getChildAt(1) as EditText).setText(s.toString())
                }

        }
    }

    fun ViewGroup.getViewByCoordinates(x: Float, y: Float) : View? {
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
}