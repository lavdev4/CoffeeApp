package com.example.coffeeapp.presentation.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.example.coffeeapp.LoginGraphDirections
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.FragmentLoginBinding
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.viewmodels.LoginViewModel
import com.example.coffeeapp.presentation.viewmodels.factories.LoginGraphVMFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginScreen : Fragment() {
    @Inject
    lateinit var viewModelFactory: LoginGraphVMFactory
    private lateinit var navController: NavController
    private val viewModel by navGraphViewModels<LoginViewModel>(R.id.login_graph) {
        viewModelFactory
    }
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivitySubcomponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupLoginButton()
        setupRegisterButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            val login = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            lifecycleScope.launch {
                val result = viewModel.logIn(login, password).await()
                if (result) navigateToCafesGraph()
            }
        }
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            navigateToRegisterScreen()
        }
    }

    private fun navigateToRegisterScreen() {
        val action = LoginScreenDirections.actionLoginScreenToRegisterScreen()
        navController.navigate(action)
    }

    private fun navigateToCafesGraph() {
        val action = LoginGraphDirections.actionGlobalCafesGraph()
        navController.navigate(action)
    }
}