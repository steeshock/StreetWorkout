package com.example.android.streetworkout.ui.training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TraningViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Тренировки"
    }
    val text: LiveData<String> = _text
}