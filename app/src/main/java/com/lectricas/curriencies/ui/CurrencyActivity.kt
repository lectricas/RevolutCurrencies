package com.lectricas.curriencies.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lectricas.curriencies.App
import com.lectricas.curriencies.model.CurrencyModel
import com.lectricas.curriencies.R.layout
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
        setContentView(layout.activity_currency)
        with(currenciesRecyclerView) {
            adapter = currenciesAdapter
            layoutManager = LinearLayoutManager(context)
            isFocusable = false
            addOnItemTouchListener(RecyclerViewTouchListener(context))
        }
    }

    override fun onBindPresentationModel(pm: CurrencyPm) {
        pm.currenciesState.bindTo {
            currenciesAdapter.updateItems(it.second)
            if (it.first) {
                currenciesRecyclerView.scrollToPosition(0)
                currenciesRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object: OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        Timber.d("OnGlobalLayout")
                        currenciesRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val first = (currenciesRecyclerView[0] as ViewGroup)
                        for (i in 0 until first.childCount) {
                            val child = first[i]
                            if (child is EditText) {
//                                child.setSelection(child.text.length)
//                                child.showkeyboard()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun providePresentationModel(): CurrencyPm {
        return CurrencyPm(CurrencyModel((applicationContext as App).serverApi))
    }

    inner class CurrenciesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val typeFirst = 0
        private val typeOther = 1

        private val items = mutableListOf<CurrencyItem>()

        override fun getItemViewType(position: Int): Int {
            return if (items[position].firstItem) {
                typeFirst
            } else {
                typeOther
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == typeFirst) {
                return FirstViewHolder(
                    LayoutInflater.from(parent.context).inflate(layout.item_currency, parent, false)
                )
            } else {
                return OtherViewHolder(
                    LayoutInflater.from(parent.context).inflate(layout.item_currency, parent, false)
                )
            }
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is FirstViewHolder) {
                holder.bind(items[position])
            }
            if (holder is OtherViewHolder) {
                holder.bind(items[position])
            }
        }


        inner class FirstViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            init {
                itemView.currencyText.isClickable = true
                itemView.currencyText.addTextChangedListener(textWatcher)
            }

            fun bind(currencyItem: CurrencyItem) {
                Timber.d("OnBindViewHolder")
                itemView.currencyId.text = currencyItem.id.toString()
                itemView.currencyText.setText(currencyItem.amount)

            }
        }

        inner class OtherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    inner class CurrencyTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            presentationModel.textChangedAction.consumer.accept(s.toString())
            Timber.d("TextChanged $s")
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
