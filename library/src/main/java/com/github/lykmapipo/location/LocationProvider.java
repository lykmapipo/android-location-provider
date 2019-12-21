package com.github.lykmapipo.location;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.List;
import java.util.Locale;

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
     * @since 0.4.0
     */
    @VisibleForTesting
    public static synchronized Task<LocationSettingsResponse> checkLocationSettings(
            @NonNull Context context
    ) {
        // create client and request
        LocationSettingsRequest request = createLocationSettingsRequest();
        SettingsClient client = createSettingsClient(context);

        // check location settings
        Task<LocationSettingsResponse> task = client.checkLocationSettings(request);

        // return
        return task;
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
            @NonNull OnLocationSettingsChangeListener listener
    ) {
        // check location settings
        Task<LocationSettingsResponse> task = checkLocationSettings(context);

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                // handle success
                if (task.isSuccessful()) {
                    listener.onSuccess(task.getResult());
                }
                // handle failure
                else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }

    /**
     * Request last known location
     *
     * @param context {@link Context}
     * @return {@link Task} which resolve with {@link Location} or {@link Exception}
     * @since 0.4.0
     */
    @VisibleForTesting
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            }
    )
    public static synchronized Task<Location> requestLocation(@NonNull Context context) {
        // obtain fused location client
        FusedLocationProviderClient fusedLocationClient = createLocationClient(context);

        // request last known location
        Task<Location> lastLocation = fusedLocationClient.getLastLocation();

        // return
        return lastLocation;
    }

    @VisibleForTesting
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            }
    )
    public static synchronized void requestLocation(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener
    ) {
        // request last known location
        Task<Location> lastLocation = requestLocation(context);
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
     * @param fragment
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            }
    )
    @MainThread
    public static synchronized void requestLastLocation(
            @NonNull Fragment fragment,
            @NonNull OnLastLocationListener listener
    ) {
        requestLastLocation(fragment.requireActivity(), listener);
    }

    /**
     * Get the last known location
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            }
    )
    @MainThread
    public static synchronized void requestLastLocation(
            @NonNull Context context,
            @NonNull OnLastLocationListener listener
    ) {
        // check location settings
        checkLocationSettings(context, new OnLocationSettingsChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse response) {
                requestLocation(context, listener);
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFailure(Exception error) {
                // try resolve error
                if (error instanceof ResolvableApiException) {
                    // do resolve
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) error;
                        PendingIntent resolution = resolvable.getResolution();
                        Request request = RequestFabric.create(
                                resolution.getIntentSender(), null,
                                0, 0, 0, null
                        );

                        new InlineActivityResult((FragmentActivity) context)
                                .startForResult(request)
                                .onSuccess(result -> requestLastLocation(context, listener))
                                .onFail(result -> onFailure(error));
                    }
                    // notify resolve error
                    catch (Exception resolveError) {
                        listener.onFailure(resolveError);
                    }
                }
                // notify error
                else {
                    listener.onFailure(error);
                }
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
     * @param fragment
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            }
    )
    @MainThread
    public static synchronized void requestLocationUpdates(
            @NonNull Fragment fragment,
            @NonNull OnLocationUpdatesListener listener
    ) {
        requestLocationUpdates(fragment.requireActivity(), listener);
    }

    /**
     * Request location updates
     *
     * @param context
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            }
    )
    @MainThread
    public static synchronized void requestLocationUpdates(
            @NonNull Context context,
            @NonNull OnLocationUpdatesListener listener
    ) {
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

            @SuppressLint("MissingPermission")
            @Override
            public void onFailure(Exception error) {
                // try resolve error
                if (error instanceof ResolvableApiException) {
                    // do resolve
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) error;
                        PendingIntent resolution = resolvable.getResolution();
                        Request request = RequestFabric.create(
                                resolution.getIntentSender(), null, 0,
                                0, 0, null
                        );

                        new InlineActivityResult((FragmentActivity) context)
                                .startForResult(request)
                                .onSuccess(result -> requestLocationUpdates(context, listener))
                                .onFail(result -> onFailure(error));
                    }
                    // notify resolve error
                    catch (Exception resolveError) {
                        listener.onFailure(resolveError);
                    }
                }
                // notify error
                else {
                    listener.onFailure(error);
                }
            }
        });
    }

    /**
     * Request location address
     *
     * @param context
     * @param location
     * @param listener
     * @since 0.1.0
     */
    @RequiresPermission("android.permission.INTERNET")
    public static synchronized void requestAddress(
            @NonNull Context context,
            @NonNull Location location,
            @NonNull OnAddressListener listener
    ) {
        // invoke task
        Task<Address> task = getAddressFromLocation(context, location);
        task.addOnCompleteListener(new OnCompleteListener<Address>() {
            @Override
            public void onComplete(@NonNull Task<Address> task) {
                // handle success
                if (task.isSuccessful()) {
                    listener.onSuccess(task.getResult());
                }
                // handle failure
                else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }

    /**
     * Derive address from a given location
     *
     * @param context
     * @param location
     * @return
     */
    public static synchronized Task<Address> getAddressFromLocation(
            @NonNull Context context, @NonNull Location location
    ) {
        final TaskCompletionSource<Address> source = new TaskCompletionSource<Address>();
        Thread fetch = new Thread(() -> {
            // request address
            try {

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                if (!Geocoder.isPresent()) {
                    throw new Exception("Geocoder Not Present");
                }
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1
                );
                if (addresses == null || addresses.isEmpty()) {
                    throw new Exception("Address Not Found");
                }
                Address address = addresses.get(0);
                source.setResult(address);
            }
            // notify error
            catch (Exception error) {
                source.setException(error);
            }
        });
        fetch.start();

        // return task
        return source.getTask();
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

    public interface OnAddressListener {
        void onSuccess(Address address);

        void onFailure(Exception error);
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
