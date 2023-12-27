package com.example.coffeeapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.RegisterUseCase
import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authErrors = MutableLiveData<AuthError>()
    val authErrors: LiveData<AuthError>
        get() = _authErrors.distinctUntilChanged()

    fun register(login: String, password: String, repeatPassword: String): Deferred<Boolean> {
        return viewModelScope.async {
            val result = registerUseCase.register(login, password, repeatPassword)
            if (result !is AuthResult.Success) {
                _authErrors.value = (result as AuthResult.Failure).error
                false
            } else
                true
        }
    }
}