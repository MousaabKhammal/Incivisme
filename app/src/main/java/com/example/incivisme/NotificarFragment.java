package com.example.incivisme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificarFragment extends Fragment implements  FetchAddressTask.OnTaskCompleted {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private final String TAG = this.getClass().getSimpleName();
    private String mParam1;
    private String mParam2;
    private Button button;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient; //Sirve para interactuar con el proveedor de ubicación fusionada.
    private TextView mLocationTextView;
    private ProgressBar mLoading;
    private boolean mTrackingLocation;



    public NotificarFragment() {
        // Required empty public constructor
    }

    public TextView getmLocationTextView() {
        return mLocationTextView;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificarFragment newInstance() {
        NotificarFragment fragment = new NotificarFragment();
        return fragment;
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
            Toast.makeText(getContext(), "Location", Toast.LENGTH_SHORT).show();
            startTrackingLocation();

        });
        return v;
    }

    private void startTrackingLocation() {
        //inicializar el mFusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    location -> {
                        if (location != null) {
                            //ejecutamos la geocodificacion inversa
                            new FetchAddressTask(getContext(), this::onTaskCompleted).execute(location);
                            mLocationTextView.setText(
                                    getString(R.string.address_text,
                                            getString(R.string.loading),
                                            System.currentTimeMillis()));

                        } else {
                            mLocationTextView.setText("Sense localització coneguda");
                        }
                    });
        }
    }

    private void stopTrackingLocation(){

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
        mLocationTextView.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
    }
}