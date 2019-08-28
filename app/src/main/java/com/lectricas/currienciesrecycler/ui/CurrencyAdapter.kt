package com.lectricas.currienciesrecycler.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lectricas.currienciesrecycler.R
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import kotlinx.android.synthetic.main.item_currency.view.currencyText
import timber.log.Timber

class CurrencyAdapter(private val function: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<CurrencyItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OtherViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
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
            itemView.currencyText.addTextChangedListener(CurrencyTextWatcher(this))
            itemView.currencyId.text = currencyItem.id
            val amount = if (currencyItem.amount == 0.0) {
                ""
            } else {
                String.format("%.2f", currencyItem.amount)
            }
            itemView.currencyText.setText(amount)
        }
    }

    fun updateItems(newItems: List<CurrencyItem>) {
        val diffResult = DiffUtil.calculateDiff(CurrenciesDiffUtil(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class CurrenciesDiffUtil(private val old: List<CurrencyItem>, private val new: List<CurrencyItem>) :
        DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].id == new[newItemPosition].id
        }

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (old[oldItemPosition].firstItem && new[newItemPosition].firstItem) {
                return true
            } else
            return old[oldItemPosition].amount == new[newItemPosition].amount
        }
    }

    inner class CurrencyTextWatcher(private val holder: OtherViewHolder) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (holder.adapterPosition == 0 && s.toString().isNotEmpty()) {
                function.invoke(s.toString())
                Timber.d("Invoke ${s.toString()}")
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun getItemId(position: Int): Long {
        Timber.d("id ${items[position].id} ${items[position].id.hashCode().toLong()}")
        Timber.d("")
        return items[position].id.hashCode().toLong()
    }
}