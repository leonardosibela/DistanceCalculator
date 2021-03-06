package com.siblea.distancecalculator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_LOCATION_PERMISION_REQUEST_CODE = 123;
    private static final String LATITUDE_PATTERN = "^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$";
    private static final String LONGITUDE_PATTERN = "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$";

    @BindView(R.id.distance)
    TextView distance;

    @BindView(R.id.duration)
    TextView duration;

    @BindView(R.id.user_long)
    TextView userLongitude;

    @BindView(R.id.user_lat)
    TextView userLatitude;

    @BindView(R.id.destination_lat)
    EditText destinationLat;

    @BindView(R.id.destination_long)
    EditText destinationLong;

    @BindView(R.id.calculations_containter)
    LinearLayout calculationsContainer;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @OnClick(R.id.calculate)
    public void calculate() {
        if (validateFields()) {
            calculationsContainer.setVisibility(View.GONE);

            GoogleDistanceMatrixAPI.GoogleDistanceMatrixService gdmAPI = GoogleDistanceMatrixAPI.getInstance();
            Call<DistanceMatrixReturn> paths = gdmAPI.get(
                    getCurrentLocation(),
                    getDestination(),
                    BuildConfig.API_KEY);

            paths.enqueue(new Callback<DistanceMatrixReturn>() {

                @Override
                public void onResponse(Call<DistanceMatrixReturn> call, Response<DistanceMatrixReturn> response) {
                    try {
                        DistanceMatrixReturn distanceMatrix = response.body();
                        duration.setText(distanceMatrix.getDurationString());
                        distance.setText(distanceMatrix.getDistanceString());
                        calculationsContainer.setVisibility(View.VISIBLE);
                    } catch (NullPointerException npe) {
                        Snackbar.make(findViewById(R.id.activity_main), R.string.unable_find_path, Snackbar.LENGTH_INDEFINITE).show();
                    }
                }

                @Override
                public void onFailure(Call<DistanceMatrixReturn> call, Throwable t) {
                    Toast.makeText(getBaseContext(), R.string.an_error_occurred, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private GoogleDistanceMatrixAPI.Place getDestination() {
        double longitude = Double.parseDouble(destinationLong.getText().toString());
        double latitude = Double.parseDouble(destinationLat.getText().toString());

        return new GoogleDistanceMatrixAPI.Place(longitude, latitude);
    }

    private boolean validateFields() {
        return validateLatitude() && validateLongitude();
    }

    @OnEditorAction(R.id.destination_lat)
    public boolean destinationLatitudeEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (validateLatitude())
                destinationLong.requestFocus();
            return true;
        }
        return false;
    }

    private boolean validateLatitude() {
        if (!destinationLat.getText().toString().matches(LATITUDE_PATTERN)) {
            destinationLat.setError(getString(R.string.invalid_latitude));
            return false;
        }
        return true;
    }

    @OnEditorAction(R.id.destination_long)
    public boolean destinationLongitudeEditorAction(TextView view, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (validateLongitude()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        }
        return false;
    }

    private boolean validateLongitude() {
        if (!destinationLong.getText().toString().matches(LONGITUDE_PATTERN)) {
            destinationLong.setError(getString(R.string.invalid_longitude));
            return false;
        }

        return true;
    }

    private GoogleDistanceMatrixAPI.Place getCurrentLocation() {
        return new GoogleDistanceMatrixAPI.Place(mLastLocation.getLongitude(), mLastLocation.getLatitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForLocationPermission();
        } else {
            startLocationUpdates();
            setLongAndLat();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void askForLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISION_REQUEST_CODE);
    }

    private void setLongAndLat() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                userLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
                userLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }
    }

    private void displayLocationPermissionSnack() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), R.string.location_permission_message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.location_permission_action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForLocationPermission();
            }
        });
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLongAndLat();
                    startLocationUpdates();
                } else {
                    displayLocationPermissionSnack();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        setLongAndLat();
    }
}