package com.example.incivisme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificarFragment extends Fragment implements  FetchAddressTask.OnTaskCompleted {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Button button;
    private FusedLocationProviderClient mFusedLocationClient; //Sirve para interactuar con el proveedor de ubicación fusionada.
    private TextView mLocationTextView;
    private ProgressBar mLoading;
    private boolean mTrackingLocation = false;
    private LocationCallback mLocationCallback;



    public NotificarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificarFragment newInstance() {
        return new NotificarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Button Get Location que retorna el metodo getLocation()
        View v = inflater.inflate(R.layout.fragment_notificar, container, false);

        mLocationTextView = (v.findViewById(R.id.localitzacio));
        mLoading = v.findViewById(R.id.loading);

        button =  v.findViewById(R.id.button_location);
        button.setOnClickListener(v1 -> {
            if (!mTrackingLocation) {
                startTrackingLocation();
            } else {
                stopTrackingLocation();
            }
        });
        //crear objeto LocationCallback
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mTrackingLocation) {
                    new FetchAddressTask(getContext(), NotificarFragment.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };
        return v;
    }

    @SuppressLint("SetTextI18n")
    private void startTrackingLocation() {
        //inicializar el mFusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);

        }
        mLoading.setVisibility(ProgressBar.VISIBLE);
        mTrackingLocation = true;
        button.setText("Aturar el seguiment de la ubicació");
    }

    /**
     * Método que deja de rastrear el dispositivo. Elimina la ubicación
     * actualiza, detiene la animación y restablece la interfaz de usuario.
     */
    @SuppressLint("SetTextI18n")
    private void stopTrackingLocation(){
        if (mTrackingLocation) {
            mTrackingLocation = false;
            button.setText("Comença a seguir la ubicació");
            mLocationTextView.setText(R.string.textview_hint);
            mLoading.setVisibility(ProgressBar.INVISIBLE);
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

    }

    //Crear LocationRequest
    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);//establece el intervalo en el que desea obtener ubicaciones
        locationRequest.setFastestInterval(5000); //si una ubicación está disponible antes, puede obtenerla (es decir, otra aplicación está utilizando los servicios de ubicación)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void permisoLocationDenegado(){
        Toast.makeText(getContext(),
                "Permís denegat",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // Si es concedeix permís, obté la ubicació,
                // d'una altra manera, mostra un Toast

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    permisoLocationDenegado();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        //actualizar el textview "address_text" con la direccion y la ora resultantes
        mLocationTextView.setText(getString(R.string.address_text,
                result, System.currentTimeMillis()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTrackingLocation) {
            startTrackingLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTrackingLocation) {
            stopTrackingLocation();
        }
    }
}