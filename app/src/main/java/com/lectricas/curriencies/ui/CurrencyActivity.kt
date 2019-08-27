package com.lectricas.curriencies.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.lectricas.curriencies.App
import com.lectricas.curriencies.R
import com.lectricas.curriencies.model.CurrencyModel
import com.lectricas.curriencies.storage.DummyApi
import kotlinx.android.synthetic.main.activity_currency.containerView
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING

class CurrencyActivity : PmSupportActivity<CurrencyPm>() {

    private val currencyAdapter = CurrencyAdapter {
        presentationModel.textChangedAction.consumer.accept(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)
        with(containerView) {
            adapter = currencyAdapter
            layoutManager = LinearLayoutManager(this@CurrencyActivity)
            addOnItemTouchListener(RecyclerViewTouchListener(this@CurrencyActivity))
            addOnScrollListener(ScrollListener())
        }
    }

    override fun onBindPresentationModel(pm: CurrencyPm) {
        pm.currenciesState.bindTo {
            currencyAdapter.updateItems(it)
        }
    }

    override fun providePresentationModel(): CurrencyPm {
        return CurrencyPm(CurrencyModel((applicationContext as App).serverApi, DummyApi()))
    }

    inner class RecyclerViewTouchListener(context: Context) : RecyclerView.OnItemTouchListener {

        private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return true
            }
        })

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (gestureDetector.onTouchEvent(e)) {
                val clicked: View? = rv.findChildViewUnder(e.x, e.y)
                (clicked as? LinearLayout)?.let {
                    val position = rv.getChildAdapterPosition(it)
                    presentationModel.pickCurrencyAction.consumer.accept(position)
                    val editText = it.getChildAt(1) as EditText
                    (editText).post {
                        editText.requestFocusFromTouch()
                        editText.setSelection(editText.length())
                        val lManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        lManager.showSoftInput(editText, 0)
                    }
                    return true
                }
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_SETTLING) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(View(this@CurrencyActivity).windowToken, 0)
            }
        }
    }
}