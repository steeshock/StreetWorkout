package com.example.android.streetworkout.ui.places

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.streetworkout.model.PlaceObject
import kotlin.random.Random

class PlacesViewModel : ViewModel() {

    val itemsMock = listOf(
        PlaceObject("Александр Пушкин", "https://picsum.photos/301/200"),
        PlaceObject("Михаил Лермонтов", "https://picsum.photos/302/200"),
        PlaceObject("Александр Блок", "https://picsum.photos/303/200"),
        PlaceObject("Николай Некрасов", "https://picsum.photos/304/200"),
        PlaceObject("Фёдор Тютчев", "https://picsum.photos/305/200"),
        PlaceObject("Сергей Есенин", "https://picsum.photos/306/200"),
        PlaceObject("Владимир Маяковский", "https://picsum.photos/307/200")
    )

    private val _text = MutableLiveData<String>().apply {
        value = "Добро пожаловать в Уличные тренировки!"
    }
    val text: LiveData<String> = _text
}