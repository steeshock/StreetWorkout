package com.example.android.streetworkout

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.streetworkout.data.Repository

abstract class BaseFragment : Fragment() {

    protected var mRepository: Repository? = null
    protected abstract fun getViewModel(): ViewModel?

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Repository.RepositoryOwner) {
            mRepository = context.obtainRepository()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}