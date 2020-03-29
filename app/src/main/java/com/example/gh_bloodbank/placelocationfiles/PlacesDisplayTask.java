package com.example.gh_bloodbank.placelocationfiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gh_bloodbank.Accessories;
import com.example.gh_bloodbank.R;
import com.example.gh_bloodbank.SearchActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    Context context;
    Accessories placesAccessor;
    TextView status;

    public PlacesDisplayTask(Context mcontext, TextView the_status){
        this.context = mcontext;
        status = the_status;
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
//            Toast.makeText(context, "place" + placeName, Toast.LENGTH_LONG).show();
            String vicinity = googlePlace.get("vicinity");
            placesAccessor = new Accessories(context);
            String bloodType = placesAccessor.getString("blood_type");
            //check for bloodtype
            if(bloodType.equals("a")){
                try{
                    DatabaseReference gethospitals = FirebaseDatabase.getInstance().getReference("blood_type").child("A");
                    gethospitals.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    if(child.getKey().contains(placeName)){
                                        LatLng latLng = new LatLng(lat, lng);
                                        markerOptions.position(latLng);
                                        markerOptions.title(placeName + " : " + vicinity);
//                                        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.hospital_marker))));
                                    }else{
        //                                Toast.makeText(context, "No nearby hospitals with blood type", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();
                        }
                    });
                    status.setText("Done");

                }catch (NullPointerException e){

                }

            }
            else if(bloodType.equals("b")){
                try{
                    DatabaseReference gethospitals = FirebaseDatabase.getInstance()
                            .getReference("blood_type").child("B");
                    gethospitals.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    if(child.getKey().contains(placeName)){
                                        LatLng latLng = new LatLng(lat, lng);
                                        markerOptions.position(latLng);
                                        markerOptions.title(placeName + " : " + vicinity);
//                                        googleMap.addMarker(markerOptions
//                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.hospital_marker))));
                                    }else{
                                        //Toast.makeText(context, "No nearby hospitals with blood type", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();
                        }
                    });
                    status.setText("Done");

                }catch (NullPointerException e){

                }

            }
            else if(bloodType.equals("ab")){
                try{
                    DatabaseReference gethospitals = FirebaseDatabase.getInstance().getReference("blood_type").child("AB");
                    gethospitals.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    if(child.getKey().contains(placeName)){
                                        LatLng latLng = new LatLng(lat, lng);
                                        markerOptions.position(latLng);
                                        markerOptions.title(placeName + " : " + vicinity);
//                                        googleMap.addMarker(markerOptions
//                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.hospital_marker))));

                                    }else{
                                        //Toast.makeText(context, "No nearby hospitals with blood type", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();
                        }
                    });
                    status.setText("Done");

                }catch (NullPointerException e){

                }

            }
            else if(bloodType.equals("o")){
                try{
                    DatabaseReference gethospitals = FirebaseDatabase.getInstance()
                            .getReference("blood_type").child("O");
                    gethospitals.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    if(child.getKey().contains(placeName)){
                                        LatLng latLng = new LatLng(lat, lng);
                                        markerOptions.position(latLng);
                                        markerOptions.title(placeName + " : " + vicinity);
//                                        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.hospital_marker))));

                                    }else{
                                        //Toast.makeText(context, "No nearby hospitals with blood type", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();
                        }
                    });
                    status.setText("Done");

                }catch (NullPointerException e){

                }

            }

//            LatLng latLng = new LatLng(lat, lng);
//            markerOptions.position(latLng);
//            markerOptions.title(placeName + " : " + vicinity);
////            Toast.makeText(SearchActivity.this, placeName.toString(), Toast.LENGTH_LONG).show();
////            googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_marker)));
//            googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

//            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                            .target(marker.getPosition())      // Sets the center of the map to Mountain View
//                            .zoom(18)                   // Sets the zoom
//                            .bearing(90)                // Sets the orientation of the camera to east
//                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                            .build();                   // Creates a CameraPosition from the builder
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    return false;
//                }
//            });
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

}