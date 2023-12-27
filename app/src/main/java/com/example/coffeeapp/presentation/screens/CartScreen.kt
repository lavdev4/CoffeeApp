package com.example.coffeeapp.presentation.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.FragmentCartBinding
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.adapters.CartProductsAdapter
import com.example.coffeeapp.presentation.adapters.VerticalMarginItemDecoration
import com.example.coffeeapp.presentation.viewmodels.AppViewModelFactory
import com.example.coffeeapp.presentation.viewmodels.ProductsViewModel
import javax.inject.Inject

class CartScreen : Fragment() {
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val viewModel by navGraphViewModels<ProductsViewModel>(R.id.cafes_graph) {
        viewModelFactory
    }
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = _binding ?: throw RuntimeException("FragmentCartBinding is null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivitySubcomponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCartList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setWaitingTime(valueMinutes: Int) {
        binding.waitTime.text = getString(R.string.cart_screen_waiting_time, valueMinutes.toString())
    }

    private fun setWaitingHint() {
        binding.waitTime.text = getString(R.string.cart_screen_waiting_hint)
    }

    private fun observeCartList() {
        val adapter = createCartAdapter()
        viewModel.selectedProductsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) setWaitingHint() else setWaitingTime(15)
        }
    }

    private fun createCartAdapter(): CartProductsAdapter {
        val menuAdapter = CartProductsAdapter { item, quantity ->
            viewModel.updateSelectedProducts(item, quantity)
        }
        with(binding.productsList) {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            addItemDecoration(VerticalMarginItemDecoration(6f))
            adapter = menuAdapter
        }
        return menuAdapter
    }
}