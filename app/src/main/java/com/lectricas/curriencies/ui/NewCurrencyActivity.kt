package com.lectricas.curriencies.ui

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lectricas.curriencies.R
import kotlinx.android.synthetic.main.activity_new.containerView
import kotlinx.android.synthetic.main.activity_new.scrollView
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import timber.log.Timber
import android.R.attr.bottom
import android.R.attr.right
import android.R.attr.top
import android.R.attr.left
import android.widget.LinearLayout.LayoutParams

class NewCurrencyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        (0..32).forEach {
            val view = layoutInflater.inflate(R.layout.item_currency, null)
            view.currencyId.text = it.toString()

            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            params.setMargins(50, 50, 50, 50)
            view.layoutParams = params
            containerView.addView(view)
        }
    }
}