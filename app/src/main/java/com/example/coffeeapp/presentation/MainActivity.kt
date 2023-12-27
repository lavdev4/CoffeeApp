package com.example.coffeeapp.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.ActivityMainBinding
import com.example.coffeeapp.di.MainActivitySubcomponent
import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.presentation.viewmodels.AppViewModelFactory
import com.example.coffeeapp.presentation.viewmodels.CafesViewModel
import com.example.coffeeapp.presentation.viewmodels.GraphState
import com.example.coffeeapp.presentation.viewmodels.GraphsViewModel
import com.example.coffeeapp.presentation.viewmodels.LoginViewModel
import com.example.coffeeapp.presentation.viewmodels.ProductsViewModel
import com.example.coffeeapp.presentation.viewmodels.RegisterViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val graphViewModel by viewModels<GraphsViewModel> { viewModelFactory }
    lateinit var mainActivitySubcomponent: MainActivitySubcomponent
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var destinationChangedListener: NavController.OnDestinationChangedListener? = null
    private val loginGraphId = R.id.login_graph
    private val cafesGraphId = R.id.cafes_graph
    private var currentGraphState: GraphState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivitySubcomponent = (application as CoffeeApplication).applicationComponent
            .activitySubcomponent()
            .build()
        mainActivitySubcomponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationGraph()
        listenToGraphChange()
        listenToDestinationChange()
        setupBackButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeDestinationListener()
    }

    private fun setupNavigationGraph() {
        navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)
        navGraph.setStartDestination(cafesGraphId)
        navController.graph = navGraph
        graphViewModel.setGraphState(GraphState.CafesGraph)
    }

    private fun listenToGraphChange() {
        graphViewModel.graphState.observe(this) { graphState ->
            currentGraphState = graphState
            when(graphState) {
                /** The view model of each screen is tied to the lifecycle
                 * of the navigation graph in which this screen is located.
                 * Thus, having made sure that we are in a specific graph,
                 * we can get a view model of any screen from the current
                 * graph and track their state.*/
                GraphState.LoginGraph -> observeLoginGraphErrors()
                GraphState.CafesGraph -> observeCafesGraphErrors()
            }
        }
    }

    private fun listenToDestinationChange() {
        destinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                destination.parent?.id?.let { setGraphState(it) }
                setScreenTitle(destination.label.toString())
            }
        navController.addOnDestinationChangedListener(
            destinationChangedListener as NavController.OnDestinationChangedListener
        )
    }

    private fun setGraphState(destinationId: Int) {
        when(destinationId) {
            loginGraphId -> {
                graphViewModel.setGraphState(GraphState.LoginGraph)
                setBackButtonVisible(false)
            }
            cafesGraphId -> {
                graphViewModel.setGraphState(GraphState.CafesGraph)
                setBackButtonVisible(true)
            }
        }
    }

    private fun setScreenTitle(title: String) {
        binding.title.text = title
    }

    private fun setBackButtonVisible(visible: Boolean) {
        binding.backButton.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener { navController.navigateUp() }
    }

    private fun removeDestinationListener() {
        destinationChangedListener?.let { navController.removeOnDestinationChangedListener(it) }
    }

    private fun observeLoginGraphErrors() {
        val lifecycleOwner = navController.getBackStackEntry(loginGraphId)
        val graphViewModelStoreOwner = navController.getViewModelStoreOwner(loginGraphId)
        val graphViewModelProvider =
            ViewModelProvider(graphViewModelStoreOwner, viewModelFactory)
        val loginViewModel = graphViewModelProvider[LoginViewModel::class.java]
        val registerViewModel = graphViewModelProvider[RegisterViewModel::class.java]
        getMediatorLiveData(
            loginViewModel.authErrors,
            registerViewModel.authErrors
        ).observe(lifecycleOwner) { reactToAuthError(it) }
    }

    private fun observeCafesGraphErrors() {
        Log.d("Destination", "graph observed: cafes_g")
        val lifecycleOwner = navController.getBackStackEntry(cafesGraphId)
        val graphViewModelStoreOwner = navController.getViewModelStoreOwner(cafesGraphId)
        val graphViewModelProvider =
            ViewModelProvider(graphViewModelStoreOwner, viewModelFactory)
        val cafesViewModel = graphViewModelProvider[CafesViewModel::class.java]
        val productsViewModel = graphViewModelProvider[ProductsViewModel::class.java]
        getMediatorLiveData(
            cafesViewModel.networkErrors,
            productsViewModel.networkErrors
        ).observe(lifecycleOwner) { reactToNetworkError(it) }
    }

    private fun reactToAuthError(error: AuthError) {
        when(error) {
            AuthError.LoginIsBlank -> showToast("Поле логина пусто")
            AuthError.PasswordIsBlank -> showToast("Поле пароля пусто")
            AuthError.RepeatPasswordIsBlank -> showToast("Поле подтверждения пароля пусто")
            AuthError.DataMismatch -> showToast("Неправильный e-mail или пароль")
            AuthError.DataRejected -> showToast("Этот e-mail занят")
            AuthError.PasswordConfirmFailed -> showToast("Повторный ввод пароля не совпал")
            is AuthError.AuthNetworkError -> reactToNetworkError(error.networkError)
        }
    }

    private fun reactToNetworkError(error: NetworkError) {
        when(error) {
            NetworkError.NoInternet -> {
                showToast("Нет интернет соединения")
                navigateToLoginGraph()
            }
            //handled in reactToAuthError()
            NetworkError.Unauthorised -> {}
            //handled in reactToAuthError()
            NetworkError.Rejected -> {}
            NetworkError.TokenExpired -> {
                showToast("Необходима повторная авторизация")
                navigateToLoginGraph()
            }
            NetworkError.NoTokenProvided -> navigateToLoginGraph()
            NetworkError.Error -> showToast("Ошибка сети")
        }
    }

    private fun navigateToLoginGraph() {
        val currentGraphId = navController.currentDestination?.parent?.id
        if (currentGraphId != loginGraphId) {
            val navParameters = NavOptions.Builder()
                .setPopUpTo(R.id.main_graph, true)
                .build()
            navController.navigate(R.id.login_graph, null, navParameters)
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun <T : Any?> getMediatorLiveData(
        vararg sources: LiveData<T>
    ): MediatorLiveData<T> {
        return MediatorLiveData<T>().apply {
            sources.forEach { source -> addSource(source) { value = it } }
        }
    }
}