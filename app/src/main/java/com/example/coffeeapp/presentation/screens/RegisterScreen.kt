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
import com.example.coffeeapp.databinding.FragmentRegisterBinding
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.viewmodels.AppViewModelFactory
import com.example.coffeeapp.presentation.viewmodels.RegisterViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterScreen : Fragment() {
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private lateinit var navController: NavController
    private val viewModel by navGraphViewModels<RegisterViewModel>(R.id.login_graph) {
        viewModelFactory
    }
    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding ?: throw RuntimeException("FragmentRegisterBinding is null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivitySubcomponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupRegisterButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val login = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            val repeatPassword = binding.repeatPasswordField.text.toString()
            lifecycleScope.launch {
                val result = viewModel.register(login, password, repeatPassword).await()
                if (result) navigateToCafesGraph()
            }
        }
    }

    private fun navigateToCafesGraph() {
        val action = LoginGraphDirections.actionGlobalCafesGraph()
        navController.navigate(action)
    }
}