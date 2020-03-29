package com.example.gh_bloodbank.placelocationfiles;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gh_bloodbank.SearchActivity;
import com.google.android.gms.maps.GoogleMap;

import org.w3c.dom.Text;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    Context context;
    TextView status;

    public GooglePlacesReadTask(Context mcontext, TextView vieww){
        context = mcontext;
        status = vieww;
    }

    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask(context, status);
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }
}