package com.lectricas.currienciesrecycler.ui

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.lectricas.currienciesrecycler.App
import com.lectricas.currienciesrecycler.R
import com.lectricas.currienciesrecycler.model.CurrencyModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_currency.currenciesRecycler
import kotlinx.android.synthetic.main.activity_currency.error
import kotlinx.android.synthetic.main.activity_currency.progress
import kotlinx.android.synthetic.main.activity_currency.tryAgain
import me.dmdev.rxpm.base.PmSupportActivity
import java.util.concurrent.TimeUnit.MILLISECONDS

class CurrencyActivity : PmSupportActivity<CurrencyPm>() {

    private val currencyAdapter = CurrencyAdapter {
        presentationModel.textChangedAction.consumer.accept(it)
    }.apply { setHasStableIds(true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)
        with(currenciesRecycler) {
            adapter = currencyAdapter
            layoutManager = LinearLayoutManager(this@CurrencyActivity)
            addOnItemTouchListener(RecyclerViewTouchListener(this@CurrencyActivity))
            setHasFixedSize(true)
            addOnScrollListener(ScrollListener())
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun onBindPresentationModel(pm: CurrencyPm) {
        pm.currencyProgress.bindTo(progress.visibility())
        pm.errorState.bindTo(error.visibility())

        pm.currenciesState.bindTo {
            currencyAdapter.updateItems(it)
        }

        pm.errorCommand.bindTo {
            //todo change to snack maybe
            Toast.makeText(this, getString(R.string.error_short), Toast.LENGTH_LONG).show()
        }
        tryAgain.clicks().bindTo(pm.tryAgain)

}

    override fun providePresentationModel(): CurrencyPm {
        return CurrencyPm(CurrencyModel((applicationContext as App).serverApi))
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
                    editText.showKeyboard()
                    editText.setSelection(editText.text.length)
                    return true
                }
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                (recyclerView.getChildAt(0) as? LinearLayout)?.let {
                    val editText = it.getChildAt(1) as EditText
                    editText.hideKeyboard()
                }
            }
        }
    }
}