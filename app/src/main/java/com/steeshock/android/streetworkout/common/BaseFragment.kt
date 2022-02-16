package com.steeshock.android.streetworkout.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.steeshock.android.streetworkout.presentation.views.MainActivity

abstract class BaseFragment : Fragment() {

    abstract fun injectComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        injectComponent()
        super.onAttach(context)
    }

    fun getBaseline(): View? {
        return (activity as? MainActivity)?.baseline
    }
}