package com.example.gh_bloodbank;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Login extends AppCompatActivity {
    EditText username, password;
    Button login;

    String usernames, passwords, name, dob, weight, height, date_of_donation, medical_status;
    ProgressBar loading;
    Accessories loginAccessor;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login_button);
        loading = findViewById(R.id.loading);
        loginAccessor = new Accessories(Login.this);
        auth = FirebaseAuth.getInstance();

        usernames = username.getText().toString().trim();
        passwords = password.getText().toString().trim();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernames = username.getText().toString().trim();
                passwords = password.getText().toString().trim();

                if (!usernames.equals("")) {
                    if (!passwords.equals("")) {
                        LoginUser(usernames, passwords);
//                        new LoggingIn("https://ghbb.000webhostapp.com/login.php",usernames,passwords).execute();
                    } else {
                        password.setError("Required");
                    }
                } else {
                    username.setError("Required");
                }

            }
        });
    }

    private void LoginUser(String usernames, String passwords) {
        loading.setVisibility(VISIBLE);
        auth.signInWithEmailAndPassword(usernames + "@gmail.com", passwords)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        loading.setVisibility(GONE);
                        // Sign in success, update UI with the signed-in user's information
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Toast.makeText(Login.this, "Login successful",
                                Toast.LENGTH_SHORT).show();
                        loginAccessor.put("login_checker", true);
                        FirebaseUser user = auth.getCurrentUser();
                        fetchuserdata(user);
                    } else {
                        loading.setVisibility(GONE);
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });
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
                            name = child.getValue().toString();
                            loginAccessor.put("name", name);
                        }

                        if (child.getKey().equals("description")) {
                            dob = child.getValue().toString();
                            loginAccessor.put("dob", dob);
                        }

                        if (child.getKey().equals("weight")) {
                            weight = child.getValue().toString();
                            loginAccessor.put("weight", weight);
                        }

                        if (child.getKey().equals("height")) {
                            height = child.getValue().toString();
                            loginAccessor.put("height", height);
                        }

                        if (child.getKey().equals("date_of_donation")) {
                            date_of_donation = child.getValue().toString();
                            loginAccessor.put("date_donation", date_of_donation);
                        }

                        if (child.getKey().equals("medical_status")) {
                            medical_status = child.getValue().toString();
                            loginAccessor.put("medical_status", medical_status);
                        } else {
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class LoggingIn extends AsyncTask<Void, Void, String> {

        String url_location, username, password;

        public LoggingIn(String url_location, String username, String password) {
            this.url_location = url_location;
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                URL url = new URL(url_location);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                String data =
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String fetch;
                while ((fetch = bufferedReader.readLine()) != null) {
                    stringBuffer.append(fetch);
                }
                String string = stringBuffer.toString();
                inputStream.close();
                return string;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return "please check internet connection";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.setVisibility(GONE);
            if (s.equals("login Successful")) {
                loginAccessor.put("login_checker", true);
                finish();
                startActivity(new Intent(Login.this, MainActivity.class));
                new Get_User_Information("https://ghbb.000webhostapp.com/FetchUserInfo.php", usernames, passwords).execute();
            } else {

//                welcomeMessage.setTextColor(getResources().getColor(R.color.red));
//                welcomeMessage.setText(s);
//                welcomeMessage.setVisibility(VISIBLE);
                Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();

            }
        }
    }

    public class Get_User_Information extends AsyncTask<Void, Void, String> {
        String urlLocation;
        String username, password;

        public Get_User_Information(String urlLocation, String username, String password) {
//            this.task = task;
            this.urlLocation = urlLocation;
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            alertDialog = new ProgressDialog(Login.this);
//            alertDialog.setMessage(task);
//            alertDialog.setCancelable(false);
//            alertDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            try {
                URL url = new URL(urlLocation);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String fetch = "";
                while ((fetch = bufferedReader.readLine()) != null) {
                    stringBuffer.append(fetch);
                }
                String string = stringBuffer.toString();
                inputStream.close();
                return string;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Please Check Your Internet Connection";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!s.equals("Unable to Find User") || !s.equals("Please Check Your Internet Connection")) {
                String lot_number = "", name = "", dob = "", weight = "",
                        height = "", date_of_donation = "", medical_status = "";

                // getting the individual values based json in php using the keys there
                try {
                    JSONObject userobject = new JSONObject(s);
                    lot_number = userobject.getString("lot_number");
                    name = userobject.getString("name");
                    dob = userobject.getString("dob");
                    weight = userobject.getString("weight");
                    height = userobject.getString("height");
                    date_of_donation = userobject.getString("date_of_donation");
                    medical_status = userobject.getString("medical_status");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //storing the info in the shared preferences class to future usage
                loginAccessor.put("lot_number", lot_number);
                loginAccessor.put("name", name);
                loginAccessor.put("dob", dob);
                loginAccessor.put("weight", weight);
                loginAccessor.put("height", height);
                loginAccessor.put("dodonation", date_of_donation);
                loginAccessor.put("medicalstatus", medical_status);
//
//                Toast.makeText(Login.this,lot_number,Toast.LENGTH_LONG).show();
//                Toast.makeText(Login.this,name,Toast.LENGTH_LONG).show();
//                Toast.makeText(Login.this,dob,Toast.LENGTH_LONG).show();
//                Toast.makeText(Login.this,weight,Toast.LENGTH_LONG).show();
//                Toast.makeText(Login.this,height,Toast.LENGTH_LONG).show();
//                Toast.makeText(Login.this,date_of_donation,Toast.LENGTH_LONG).show();
//                Toast.makeText(Login.this,medical_status,Toast.LENGTH_LONG).show();

            } else {
                android.app.AlertDialog.Builder getS = new android.app.AlertDialog.Builder(Login.this);
                getS.setMessage(s);
                getS.show();

            }
//            alertDialog.dismiss();
        }

    }


}
