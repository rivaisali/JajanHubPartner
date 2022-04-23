package com.arajangstudio.jajanhub_partner.utils

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arajangstudio.jajanhub_partner.data.remote.models.CurrentLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val context: Context
) {

    val uuid: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[UUID]
        }

    val favoriteMenu: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[FAVORITE_MENU]
        }

    val locationName: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[LOCATION_NAME]
        }

    val locationAddress: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[LOCATION_ADDRESS]
        }

    val locationLatLng: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[LOCATION_LATLNG]
        }

    fun currentLocation() = context.dataStore.data.map {
        CurrentLocation(
            location_name = it[LOCATION_NAME]!!,
            location_address = it[LOCATION_ADDRESS]!!,
            location_latitude = it[LOCATION_LATITUDE]!!,
            location_longitude = it[LOCATION_LONGITUDE]!!
        )
    }


    suspend fun save(uuid: String) {
        context.dataStore.edit { preferences ->
            preferences[UUID] = uuid
        }
    }

//    suspend fun saveCurrentLocation(currentLocation: CurrentLocation){
//        context.dataStore.edit {
//            it[LOCATION_NAME] = currentLocation.location_name
//            it[LOCATION_ADDRESS] = currentLocation.location_address
//            it[LOCATION_LATITUDE] = currentLocation.location_latitude
//            it[LOCATION_LONGITUDE] = currentLocation.location_longitude
//        }
//    }

    suspend fun saveFavoriteMenu(menu_id: String) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_MENU] = menu_id
        }
    }

    suspend fun saveCurrentLocation(
        location_name: String, location_address: String, location_latitude: Double,
        location_longitude: Double, location_latLng: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION_NAME] = location_name
            preferences[LOCATION_ADDRESS] = location_address
            preferences[LOCATION_LATITUDE] = location_latitude
            preferences[LOCATION_LONGITUDE] = location_longitude
            preferences[LOCATION_LATLNG] = location_latLng
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(UUID)
        }
    }

    suspend fun clearFavoriteMenu() {
        context.dataStore.edit { preferences ->
            preferences.remove(FAVORITE_MENU)
        }
    }

    suspend fun clearCurrentLocation() {
        context.dataStore.edit { preferences ->
            preferences.remove(LOCATION_NAME)
            preferences.remove(LOCATION_ADDRESS)
            preferences.remove(LOCATION_LATITUDE)
            preferences.remove(LOCATION_LONGITUDE)
            preferences.remove(LOCATION_LATLNG)
        }
    }


    companion object {
        private val UUID = stringPreferencesKey("uuid")
        private val FAVORITE_MENU = stringPreferencesKey("favorite_menu")
        private val LOCATION_NAME = stringPreferencesKey("location_name")
        private val LOCATION_ADDRESS = stringPreferencesKey("location_address")
        private val LOCATION_LATITUDE = doublePreferencesKey("location_latitude")
        private val LOCATION_LONGITUDE = doublePreferencesKey("location_longitude")
        private val LOCATION_LATLNG = stringPreferencesKey("location_latlng")

        private const val DATASTORE_NAME = "jajanhub_preferences"
        private val Context.dataStore by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }
}