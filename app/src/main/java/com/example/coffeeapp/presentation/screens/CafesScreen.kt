package com.example.coffeeapp.presentation.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.FragmentCafesBinding
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.adapters.CafesAdapter
import com.example.coffeeapp.presentation.adapters.decorators.VerticalMarginItemDecoration
import com.example.coffeeapp.presentation.viewmodels.CafesViewModel
import com.example.coffeeapp.presentation.viewmodels.factories.CafesGraphVMFactory
import javax.inject.Inject

class CafesScreen : Fragment() {
    @Inject
    lateinit var viewModelFactory: CafesGraphVMFactory
    private lateinit var navController: NavController
    private val viewModel by navGraphViewModels<CafesViewModel>(R.id.cafes_graph) {
        viewModelFactory
    }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        observeCafesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeCafesList() {
        viewModel.cafesList.observe(viewLifecycleOwner) { setCafesAdapter(it) }
    }

    private fun setCafesAdapter(
        data: List<CafeEntity>,
    ) {
        val cafesAdapter = CafesAdapter(data) { itemId -> navigateToCafeMenu(itemId) }
        with(binding.cafesList) {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            addItemDecoration(VerticalMarginItemDecoration(6f))
            adapter = cafesAdapter
        }
    }

    private fun navigateToCafeMenu(locationId: Int) {
        val action = CafesScreenDirections.actionCafesScreenToMenuScreen(locationId)
        navController.navigate(action)
    }
}