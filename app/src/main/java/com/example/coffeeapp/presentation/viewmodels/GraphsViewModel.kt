package com.example.coffeeapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.example.coffeeapp.presentation.viewmodels.states.GraphState
import javax.inject.Inject

class GraphsViewModel @Inject constructor() : ViewModel() {

    private val _graphState = MutableLiveData<GraphState>()
    val graphState: LiveData<GraphState>
        get() = _graphState.distinctUntilChanged()

    fun setGraphState(graph: GraphState) {
        _graphState.value = graph
    }
}