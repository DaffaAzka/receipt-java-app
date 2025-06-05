package com.example.esemkar_2.ui.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.esemkar_2.databinding.FragmentNotificationsBinding;
import com.example.esemkar_2.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ProfileTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... voids) {

            try {
                URL url = new URL("http://10.0.2.2:5000/api/me");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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

                conn.disconnect();
            } catch (Exception e) {
                Log.e("ME_ERROR", "Error: " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
//                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
//                finish();
//                v.setText(user.toString());
            }
        }
    }
}