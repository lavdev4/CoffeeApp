package com.example.coffeeapp.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.ActivityMainBinding
import com.example.coffeeapp.di.MainActivitySubcomponent
import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.LocationResultEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.presentation.viewmodels.GraphsViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.AuthErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.LocationStateViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.NetworkErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.ScreenStateViewModel
import com.example.coffeeapp.presentation.viewmodels.factories.ApplicationVMFactory
import com.example.coffeeapp.presentation.viewmodels.factories.CafesGraphVMFactory
import com.example.coffeeapp.presentation.viewmodels.factories.LoginGraphVMFactory
import com.example.coffeeapp.presentation.viewmodels.factories.MenuGraphVMFactory
import com.example.coffeeapp.presentation.viewmodels.states.Event
import com.example.coffeeapp.presentation.viewmodels.states.GraphState
import com.example.coffeeapp.presentation.viewmodels.states.ScreenState
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    lateinit var mainActivitySubcomponent: MainActivitySubcomponent
    @Inject lateinit var viewModelFactory: ApplicationVMFactory
    @Inject lateinit var loginGraphVMFactory: LoginGraphVMFactory
    @Inject lateinit var cafesGraphVMFactory: CafesGraphVMFactory
    @Inject lateinit var menuGraphVMFactory: MenuGraphVMFactory
    private val graphViewModel by viewModels<GraphsViewModel> { viewModelFactory }
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private var destinationChangedListener: NavController.OnDestinationChangedListener? = null
    private val loginGraphId = R.id.login_graph
    private val cafesGraphId = R.id.cafes_graph
    private val menuGraphId = R.id.menu_graph

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
            when(graphState) {
                /** The view model of each screen is tied to the lifecycle
                 * of the navigation graph in which this screen is located.
                 * Thus, having made sure that we are in a specific graph,
                 * we can get a view model of any screen from the current
                 * graph and track their state. */
                GraphState.LoginGraph -> observeGraph(loginGraphId)
                GraphState.CafesGraph -> observeGraph(cafesGraphId)
                GraphState.MenuGraph -> observeGraph(menuGraphId)
            }
        }
    }

    private fun listenToDestinationChange() {
        destinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                setScreenTitle(destination.label.toString())
                destination.parent?.id?.let {
                    setBackButtonVisible(it)
                    setGraphState(it)
                }
            }
        navController.addOnDestinationChangedListener(
            destinationChangedListener as NavController.OnDestinationChangedListener
        )
    }

    private fun setGraphState(destinationId: Int) {
        when(destinationId) {
            loginGraphId -> graphViewModel.setGraphState(GraphState.LoginGraph)
            cafesGraphId -> graphViewModel.setGraphState(GraphState.CafesGraph)
            menuGraphId -> graphViewModel.setGraphState(GraphState.MenuGraph)
        }
    }

    private fun setScreenTitle(title: String) { binding.title.text = title }

    private fun setBackButtonVisible(graphId: Int) {
        with(binding.backButton) {
            when (graphId) {
                loginGraphId -> visibility = View.GONE
                cafesGraphId -> visibility = View.VISIBLE
                menuGraphId -> visibility = View.VISIBLE
            }
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener { navController.navigateUp() }
    }

    private fun removeDestinationListener() {
        destinationChangedListener?.let { navController.removeOnDestinationChangedListener(it) }
    }

    private fun observeGraph(graphId: Int) {
        val lifecycleOwner = navController.getBackStackEntry(graphId)
        val graphViewModelStoreOwner = navController.getViewModelStoreOwner(graphId)
        val graphViewModels = getGraphViewModels(graphId, graphViewModelStoreOwner)
        observeNetworkErrors(graphViewModels, lifecycleOwner)
        observeAuthErrors(graphViewModels, lifecycleOwner)
        observeViewStates(graphViewModels, lifecycleOwner)
        observeLocationStates(graphViewModels, lifecycleOwner)
    }

    private fun observeNetworkErrors(viewModels: List<ViewModel>, lifecycleOwner: LifecycleOwner) {
        MediatorLiveData<Event<NetworkError>>().apply {
            viewModels.forEach { vm ->
                if (vm is NetworkErrorViewModel) addSource(vm.networkErrors) { value = it }
            }
        }.observe(lifecycleOwner) { error ->
            error.getContentIfNotHandled()?.let { reactToNetworkError(it) }
        }
    }

    private fun observeAuthErrors(viewModels: List<ViewModel>, lifecycleOwner: LifecycleOwner) {
        MediatorLiveData<Event<AuthError>>().apply {
            viewModels.forEach { vm ->
                if (vm is AuthErrorViewModel) addSource(vm.authErrors) { value = it }
            }
        }.observe(lifecycleOwner) { error ->
            error.getContentIfNotHandled()?.let { reactToAuthError(it) }
        }
    }

    private fun observeViewStates(viewModels: List<ViewModel>, lifecycleOwner: LifecycleOwner) {
        MediatorLiveData<Event<ScreenState>>().apply {
            viewModels.forEach { vm ->
                if (vm is ScreenStateViewModel) addSource(vm.screenState) { value = it }
            }
        }.observe(lifecycleOwner) { state ->
            state.getContentIfNotHandled()?.let { reactToStateChange(it) }
        }
    }

    private fun observeLocationStates(viewModels: List<ViewModel>, lifecycleOwner: LifecycleOwner) {
        MediatorLiveData<Event<LocationResultEntity>>().apply {
            viewModels.forEach { vm ->
                if (vm is LocationStateViewModel) addSource(vm.locationState) { value = it }
            }
        }.observe(lifecycleOwner) { state ->
            state.getContentIfNotHandled()?.let { reactToLocationStateChange(it) }
        }
    }

    private fun getGraphViewModels(
        graphId: Int,
        viewModelStoreOwner: ViewModelStoreOwner
    ): List<ViewModel> {
        return when (graphId) {
            loginGraphId -> with(loginGraphVMFactory) {
                viewModels.keys.map { ViewModelProvider(viewModelStoreOwner, this)[it] }
            }
            cafesGraphId -> with(cafesGraphVMFactory) {
                viewModels.keys.map { ViewModelProvider(viewModelStoreOwner, this)[it] }
            }
            menuGraphId -> with(menuGraphVMFactory) {
                viewModels.keys.map { ViewModelProvider(viewModelStoreOwner, this)[it] }
            }
            else -> throw RuntimeException("Graph with id{$graphId} is not handled in MainActivity.")
        }
    }

    private fun reactToAuthError(error: AuthError) {
        when (error) {
            AuthError.LoginIsBlank -> showToast(getString(R.string.toast_empty_login))
            AuthError.PasswordIsBlank -> showToast(getString(R.string.toast_password_blank))
            AuthError.RepeatPasswordIsBlank -> showToast(getString(R.string.toast_repeat_password_blank))
            AuthError.DataMismatch -> showToast(getString(R.string.toast_entry_data_mismatch))
            AuthError.DataRejected -> showToast(getString(R.string.toast_e_mail_occupied))
            AuthError.PasswordConfirmFailed -> showToast(getString(R.string.toast_wrong_confirmation_password))
            is AuthError.AuthNetworkError -> reactToNetworkError(error.networkError)
        }
    }

    private fun reactToNetworkError(error: NetworkError) {
        when (error) {
            NetworkError.NoInternet -> {
                showToast(getString(R.string.toast_no_internet))
                navigateToLoginGraph()
            }
            //handled in reactToAuthError()
            NetworkError.Unauthorised -> {}
            //handled in reactToAuthError()
            NetworkError.Rejected -> {}
            NetworkError.TokenExpired -> {
                showToast(getString(R.string.toast_required_authorisation))
                navigateToLoginGraph()
            }
            NetworkError.NoTokenProvided -> navigateToLoginGraph()
            NetworkError.UnknownError -> showToast(getString(R.string.toast_network_error))
        }
    }

    private fun reactToStateChange(state: ScreenState) {
        when (state) {
            ScreenState.Initial -> showProgress(false)
            ScreenState.Loading -> showProgress(true)
            ScreenState.Presenting -> showProgress(false)
            ScreenState.Error -> showProgress(false)
        }
    }

    private fun reactToLocationStateChange(state: LocationResultEntity) {
        when (state) {
            LocationResultEntity.Success -> {}
            LocationResultEntity.NoProvider -> {
                showToast(getString(R.string.toast_location_offline))
            }
            LocationResultEntity.NoPermission -> {}
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

    private fun showProgress(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
    }
}