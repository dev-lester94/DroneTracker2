package com.example.dronetracker2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class DroneLoader extends AsyncTaskLoader<String> {

    /** Tag for log messages */
    private static final String LOG_TAG = DroneLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link DroneLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public DroneLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public String loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        String json = QueryUtils.fetchDroneData(mUrl);
        return json;
    }
}