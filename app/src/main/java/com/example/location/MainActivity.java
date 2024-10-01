package com.example.location;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager lm;
    TextView status;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "LocationUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView
        status = findViewById(R.id.status);

        // Set an initial message in the TextView
        status.setText("Waiting for location...");

        // Initialize the LocationManager
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS is enabled
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
        }

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Start requesting location updates
            requestLocationUpdates();
        }
    }

    // Request location updates from both GPS and Network providers
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Request location updates from GPS
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);

            // Request location updates from Network provider (backup)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);

            Toast.makeText(this, "Location updates started", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates();
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Called when location changes
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            // Get latitude and longitude
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());

            // Update the status TextView
            status.setText("Lat: " + lat + " | Lon: " + lon);

            // Log the location for debugging purposes
            Log.d(TAG, "Lat: " + lat + ", Lon: " + lon);
        } else {
            status.setText("Unable to determine location.");
        }
    }

    // Called when provider is disabled
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getApplicationContext(), provider + " Disabled", Toast.LENGTH_LONG).show();
    }

    // Called when provider is enabled
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(getApplicationContext(), provider + " Enabled", Toast.LENGTH_LONG).show();
    }

    // Deprecated in API 29, but still required for older devices
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
