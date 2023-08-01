package com.example.storyapp.ui

import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityStoryMapBinding
import com.example.storyapp.ui.model.StoriesViewModel
import com.example.storyapp.ui.model.StoriesViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var binding: ActivityStoryMapBinding

    private val storiesViewModel: StoriesViewModel by viewModels {
        StoriesViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storiesViewModel.getMapStories(1)

        storiesViewModel.mapStory.observe(this) {
            addStoriesToMap(it)
        }

        storiesViewModel.error.observe(this) {
            Toast.makeText(this@StoryMapActivity, it, Toast.LENGTH_SHORT).show()
        }

        storiesViewModel.isEmpty.observe(this) {
            if (it) {
                Toast.makeText(
                    this@StoryMapActivity,
                    resources.getString(R.string.empty_stories),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun addStoriesToMap(it: List<Story>) {
        it.forEach { story ->
            val latLng = LatLng(story.lat!!, story.lon!!)
            val address = getAddress(story.lat, story.lon)
            mMap.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(address))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                resources.getDimensionPixelSize(R.dimen.spacing_24)
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MAP PARSING", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MAP PARSING", "Can't find style. Error: ", exception)
        }
    }

    private fun getAddress(lat: Double, long: Double): String? {
        var address: String? = null
        val geocoder = Geocoder(this@StoryMapActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(
                if (lat > 90) 90.0 else lat,
                long,
                1
            )
            address = if (list != null && list.size != 0) {
                list[0].getAddressLine(0)
            } else {
                ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }
}