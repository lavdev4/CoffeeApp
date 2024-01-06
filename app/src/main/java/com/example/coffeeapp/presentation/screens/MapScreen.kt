package com.example.coffeeapp.presentation.screens

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.example.coffeeapp.R
import com.example.coffeeapp.databinding.FragmentMapBinding
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.viewmodels.CafesViewModel
import com.example.coffeeapp.presentation.viewmodels.factories.CafesGraphVMFactory
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.runtime.image.ImageProvider
import javax.inject.Inject

class MapScreen : Fragment() {
    @Inject lateinit var viewModelFactory: CafesGraphVMFactory
    private val viewModel by navGraphViewModels<CafesViewModel>(R.id.cafes_graph) { viewModelFactory }
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = _binding ?: throw RuntimeException("FragmentMapBinding is null")

    private var cafeIconBig: ImageProvider? = null
    private var cafeIconSmall: ImageProvider? = null
    private var cafeIconColor: Int? = null
    private var currentCafesPlaceMarks: MapObjectCollection? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivitySubcomponent.inject(this)
        MapKitFactory.initialize(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cafeIconBig = createCafeIcon(false)
        cafeIconSmall = createCafeIcon(true)
        cafeIconColor = requireContext().getColor(R.color.brown)
        observeCafes()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.map.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        binding.map.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeCafes() {
        viewModel.cafesList.observe(viewLifecycleOwner) { refreshCafePointsOnMap(it) }
    }

    private fun refreshCafePointsOnMap(cafes: List<CafeEntity>) {
        val mapObjects = binding.map.mapWindow.map.mapObjects
        currentCafesPlaceMarks?.let { mapObjects.remove(currentCafesPlaceMarks as MapObject) }
        currentCafesPlaceMarks = mapObjects.addCollection()
        cafes.forEach { cafe ->
            currentCafesPlaceMarks!!.addPlacemark().apply {
                geometry = Point(cafe.latitude.toDouble(), cafe.longitude.toDouble())
                cafe.distanceMeters?.let { distanceTillUser ->
                    chooseCafeIcon(distanceTillUser, this, cafe.name)
                } ?: run {
                    setBigCafeIcon(this, cafe.name)
                }
            }
        }
        if (!viewModel.mapWasInitialised) {
            moveCameraToClosestCafe(cafes, true) { completed ->
                if (completed) viewModel.mapWasInitialised = true
            }
        } else {
            moveCameraToClosestCafe(cafes, false, null)
        }
    }

    private fun createCafeIcon(small: Boolean): ImageProvider? {
        val drawableResId = if (small) {
            R.drawable.icon_map_cafe_small
        } else {
            R.drawable.icon_map_cafe_big
        }
        val cafeIconBitmap = (ResourcesCompat.getDrawable(
            resources,
            drawableResId,
            null
        ) as VectorDrawable).toBitmap()
        return ImageProvider.fromBitmap(cafeIconBitmap)
    }

    private fun chooseCafeIcon(distanceTillUser: Float, placeMark: PlacemarkMapObject, text: String) {
        return if (distanceTillUser >= 1000.0f) {
            setBigCafeIcon(placeMark, text)
        } else {
            setSmallCafeIcon(placeMark)
        }
    }

    private fun setBigCafeIcon(placeMark: PlacemarkMapObject, text: String) {
        placeMark.apply {
            cafeIconBig?.let { setIcon(it) }
            setText(text)
            setTextStyle(createCafeIconTextStyle())
        }
    }

    private fun setSmallCafeIcon(placeMark: PlacemarkMapObject) {
        cafeIconSmall?.let { placeMark.setIcon(it) }
    }

    private fun createCafeIconTextStyle(): TextStyle {
        return TextStyle().apply {
            color = cafeIconColor
            size = 14.0f
            placement = TextStyle.Placement.BOTTOM
        }
    }

    private fun moveCameraToClosestCafe(
        cafes: List<CafeEntity>,
        animated: Boolean,
        onAnimated: ((Boolean) -> Unit)?
    ) {
        val closestCafe = cafes.sortedBy { it.distanceMeters }[0]
        val map = binding.map.mapWindow.map
        val cameraPosition = CameraPosition(
            Point(closestCafe.latitude.toDouble(), closestCafe.longitude.toDouble()),
            15.0f,
            0.0f,
            0.0f
        )
        if (animated) {
            map.move(
                cameraPosition,
                Animation(Animation.Type.SMOOTH, 2.0f),
                onAnimated
            )
        } else {
            map.move(cameraPosition)
        }
    }
}