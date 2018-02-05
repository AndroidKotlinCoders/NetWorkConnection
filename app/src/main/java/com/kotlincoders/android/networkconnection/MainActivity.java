package com.kotlincoders.android.networkconnection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements DownloadCompleteListener {

    public static String TAG = "MainActivity";
    ProgressDialog mProgressDialog;
    TextView txt_result;
    // URL to get contacts JSON
    private static String url = "https://api.androidhive.info/contacts/";
    public static final String URL = "https://api.github.com";
    private List<Repository> mRepos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_result = (TextView) findViewById(R.id.result);


//         if(isWifiConnected());   //This Method to retrieves huge amounts of data.

    }

    public void startDownload(View view) {

        txt_result.setText(" ");

        int id = view.getId();


        if (isNetworkConnected()) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();


            if (id == R.id.AsyncTask) {

                new DownloadDataTask(this).execute(URL);

            } else if (id == R.id.Volley) {

                ConnectWithVolley(URL);

            } else if (id == R.id.OkHTTP) {

                ConnectWithOkHttp(URL);

            } else if (id == R.id.RetrofitCalls) {

                ConnectWithRetrofitCalls(URL);
            }

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setMessage("It looks like your internet connection is off. Please turn it " +
                            "on and try again")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }


    }

    private void ConnectWithRetrofitCalls(String BaseURL) {

        Log.e("MainActivity", "BaseURL=>" + BaseURL);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL) // 1
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<ArrayList<Repository>> call = retrofitAPI.retrieveRepositories();

        call.enqueue(new Callback<ArrayList<Repository>>() {
            @Override
            public void onResponse(Call<ArrayList<Repository>> call,
                                   retrofit2.Response<ArrayList<Repository>> response) {

                mRepos = response.body();

                String name = mRepos.get(0).getName();
                String fullname = mRepos.get(0).getFullName();
                String htmlURl = mRepos.get(0).getHTMLurl();
                String login = mRepos.get(0).getOwner().getLogin();

                String result = "Here we are just fetching only 1st index json data.\n\n\n\n\n"+name+"\n"+fullname+"\n"+htmlURl+"\n"+login+"\n";

                Log.e("MainActivity", "htmlURl---->" + htmlURl);
                Log.e("MainActivity", "name---->" + name);
                Log.e("MainActivity", "fullname---->" + fullname);
                Log.e("MainActivity", "CoonectRetrofitCalls---->" + login);

                downloadCompleteData(result);
            }

            @Override
            public void onFailure(Call<ArrayList<Repository>> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mProgressDialog != null) {

                    mProgressDialog.hide();

                }
            }
        });
    }

    private void ConnectWithVolley(String sURL) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, sURL, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                downloadCompleteData(response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mProgressDialog != null) {

                    mProgressDialog.hide();

                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                // HashMap Parameters for in Case POST mehto call

                Map<String, String> params = new HashMap<String, String>();
                params.put("name", " ");
                params.put("domain", " ");

                return params;
            }
        };

        requestQueue.add(stringRequest);


    }

    private void ConnectWithOkHttp(String url) {

        OkHttpClient client = new OkHttpClient();

        Log.e("MainActivity", "BaseURL===>" + url);

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                final String result = response.body().string();

                Log.e("MainActivity", "onResponse===>" + result);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        downloadCompleteData(result);
                    }
                });
            }
        });
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }

    @Override
    public void downloadCompleteData(String result) {

        if (mProgressDialog != null) {
            mProgressDialog.hide();
        }

        txt_result.setText("Responces:===>" + result);


    }
}
