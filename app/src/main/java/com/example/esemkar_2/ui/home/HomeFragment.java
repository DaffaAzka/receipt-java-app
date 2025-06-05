package com.example.esemkar_2.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esemkar_2.adapter.CategoriesAdapter;
import com.example.esemkar_2.databinding.FragmentHomeBinding;
import com.example.esemkar_2.model.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements CategoriesAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView rv;

    private final ArrayList<Category> categoryArrayList = new ArrayList<>();
    CategoriesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rv = binding.rvCategories;

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoriesAdapter(categoryArrayList, this);
        rv.setAdapter(adapter);

        new CategoriesTask().execute();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Category category) {

    }

    private class CategoriesTask extends AsyncTask<Void, Void, List<Category>> {

        @Override
        protected List<Category> doInBackground(Void... voids) {
            try {
                URL url = new URL("http://10.0.2.2:5000/api/categories");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    var jsonToArray = new JSONArray(response.toString());
                    List<Category> categories = new ArrayList<>();

                    for (int i = 0; i < jsonToArray.length(); i++) {
                        JSONObject obj = jsonToArray.getJSONObject(i);
                        categories.add(new Category(obj.getInt("id"),  obj.getString("name"),  obj.getString("icon")));
                    }

                    return categories;
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("ME_ERROR", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Category> categories) {
            if (categories != null) {
                adapter.updateCategories(categories);
            } else {
                Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        }
    }

}