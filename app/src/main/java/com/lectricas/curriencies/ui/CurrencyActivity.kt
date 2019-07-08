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
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
import com.jakewharton.rxbinding2.widget.textChanges
import com.lectricas.curriencies.App
import com.lectricas.curriencies.R.layout
import com.lectricas.curriencies.model.CurrencyModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_currency.currenciesRecyclerView
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import kotlinx.android.synthetic.main.item_currency.view.currencyText
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber
import java.text.DecimalFormat

class CurrencyActivity : PmSupportActivity<CurrencyPm>() {

    private val currenciesAdapter = CurrenciesAdapter()
    private val textWatcher = CurrencyTextWatcher()

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(currenciesAdapter.toString())
        Timber.d(this.toString())
        setContentView(layout.activity_currency)
        with(currenciesRecyclerView) {
            adapter = currenciesAdapter
            layoutManager = MyManager(context)
            isFocusable = false
            addOnItemTouchListener(RecyclerViewTouchListener(context))
//            recycledViewPool.setMaxRecycledViews(1, 0);
        }

//        currenciesRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                currenciesRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                if (currenciesRecyclerView.childCount > 0) {
//                    val zero = currenciesRecyclerView[1]
//                    val first = currenciesRecyclerView[0]
//                    val zeroEdit = (zero as ViewGroup).getChildAt(0) as TextView
//                    val firstEdit = (first as ViewGroup).getChildAt(0) as TextView
//                    Timber.d("zero = ${zeroEdit.text}, first = ${firstEdit.text}")
//                }
//            }
//        })
    }

    inner class MyManager(context: Context) : LinearLayoutManager(context) {

        override fun onItemsMoved(recyclerView: RecyclerView, from: Int, to: Int, itemCount: Int) {
            super.onItemsMoved(recyclerView, from, to, itemCount)
            Timber.d("OnItemsMoved")

//            for (i in 0 until zero.childCount) {
//                val childZero = zero.getChildAt(i)
//                val childOne = first.getChildAt(i)
//                if (childZero is EditText) {
//                    childZero.addTextChangedListener(textWatcher)
//                }
//                if (childOne is EditText) {
//                    childOne.removeTextChangedListener(textWatcher)
//                }
//            }
        }

//        override fun attachView(child: View, index: Int) {
//            Timber.d(((child as ViewGroup).getChildAt(0) as TextView).text.toString() + index.toString())
//            super.attachView(child, index)
//            Timber.d("AttachView")
//        }


//
//        override fun endAnimation(view: View?) {
//            super.endAnimation(view)
//            Timber.d("EndAnimation ${view.toString()}")
//        }

        override fun onLayoutCompleted(state: State?) {
            super.onLayoutCompleted(state)
            if (currenciesRecyclerView.childCount > 0) {
                val zero = currenciesRecyclerView[1]
                val first = currenciesRecyclerView[0]
                val zeroEdit = (zero as ViewGroup).getChildAt(0) as TextView
                val firstEdit = (first as ViewGroup).getChildAt(0) as TextView
                Timber.d("zero = ${zeroEdit.text}, first = ${firstEdit.text}")
            }
        }

//        override fun onLayoutChildren(recycler: Recycler?, state: State?) {
//            super.onLayoutChildren(recycler, state)
//            Timber.d("FinishChilder")
//        }
    }

    override fun onBindPresentationModel(pm: CurrencyPm) {
        pm.currenciesState.bindTo {
            currenciesAdapter.updateContent(it)
        }
    }

    override fun providePresentationModel(): CurrencyPm {
        return CurrencyPm(CurrencyModel((applicationContext as App).serverApi))
    }

    inner class CurrenciesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var formatter = DecimalFormat("#0.000")

        private val typeOther = 1

        private val items = mutableListOf<CurrencyItem>()

        override fun getItemViewType(position: Int): Int {
            return typeOther
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return OtherViewHolder(
                LayoutInflater.from(parent.context).inflate(layout.item_currency, parent, false)
            )
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is OtherViewHolder) {
                holder.bind(items[position])
            }
        }

        inner class OtherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(currencyItem: CurrencyItem) {
//                Timber.d("bind ${currencyItem.id}")
                itemView.currencyId.text = currencyItem.id + " other"
                itemView.currencyText.setText(formatter.format(currencyItem.amount))
            }
        }

        fun updateContent(newItems: Pair<List<CurrencyItem>, Int>) {
            if (items.isEmpty()) {
                items.addAll(newItems.first)
                notifyDataSetChanged()
                return
            }
            if (newItems.second == 0) {
                items.clear()
                items.addAll(newItems.first)
                notifyItemRangeChanged(1, items.size - 1)
                return
            }
            items.clear()
            items.addAll(newItems.first)
            notifyItemMoved(newItems.second, 0)
        }
    }

    inner class CurrencyTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Timber.d("Changed $s")
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
                presentationModel.pickCurrencyAction.consumer.accept(position)
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }
}
//                disposable?.dispose()
//                if (currencyItem.firstItem) {
//                    Timber.d("bind first ${currencyItem.id}")
//                    disposable = itemView.currencyText
//                        .textChanges()
//                        .skipInitialValue()
//                        .subscribe {
//                            presentationModel.textChangedAction.consumer.accept(it.toString())
//                        }
//                }
//inner class CurrenciesDiffUtil(private val old: List<CurrencyItem>, private val new: List<CurrencyItem>) :
//            DiffUtil.Callback() {
//
//            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return old[oldItemPosition].id == new[newItemPosition].id
//            }
//
//            override fun getOldListSize() = old.size
//
//            override fun getNewListSize() = new.size
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return if (new[newItemPosition].firstItem) {
//                    false
//                } else {
//                    return old[oldItemPosition].amount == new[newItemPosition].amount
//                }
//            }
//        }

//diffResult.dispatchUpdatesTo(this)
//val diffResult = DiffUtil.calculateDiff(CurrenciesDiffUtil(items, newItems))