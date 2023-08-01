package com.example.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityPostBinding
import com.example.storyapp.ui.model.StoriesViewModel
import com.example.storyapp.ui.model.StoriesViewModelFactory
import com.example.storyapp.utils.rotateFile
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.File


class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private var getFile: File? = null

    private val storiesViewModel: StoriesViewModel by viewModels {
        StoriesViewModelFactory(this)
    }

    private var isLocation: Boolean = false
    private var postLocation: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.post_story)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        storiesViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        storiesViewModel.error.observe(this) {
            showToast(it.toString())
        }

        storiesViewModel.upload.observe(this) {
            showToast(getString(R.string.upload_success))
            finish()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
        binding.btnLocation.setOnClickListener {
            toggleLocation()
            setupLocation()
        }
    }

    private fun toggleLocation() {
        isLocation = !isLocation
        if (isLocation) {
            binding.btnLocation.setCardBackgroundColor(resources.getColor(R.color.primary_400))
            binding.ivLocationIcon.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker_white))
        } else {
            binding.btnLocation.setCardBackgroundColor(resources.getColor(R.color.white))
            binding.ivLocationIcon.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker))
        }
    }

    private fun setupLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (isLocation) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        postLocation =
                            LatLng(location.latitude, location.longitude)
                    }
                }.addOnFailureListener { e: Exception ->
                    Log.e("LOCATION", e.toString())
                }

            } else {
                postLocation = null
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
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

    private fun uploadStory() {
        val desc = binding.edAddDescription.text
        if (desc.isEmpty()) {
            binding.edAddDescription.error = getString(R.string.add_description)
            showToast(getString(R.string.add_description))
            return
        }

        if (getFile == null) {
            showToast(getString(R.string.choose_picture))
            return
        }

        Log.wtf("LOCATION","${postLocation?.latitude?.toString()} || ${postLocation?.longitude?.toString()}")
        storiesViewModel.uploadStory(
            getFile as File,
            binding.edAddDescription.text.toString(),
            postLocation?.latitude?.toString(),
            postLocation?.longitude?.toString()
        )
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.pick_image))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this@PostActivity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun setImageHeight() {
        val layoutParams = binding.imagePreview.layoutParams
        layoutParams.height = resources.getDimensionPixelSize(R.dimen.default_image_height)
        binding.imagePreview.layoutParams = layoutParams

    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                setImageHeight()
                getFile = file
                rotateFile(file, isBackCamera)
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@PostActivity)
                getFile = myFile
                setImageHeight()
                binding.imagePreview.setImageURI(uri)
            }
        }
    }


    private fun showLoading(loading: Boolean) {
        if (loading) binding.loadingIndicator.root.visibility =
            View.VISIBLE else binding.loadingIndicator.root.visibility = View.INVISIBLE
    }


    private fun showToast(message: String) {
        Toast.makeText(this@PostActivity, message, Toast.LENGTH_SHORT).show()
    }
}