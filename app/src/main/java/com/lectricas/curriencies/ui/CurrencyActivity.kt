package com.lectricas.curriencies.ui

import android.os.Bundle
import com.lectricas.curriencies.App
import com.lectricas.curriencies.R
import com.lectricas.curriencies.model.CurrencyModel
import com.lectricas.curriencies.ui.CurrencyContainer.CurrencyListener
import kotlinx.android.synthetic.main.activity_currency.containerView
import me.dmdev.rxpm.base.PmSupportActivity

class CurrencyActivity : PmSupportActivity<CurrencyPm>(), CurrencyListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)
        containerView.setListener(this)
    }

    override fun onBindPresentationModel(pm: CurrencyPm) {
        pm.currenciesState.bindTo {
            containerView.updateContent(it)
        }
    }

    override fun providePresentationModel(): CurrencyPm {
        return CurrencyPm(CurrencyModel((applicationContext as App).serverApi))
    }

    override fun onNewCurrencyClicked(newCurrency: Int) {
        presentationModel.pickCurrencyAction.consumer.accept(newCurrency)
    }

    override fun onAmountChanged(amount: String) {
        presentationModel.textChangedAction.consumer.accept(amount)
    }
}