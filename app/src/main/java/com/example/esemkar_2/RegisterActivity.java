package com.example.esemkar_2;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esemkar_2.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView name, username, email, date, password, confirm_password;
        name = findViewById(R.id.registerTextName);
        username = findViewById(R.id.registerUsernameText);
        email = findViewById(R.id.registerTextEmailAddress);
        date = findViewById(R.id.registerTextDate);
        password = findViewById(R.id.registerTextPassword);
        confirm_password = findViewById(R.id.registerTextPassword2);

        Button registerBtn = findViewById(R.id.registerButton);

        registerBtn.setOnClickListener(v -> {
            if (!name.getText().toString().isEmpty() && !username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !date.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirm_password.getText().toString().isEmpty()) {
                if (password.getText().toString().equals(confirm_password.getText().toString())) {
                    new RegisterTask().execute(name.getText().toString(), username.getText().toString(), email.getText().toString(), date.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(RegisterActivity.this, "Password tidak sama", LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Tidak boleh ada yang kosong", LENGTH_SHORT).show();
            }
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, User> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected User doInBackground(String... strings) {

            var name = strings[0];
            var username = strings[1];
            var email = strings[2];
            var date = strings[3];
            var password = strings[4];

            String formattedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC)
                    .toString();

            try {
                URL url = new URL("http://10.0.2.2:5000/api/sign-up");
                var conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                var jsonBody = "{" +

                        "\"fullname\": \"" + name + "\"," +
                        "\"username\": \"" + username + "\"," +
                        "\"dateOfBirth\": \"" + formattedDate + "\"," +
                        "\"password\": \"" + password + "\"" +

                        "}";

                try(OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    return new User(
                            Integer.parseInt(json.optString("id")),
                            json.optString("username"),
                            json.optString("fullName"),
                            json.optString("image"),
                            json.optString("password")
                    );
                }


            } catch (Exception e) {
                Log.e("REGISTER_ERROR", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Gagal Register", LENGTH_SHORT).show();
            }
        }
    }
}