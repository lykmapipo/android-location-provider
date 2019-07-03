package com.github.lykmapipo.location;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * A pack of helpful helpers to obtain location(s) from fused {@link com.google.android.gms.location.FusedLocationProviderClient}.
 *
 * @author lally elias <lallyelias87@gmail.com>
 * @version 0.1.0
 * @since 0.1.0
 */
public class LocationProvider {
    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_LOCATION_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private static FusedLocationProviderClient fusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private static SettingsClient settingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private static LocationRequest locationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private static LocationSettingsRequest locationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private static LocationCallback locationCallback;

    /**
     * Create a new instance of {@link FusedLocationProviderClient} for use in a non-activity {@link Context}
     *
     * @param context
     * @return
     * @since 0.1.0
     */
    public static synchronized FusedLocationProviderClient createLocationClient(@NonNull Context context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return fusedLocationClient;
    }

    /**
     * Create a new instance of {@link SettingsClient} for use in a non-activity {@link Context}
     *
     * @param context
     * @return
     * @since 0.1.0
     */
    public static synchronized SettingsClient createSettingsClient(@NonNull Context context) {
        if (settingsClient == null) {
            settingsClient = LocationServices.getSettingsClient(context);
        }
        return settingsClient;
    }

    /**
     * Create a new instance of {@link LocationRequest}
     * <p>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     *
     * @return
     * @since 0.1.0
     */
    public static synchronized LocationRequest createLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        return locationRequest;
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     *
     * @since 0.1.0
     */
    public static synchronized LocationSettingsRequest createLocationSettingsRequest() {
        if (locationSettingsRequest == null) {
            LocationRequest locationRequest = createLocationRequest();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            locationSettingsRequest = builder.build();
        }
        return locationSettingsRequest;
    }

    /**
     * Check if the device has the necessary location settings.
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    public static synchronized void checkLocationSettings(
            @NonNull Context context,
            @NonNull OnLocationSettingsChangeListener listener) {
        // create client and request
        LocationSettingsRequest request = createLocationSettingsRequest();
        SettingsClient client = createSettingsClient(context);

        // check location settings
        Task<LocationSettingsResponse> task = client.checkLocationSettings(request);

        // handles success response
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse response) {
                listener.onSuccess(response);
            }
        });

        // handle failure & request setting updates
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception failure) {
                // TODO try resolve failure
                if (failure instanceof ResolvableApiException) {
                    listener.onFailure(failure);
                }

                // notify failure
                else {
                    listener.onFailure(failure);
                }
            }
        });

    }

    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    private static synchronized void requestLocation(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener) {
        // obtain fused location client
        FusedLocationProviderClient fusedLocationClient = createLocationClient(context);

        // request last known location
        Task<Location> lastLocation = fusedLocationClient.getLastLocation();
        lastLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // grab last known location
                Location lastLocation = task.getResult();
                // reply ok if found
                if (task.isSuccessful() && lastLocation != null) {
                    listener.onSuccess(lastLocation);
                }
                // notify error otherwise
                else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }

    /**
     * Get the last known location
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public static synchronized void requestLastLocation(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener) {
        // check location settings
        checkLocationSettings(context, new OnLocationSettingsChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse response) {
                requestLocation(context, listener);
            }

            @Override
            public void onFailure(Exception error) {
                listener.onFailure(error);
            }
        });
    }

    /**
     * Creates a callback for receiving location events.
     *
     * @param listener
     * @return
     * @since 0.1.0
     */
    public static synchronized LocationCallback createLocationCallback(
            @NonNull OnLocationUpdatesListener listener
    ) {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    super.onLocationResult(result);
                    listener.onSuccess(result);
                }
            };
        }
        return locationCallback;
    }

    /**
     * Request location updates
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public static synchronized void requestLocationUpdates(
            @NonNull Context context,
            @NonNull OnLocationUpdatesListener listener) {
        // check location settings
        checkLocationSettings(context, new OnLocationSettingsChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse response) {
                // obtain location updates callback
                LocationCallback callback = createLocationCallback(listener);

                // obtain fused location client
                FusedLocationProviderClient fusedLocationClient = createLocationClient(context);

                // obtain location request
                LocationRequest request = createLocationRequest();

                // TODO guard register multiple callback

                // start request location updates
                fusedLocationClient.requestLocationUpdates(request, callback, Looper.myLooper());
            }

            @Override
            public void onFailure(Exception error) {
                listener.onFailure(error);
            }
        });
    }

    /**
     * Stop location updates
     *
     * @since 0.1.0
     */
    public static void stopLocationUpdates() {
        boolean shouldStop = fusedLocationClient != null && locationCallback != null;
        if (shouldStop) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            // TODO execute returned tasks and update states
        }
    }

    /**
     * Clear and reset internal states
     *
     * @since 0.1.0
     */
    public static synchronized void clear() {
        locationCallback = null;
        locationSettingsRequest = null;
        locationRequest = null;
        settingsClient = null;
        fusedLocationClient = null;
    }

    public interface OnLastLocationListener {
        void onSuccess(Location location);

        void onFailure(Exception error);
    }

    public interface OnLocationUpdatesListener {
        void onSuccess(LocationResult result);

        void onFailure(Exception error);
    }

    public interface OnLocationSettingsChangeListener {
        void onSuccess(LocationSettingsResponse response);

        void onFailure(Exception error);
    }

}
