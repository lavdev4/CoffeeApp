package com.example.coffeeapp.presentation.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coffeeapp.MenuGraphArgs
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.FragmentMenuBinding
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.adapters.ProductsAdapter
import com.example.coffeeapp.presentation.adapters.decorators.GridMarginItemDecoration
import com.example.coffeeapp.presentation.viewmodels.ProductsViewModel
import com.example.coffeeapp.presentation.viewmodels.factories.MenuGraphVMFactory
import javax.inject.Inject

class MenuScreen : Fragment() {
    @Inject lateinit var viewModelFactory: MenuGraphVMFactory
    private val viewModel by navGraphViewModels<ProductsViewModel>(R.id.menu_graph) { viewModelFactory }
    private lateinit var navController: NavController
    private val args: MenuGraphArgs by navArgs()
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw RuntimeException("FragmentMenuBinding is null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivitySubcomponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModel.initialise(args.locationId)
        val adapter = setMenuAdapter()
        observeMenuList(adapter)
        setupPaymentButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupPaymentButton() {
        binding.paymentButton.setOnClickListener { navigateToCartScreen() }
    }

    private fun observeMenuList(adapter: ProductsAdapter) {
        viewModel.productsList.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun setMenuAdapter(): ProductsAdapter {
        val menuAdapter = ProductsAdapter { item, quantity ->
            viewModel.updateSelectedProducts(item, quantity)
        }
        with(binding.productList) {
            val spanCount = 2
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            addItemDecoration(GridMarginItemDecoration(13f, spanCount))
            adapter = menuAdapter
        }
        return menuAdapter
    }

    private fun navigateToCartScreen() {
        val action = MenuScreenDirections.actionMenuScreenToCartScreen()
        navController.navigate(action)
    }
}