package com.lectricas.curriencies

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_currency.currenciesRecyclerView
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import kotlinx.android.synthetic.main.item_currency.view.currencyText
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber

class CurrencyActivity : PmSupportActivity<CurrencyPm>() {

    private val currenciesAdapter = CurrenciesAdapter()
    private val textWatcher = CurrencyTextWatcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(currenciesAdapter.toString())
        Timber.d(this.toString())
        setContentView(R.layout.activity_currency)
        with(currenciesRecyclerView) {
            adapter = currenciesAdapter
            layoutManager = LinearLayoutManager(context)
            isFocusable = false
            addOnItemTouchListener(RecyclerViewTouchListener(context))
        }
    }

    override fun onBindPresentationModel(pm: CurrencyPm) {
        pm.currenciesState.bindTo {
            currenciesAdapter.updateItems(it)
            currenciesRecyclerView.scrollToPosition(0)
            currenciesRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    Timber.d("OnGlobalLayout") //todo now working, add onLayoutChildren
                    val first = currenciesRecyclerView.getChildAt(0) as ViewGroup
                    val second = currenciesRecyclerView.getChildAt(1) as ViewGroup
                    for (i in 0 until first.childCount) {
                        val innerChild = first.getChildAt(i)
                        if (innerChild is EditText) {
                            innerChild.isClickable = true
                            innerChild.isLongClickable = true
                            innerChild.requestFocus()
                            innerChild.addTextChangedListener(textWatcher)
                        }
                    }

                    for (i in 0 until second.childCount) {
                        val innerChild = second.getChildAt(i)
                        if (innerChild is EditText) {
                            innerChild.isClickable = false
                            innerChild.isLongClickable = false
//                            innerChild.removeTextChangedListener(textWatcher)
                        }
                    }
                    currenciesRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    override fun providePresentationModel(): CurrencyPm {
        return CurrencyPm()
    }

    inner class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

        private val items = mutableListOf<CurrencyItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
            return CurrencyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
            )
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holderCurrency: CurrencyViewHolder, position: Int) =
            holderCurrency.bind(items[position])

        inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//            private val item get() = items[adapterPosition]

            //            init {
//                itemView.setOnClickListener {
//                    item.passTo(presentationModel.moveToFirstAction.consumer)
//                }
//
//                itemView.currencyText.addTextChangedListener(object: TextWatcher {
//                    override fun afterTextChanged(s: Editable?) {}
//
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                        if (itemView.currencyText.hasFocus()) {
//                            presentationModel.textChangedAction.consumer.accept(s.toString())
//                        }
//                    }
//                })
//            }
//
            fun bind(currencyItem: CurrencyItem) {
                itemView.currencyId.text = currencyItem.id.toString()
                itemView.currencyText.setText(currencyItem.amount)
            }
        }

        fun updateItems(newItems: List<CurrencyItem>) {
            val diffResult = DiffUtil.calculateDiff(CurrenciesDiffUtil(items, newItems))
            items.clear()
            items.addAll(newItems)
            diffResult.dispatchUpdatesTo(this)
        }

        inner class CurrenciesDiffUtil(private val old: List<CurrencyItem>, private val new: List<CurrencyItem>) :
            DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition].id == new[newItemPosition].id
            }

            override fun getOldListSize() = old.size

            override fun getNewListSize() = new.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return if (new[newItemPosition].firstItem) {
                    true
                } else {
                    old[oldItemPosition].amount == new[newItemPosition].amount
                }
            }
        }
    }

    inner class CurrencyTextWatcher(): TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            presentationModel.textChangedAction.consumer.accept(s.toString())
        }
    }

    inner class RecyclerViewTouchListener(context: Context) : RecyclerView.OnItemTouchListener {

        private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return true
            }
        })

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val clicked = rv.findChildViewUnder(e.x, e.y)
            if (clicked != null && gestureDetector.onTouchEvent(e)) {
                val position = rv.getChildAdapterPosition(clicked)
                presentationModel.moveToFirstAction.consumer.accept(position)
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }
}
