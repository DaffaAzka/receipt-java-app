package com.example.esemkar_2;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = findViewById(R.id.usernameLoginText);
        TextView password = findViewById(R.id.passwordLoginText);
        TextView tv_reg = findViewById(R.id.textView2);
        Button btn = findViewById(R.id.loginButton);

        btn.setOnClickListener(v -> {
            new LoginTask().execute(username.getText().toString(), password.getText().toString());
        });

        tv_reg.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

    }


    private class LoginTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... strings) {

            var username = strings[0];
            var password = strings[1];

            try {

                URL url = new URL("http://10.0.2.2:5000/api/sign-in");
                var conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                var jsonBody = "{" +
                        "\"username\": \"" + username + "\"," +
                        "\"password\": \"" + password + "\"" +
                        "}";

                try(OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
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

            }catch (Exception e) {
                Log.e("LOGIN_ERROR", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login Gagal", LENGTH_SHORT).show();
            }
        }
    }
}