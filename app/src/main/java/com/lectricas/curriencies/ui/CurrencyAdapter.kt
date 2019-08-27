package com.lectricas.curriencies.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lectricas.curriencies.R
import kotlinx.android.synthetic.main.item_currency.view.currencyId
import kotlinx.android.synthetic.main.item_currency.view.currencyText
import timber.log.Timber

class CurrencyAdapter(private val function: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private val textWatcher = CurrencyTextWatcher()

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
            itemView.currencyId.text = currencyItem.id
            itemView.currencyText.setText(currencyItem.amount.toString())
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
            return old[oldItemPosition].amount == new[newItemPosition].amount
        }
    }
}