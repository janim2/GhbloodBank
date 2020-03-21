package com.example.gh_bloodbank;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    TextView name, dob, weight, height, dofdonation, medicalStatus;
    String sid, sname, sdob, sweight, sheight, sdofdonation, smedicalStatus;
    String s_name, s_dob, s_weight, s_height, s_dofdonation, s_medicalStatus;
    Accessories mainAccessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainAccessor = new Accessories(MainActivity.this);
        name = findViewById(R.id.fullname);
        dob = findViewById(R.id.dob);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        dofdonation = findViewById(R.id.dofdonation);
        medicalStatus = findViewById(R.id.medicalStatus);

//        sid = mainAccessor.getString("lot_number");
        sname = mainAccessor.getString("name");
        sdob = mainAccessor.getString("dob");
        sweight = mainAccessor.getString("weight");
        sheight = mainAccessor.getString("height");
        sdofdonation = mainAccessor.getString("date_donation");
        smedicalStatus = mainAccessor.getString("medical_status");

        if(name.getText().equals("")){
            try {
                fetchuserdata(FirebaseAuth.getInstance().getCurrentUser());
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            name.setText(sname);
            dob.setText(sdob);
            weight.setText(sweight);
            height.setText(sheight);
            dofdonation.setText(sdofdonation);
            medicalStatus.setText(smedicalStatus);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();
                if(query.equals("a") || query.equals("b") || query.equals("ab") || query.equals("o")){
                    Intent seacrchintent = new Intent(MainActivity.this, SearchActivity.class);
                    seacrchintent.putExtra("blood_type", query);
                    startActivity(seacrchintent);
                }else{
                    Toast.makeText(MainActivity.this, "Invalid blood type", Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            mainAccessor.put("login_checker", false);
            FirebaseAuth.getInstance().signOut();
            mainAccessor.clearStore();
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mainAccessor.getBoolean("login_checker")) {
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }

    private void fetchuserdata(FirebaseUser user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("lots")
                .child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("name")) {
                            s_name = child.getValue().toString();
                            name.setText(s_name);
                        }

                        if (child.getKey().equals("description")) {
                            s_dob = child.getValue().toString();
                            dob.setText(s_dob);
                        }

                        if (child.getKey().equals("weight")) {
                            s_weight = child.getValue().toString();
                            weight.setText(s_weight);
                        }

                        if (child.getKey().equals("height")) {
                            s_height = child.getValue().toString();
                            height.setText(s_height);
                        }

                        if (child.getKey().equals("date_of_donation")) {
                            s_dofdonation = child.getValue().toString();
                            dofdonation.setText(s_dofdonation);
                        }

                        if (child.getKey().equals("medical_status")) {
                            s_medicalStatus = child.getValue().toString();
                            medicalStatus.setText(s_medicalStatus);
                        } else {
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }
                    Toast.makeText(MainActivity.this, s_name, Toast.LENGTH_LONG).show();


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
