package com.arajangstudio.jajanhub_partner.ui.maps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.arajangstudio.jajanhub_partner.BuildConfig
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.ApiServiceExternal
import com.arajangstudio.jajanhub_partner.data.remote.models.Item
import com.arajangstudio.jajanhub_partner.databinding.ActivityLocationPickerBinding
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class LocationPickerActivity : AppCompatActivity() {

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var hasFetch = false
    private var animateMarker = true
    private val retrofitInstance = ApiServiceExternal.create()
    lateinit var latLng: LatLng
    lateinit var activityLocationPickerBinding: ActivityLocationPickerBinding
    lateinit var  mapFragment: SupportMapFragment
    lateinit var currentLocation: LatLng
    private var locationName = ""
    private var locationAddress = ""
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQ_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityLocationPickerBinding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(activityLocationPickerBinding.root)


        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        latLng = LatLng(0.5490078,123.0050009)
        currentLocation = LatLng(0.5490078,123.0050009)

         mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        activityLocationPickerBinding.progressCircular.visibility = View.GONE
         bottomSheet = BottomSheetBehavior.from(activityLocationPickerBinding.bottomSheet.bottomSheet)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        activityLocationPickerBinding.btnCurrentLocation.setOnClickListener {
           getCurrentLocation()
        }

        activityLocationPickerBinding.bottomSheet.btnChooseLocation.setOnClickListener {
            val intent = Intent(this, MerchantActivity::class.java)
            intent.putExtra("locationName", locationName)
            intent.putExtra("locationAddress", locationAddress)
            intent.putExtra("locationLatitude", currentLocation.latitude)
            intent.putExtra("locationLongitude", currentLocation.longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        activityLocationPickerBinding.bottomSheet.btnChangeLocation.setOnClickListener {
            val radius = 600.0
            val distance = radius * sqrt(2.0)
            val center = latLng
            val ne = SphericalUtil.computeOffset(center, distance, 45.0)
            val sw = SphericalUtil.computeOffset(center, distance, 225.0)
            val bounds = RectangularBounds.newInstance(sw, ne)
            val fields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountries(arrayListOf("ID"))
                .setLocationBias(bounds)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        mapFragment.getMapAsync { map ->
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

            val oldPosition = map.cameraPosition.target

            map.setOnCameraMoveStartedListener {
                // drag started
                if (animateMarker) {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    activityLocationPickerBinding.iconMarker.animate().translationY(-50f).start()
                    activityLocationPickerBinding.iconMarkerShadow.animate().withStartAction {
                        activityLocationPickerBinding.iconMarkerShadow.setPadding(10, 0,0,0)
                    }.start()
                    activityLocationPickerBinding.infoMarker.animate().alpha(0.0f)

                }

                hasFetch = false
            }

            map.setOnCameraIdleListener {
                val newPosition = map.cameraPosition.target
                if (newPosition != oldPosition) {
                    // drag ended
                    activityLocationPickerBinding.infoMarker.animate().alpha(1.0f)
                    activityLocationPickerBinding.iconMarker.animate().translationY(0f).start()
                    activityLocationPickerBinding.iconMarkerShadow.animate().withStartAction {
                        activityLocationPickerBinding.iconMarkerShadow.setPadding(0,0,0,0)
                    }.start()

                    getLocation(newPosition) { item ->
                        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                        val position = item.position
                        val findLocation = LatLng(position.lat, position.lng)

                        map.animateCamera(CameraUpdateFactory.newLatLng(findLocation), 200,
                            object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    hasFetch = true
                                    animateMarker = true
                                }

                                override fun onCancel() {
                                    animateMarker = true
                                }

                            })

                        val titlePlace = item.title
                        val address = item.address.label
                        locationName = item.title
                        locationAddress = item.address.label
                        currentLocation = LatLng(position.lat, position.lng)

                        activityLocationPickerBinding.bottomSheet.textTitle.text = titlePlace
                        activityLocationPickerBinding.bottomSheet.textAddress.text = address
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getCurrentLocation()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
    }

    override fun onResume() {
        super.onResume()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
    }

    override fun onPause() {
        super.onPause()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
    }

    override fun onRestart() {
        super.onRestart()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        mapFragment.getMapAsync { map ->
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                            val place = Autocomplete.getPlaceFromIntent(data)

                                val position = place.latLng
                                val findLocation = LatLng(position.latitude, position.longitude)


                                map.animateCamera(CameraUpdateFactory.newLatLng(findLocation), 200,
                                    object : GoogleMap.CancelableCallback {
                                        override fun onFinish() {
                                            hasFetch = true
                                            animateMarker = true
                                            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                                        }

                                        override fun onCancel() {
                                            animateMarker = true
                                        }

                                    })
                                locationName = place.name
                                locationAddress = place.address
                                currentLocation =LatLng(position.latitude, position.longitude)
                                val titlePlace = place.name
                                val address = place.address


                                activityLocationPickerBinding.bottomSheet.textTitle.text =
                                    titlePlace
                                activityLocationPickerBinding.bottomSheet.textAddress.text = address
                            }

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(this, status.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getLocation(latLng: LatLng, done: (Item) -> Unit) {
        val at = "${latLng.latitude},${latLng.longitude}"
        if (!hasFetch) {
            animateMarker = false
            activityLocationPickerBinding.progressCircular.visibility = View.VISIBLE
            GlobalScope.launch {
                try {
                    val places = retrofitInstance.getLocation(at).items
                    runOnUiThread {
                        if (places.isNotEmpty()) {
                            activityLocationPickerBinding.progressCircular.visibility = View.GONE
                            done.invoke(places.first())
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
            return
        }


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                mapFragment.getMapAsync { map ->
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.latitude), 18f))
                    getLocation(LatLng(location.latitude, location.longitude)) { item ->
                        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                        val position = item.position
                        val findLocation = LatLng(position.lat, position.lng)



                        map.animateCamera(CameraUpdateFactory.newLatLng(findLocation), 200,
                            object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    hasFetch = true
                                    animateMarker = true
                                }

                                override fun onCancel() {
                                    animateMarker = true
                                }

                            })

                        locationName = item.title
                        locationAddress = item.address.label
                        currentLocation = LatLng(position.lat, position.lng)
                        val titlePlace = item.title
                        val address = item.address.label

                        activityLocationPickerBinding.bottomSheet.textTitle.text = titlePlace
                        activityLocationPickerBinding.bottomSheet.textAddress.text = address
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
      }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
