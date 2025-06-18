package com.example.saborchef.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.saborchef.model.Cronograma
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class SharedCursoViewModel : ViewModel() {
    var cronogramas by mutableStateOf<List<Cronograma>>(emptyList())
}
