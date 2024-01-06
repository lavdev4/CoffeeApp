package com.example.coffeeapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import com.example.coffeeapp.domain.usecases.LoginUseCase
import com.example.coffeeapp.presentation.viewmodels.contracts.AuthErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.LoginGraphViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.ScreenStateViewModel
import com.example.coffeeapp.presentation.viewmodels.states.Event
import com.example.coffeeapp.presentation.viewmodels.states.ScreenState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel(),
    LoginGraphViewModel,
    ScreenStateViewModel,
    AuthErrorViewModel {

    private val _screenState = MutableLiveData<Event<ScreenState>>(Event(ScreenState.Initial))
    override val screenState: LiveData<Event<ScreenState>>
        get() = _screenState

    private val _authErrors = MutableLiveData<Event<AuthError>>()
    override val authErrors: LiveData<Event<AuthError>>
        get() = _authErrors

    fun logIn(login: String, password: String): Deferred<Boolean> {
        return viewModelScope.async {
            _screenState.value = Event(ScreenState.Loading)
            when (val result = loginUseCase.logIn(login, password)) {
                AuthResult.Success -> {
                    _screenState.value = Event(ScreenState.Presenting)
                    return@async true
                }
                is AuthResult.Failure -> {
                    _authErrors.value = Event(result.error)
                    _screenState.value = Event(ScreenState.Error)
                    return@async false
                }
            }
        }
    }
}