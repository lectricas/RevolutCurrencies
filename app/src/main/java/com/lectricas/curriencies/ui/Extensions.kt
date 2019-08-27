package com.lectricas.curriencies.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
//    val function = {
//        if (requestFocus()) {
//            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(this, 0)
//        }
//    }
//
//    function.invoke()
//    post {
//        function.invoke()
//    }
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    val function = {
        clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    function.invoke()
    post {
        function.invoke()
    }
}