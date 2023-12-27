package com.example.coffeeapp.presentation.viewmodels

sealed class GraphState {
    data object LoginGraph : GraphState()
    data object CafesGraph : GraphState()
}