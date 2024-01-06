package com.example.coffeeapp.presentation.screens

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.FragmentCafesBinding
import com.example.coffeeapp.domain.entities.LocationResultEntity
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.adapters.CafesAdapter
import com.example.coffeeapp.presentation.adapters.decorators.VerticalMarginItemDecoration
import com.example.coffeeapp.presentation.viewmodels.CafesViewModel
import com.example.coffeeapp.presentation.viewmodels.factories.CafesGraphVMFactory
import javax.inject.Inject

class CafesScreen : Fragment() {
    @Inject lateinit var viewModelFactory: CafesGraphVMFactory
    private val viewModel by navGraphViewModels<CafesViewModel>(R.id.cafes_graph) { viewModelFactory }
    private lateinit var navController: NavController
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private var _binding: FragmentCafesBinding? = null
    private val binding: FragmentCafesBinding
        get() = _binding ?: throw RuntimeException("FragmentCafesBinding is null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivitySubcomponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCafesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionRequest = createLocationPermissionsRequest()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val cafesAdapter = setCafesAdapter()
        observeCafesList(cafesAdapter)
        observeLocationStatus()
        setupMapButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeCafesList(adapter: CafesAdapter) {
        viewModel.cafesList.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun observeLocationStatus() {
        viewModel.locationState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { state ->
                if (state == LocationResultEntity.NoPermission) requestLocationPermission()
            }
        }
    }

    private fun setCafesAdapter(): CafesAdapter {
        val cafesAdapter = CafesAdapter { itemId -> navigateToCafeMenu(itemId) }
        with(binding.cafesList) {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            addItemDecoration(VerticalMarginItemDecoration(6f))
            adapter = cafesAdapter
        }
        return cafesAdapter
    }

    private fun setupMapButton() {
        binding.mapButton.setOnClickListener { navigateToMap() }
    }

    private fun navigateToCafeMenu(locationId: Int) {
        val action = CafesScreenDirections.actionGlobalMenuGraph(locationId)
        navController.navigate(action)
    }

    private fun navigateToMap() {
        val action = CafesScreenDirections.actionCafesScreenToMapScreen()
        navController.navigate(action)
    }

    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        locationPermissionRequest.launch(permissions)
    }

    private fun createLocationPermissionsRequest(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.forEach { permission ->
                when (permission.key) {
                    "android.permission.ACCESS_COARSE_LOCATION" -> {
                        if (permission.value) viewModel.requestLocations()
                    }

                    "android.permission.ACCESS_FINE_LOCATION" -> {}
                }
            }
        }
    }
}