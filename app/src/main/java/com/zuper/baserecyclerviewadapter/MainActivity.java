package com.zuper.baserecyclerviewadapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zuper.baserecyclerviewadapter.baserecyclerviewadapter.R;
import com.zuper.baserecyclerviewadapter.model.Joke;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import co.zuper.util.baserecyclerviewadapter.pagination.PaginationConstants;
import co.zuper.util.baserecyclerviewadapter.pagination.PaginationListener;
import co.zuper.util.baserecyclerviewadapter.pagination.PaginationLoadMoreCallback;

public class MainActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();

    private RecyclerView recyclerView;

    private JokesAdapter adapter;
    private Gson gson;
    private Type listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViewIds();
        setupRecyclerView();
        gson = new Gson();
        listType = new TypeToken<List<Joke>>() {
        }.getType();
        getJokes(1);
    }

    private void getViewIds() {
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        adapter = new JokesAdapter();
        adapter.setPaginationEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        adapter.setPaginationListener(new PaginationListener() {
            @Override
            public int getTotalPages() {
                return 5;
            }

            @Override
            public int getPreLoadNumber() {
                return 1;
            }

            @Override
            public int getPageLimit() {
                return 10;
            }
        });

        adapter.setPaginationLoadMoreCallback(new PaginationLoadMoreCallback() {
            @Override
            public void loadMore(int pageNo) {
                Log.d("page_no", String.valueOf(pageNo));
                getJokes(pageNo);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getJokes(int pageNo) {
        HttpPostAsyncTask httpPostAsyncTask;
        httpPostAsyncTask = new HttpPostAsyncTask(new Callback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "On success called");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<Joke> jokes = gson.fromJson(jsonObject.getString("results"), listType);
                    adapter.onLoadMoreCompleted();
                    adapter.addAll(jokes, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed called");
                adapter.onLoadMoreFailed(PaginationConstants.LOAD_MORE_STATUS_ERROR);
            }
        });
        String url = "https://icanhazdadjoke.com/search?page=" + pageNo + "&limit=10";
        httpPostAsyncTask.execute(url);
    }

    public static class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {

        public final String TAG = this.getClass().getName();
        public Callback callback;
        private String response;
        private int statusCode;

        public HttpPostAsyncTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "Executing request");
            try {
                URL url = new URL(params[0]);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("GET");

                statusCode = urlConnection.getResponseCode();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                response = convertInputStreamToString(inputStream);
                Log.d(TAG, "Response - " + response);
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (statusCode == 200) {
                Log.d(TAG, "Success");
                callback.onSuccess(response);
            } else {
                Log.d(TAG, "Failed");
                callback.onFailed();
            }
        }

        String convertInputStreamToString(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public interface Callback {

        void onSuccess(String response);

        void onFailed();
    }
}
