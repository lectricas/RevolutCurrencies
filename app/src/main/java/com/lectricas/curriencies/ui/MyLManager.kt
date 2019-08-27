package com.lectricas.curriencies.ui

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.recyclerview.widget.RecyclerView.State
import timber.log.Timber

class MyLManager(context: Context?) : LinearLayoutManager(context) {

    override fun onItemsMoved(recyclerView: RecyclerView, from: Int, to: Int, itemCount: Int) {
        super.onItemsMoved(recyclerView, from, to, itemCount)
//        (getChildAt(0) as LinearLayout).post {
//            getChildAt(1)?.requestFocus()
//            getChildAt(1)?.showKeyboard()
//        }
//        scrollToPosition(0)
    }

    override fun onRequestChildFocus(parent: RecyclerView, state: State, child: View, focused: View?): Boolean {
        Timber.d("ScrollStateChanged ${(focused as? EditText)}")
        return super.onRequestChildFocus(parent, state, child, focused)
    }

    override fun onScrollStateChanged(state: Int) {
        Timber.d("ScrollStateChanged $state")
        super.onScrollStateChanged(state)
        Timber.d("ScrollStateChanged $state")
    }

    override fun endAnimation(view: View?) {
        super.endAnimation(view)
        Timber.d("EndAnimation")
    }
}