package com.example.android.streetworkout.ui.places

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlacesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Добро пожаловать в Уличные тренировки!"
    }
    val text: LiveData<String> = _text
}