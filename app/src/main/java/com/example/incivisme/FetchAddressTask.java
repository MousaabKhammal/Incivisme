package com.example.incivisme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private final String TAG = FetchAddressTask.class.getSimpleName();
    private final Context mContext;
    private OnTaskCompleted mListener;


    public FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }



    @Override
    protected String doInBackground(Location... locations) {
        //Geocoder = clase para manejar la codificación geográfica y la codificación geográfica inversa.

        //Configurar el geolocalizador
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault()); //Un objeto Locale representa una región geográfica, política o cultural específica
        //Obtener un objeto de tipo Location
        Location location = locations[0];
        List<Address> addresses = null;
        String resultMessage = "";
        try {
            addresses =  geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            //Detectar problemas de red u otros problemas de E / S
            resultMessage = "Servei no disponible";
            Log.e(TAG, resultMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            //Capturar valores de latitud o longitud no válidos
            resultMessage = "Coordenades no vàlides";
            Log.e(TAG, resultMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        //Si no se encuentran direcciones, muestra un mensaje de error.
        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = "No s'ha trobat cap adreça";
                Log.e(TAG, resultMessage);
            }
        }
        else {
            //Si se encuentra una dirección, leerla en resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<String>();

            //Iterar sobre la lista de objetos de tipo Address i guardarlos en el ArrayList línia por línia.
            //Obtener las líneas de dirección usando getAddressLine
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }
            //COnvierte la lista en una cadena
            resultMessage = TextUtils.join(
                    "\n",
                    addressParts);


        }
        return resultMessage;
    }

    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}
