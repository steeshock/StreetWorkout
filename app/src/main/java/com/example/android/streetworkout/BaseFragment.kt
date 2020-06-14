package com.example.android.streetworkout

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.streetworkout.data.Storage

abstract class BaseFragment : Fragment() {

    protected var mStorage: Storage? = null

    protected abstract fun getFactory(): ViewModelProvider.NewInstanceFactory?
    protected abstract fun getViewModel(): ViewModel?


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Storage.StorageOwner) {
            mStorage = context.obtainStorage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}