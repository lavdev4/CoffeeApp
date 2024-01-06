package com.example.coffeeapp.presentation.viewmodels.states

sealed class GraphState {
    data object LoginGraph : GraphState()
    data object CafesGraph : GraphState()
    data object MenuGraph : GraphState()
}