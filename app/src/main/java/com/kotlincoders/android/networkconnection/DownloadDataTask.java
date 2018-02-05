package com.kotlincoders.android.networkconnection;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by root on 1/30/18.
 */

public class DownloadDataTask extends AsyncTask<String, Integer, String> {

    DownloadCompleteListener downloadCompleteListener;

    public DownloadDataTask(DownloadCompleteListener downloadCompleteListener) {

        this.downloadCompleteListener = downloadCompleteListener;

    }

    @Override
    protected String doInBackground(String... strings) {


        try {

            return DownloadDATA(strings[0]);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        downloadCompleteListener.downloadCompleteData(s);

    }

    private String DownloadDATA(String urlString) throws IOException {

        InputStream inputStream = null;

        try {

            URL url = new URL(urlString);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();

            return convertStremToString(inputStream);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    private String convertStremToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return new String(stringBuilder);
    }

}
