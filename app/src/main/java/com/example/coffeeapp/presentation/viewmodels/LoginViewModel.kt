package com.example.coffeeapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import com.example.coffeeapp.domain.usecases.LoginUseCase
import com.example.coffeeapp.presentation.viewmodels.contracts.AuthErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.LoginGraphViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.ScreenStateViewModel
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

    private val _screenState = MutableLiveData<ScreenState>(ScreenState.Initial)
    override val screenState: LiveData<ScreenState>
        get() = _screenState

    private val _authErrors = MutableLiveData<AuthError>()
    override val authErrors: LiveData<AuthError>
        get() = _authErrors.distinctUntilChanged()

    fun logIn(login: String, password: String): Deferred<Boolean> {
        return viewModelScope.async {
            _screenState.value = ScreenState.Loading
            val result = loginUseCase.logIn(login, password)
            if (result !is AuthResult.Success) {
                _authErrors.value = (result as AuthResult.Failure).error
                _screenState.value = ScreenState.Error
                false
            } else {
                _screenState.value = ScreenState.Presenting
                true
            }
        }
    }
}