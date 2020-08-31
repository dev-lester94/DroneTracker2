package com.example.dronetracker2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.dronetracker2.ui.details.DetailItem;
import com.example.dronetracker2.ui.details.DetailsFragment;
import com.example.dronetracker2.ui.map.MapFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private MapFragment fragmentMap;
    private DetailsFragment fragmentDetails;

    private CurrentData currentData;
    public boolean isWebSocketActive;

    private static final int DRONE_LOADER_ID = 1;

    private OkHttpClient client;
    private WebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentData = new CurrentData(this);

        mAuth = FirebaseAuth.getInstance();

        //Set up the tabs and viewpager
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        //Create the two fragments
        fragmentMap = new MapFragment();
        fragmentDetails = new DetailsFragment();

        //Add the fragments to the viewPagerAdapter and set the viewPager
        //to the adapter
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(fragmentMap, "Map");
        viewPagerAdapter.addFragment(fragmentDetails, "Details");
        viewPager.setAdapter(viewPagerAdapter);


        client = new OkHttpClient();
        String server = "ws://10.0.2.2:9003/test-ws";
        Request request = new Request.Builder().url(server).build();
        MyWebsocketListener listener = new MyWebsocketListener(this);

        ws = client.newWebSocket(request, listener);
        //ws.send("username:" + username);
        //ws.send("password:" + password);

        client.dispatcher().executorService().shutdown();
        Log.i("connect", "connect");


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);
    }

    public void updateUi(FirebaseUser currentUser) {
        if(currentUser == null){
            Log.i("here", "here");
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        }else{
            Log.i("signin", "signin");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signOut:
                OnWebSocketClose();
                mAuth.signOut();
                updateUi(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void OnDroneDetailSelected(DetailItem detailItem)
    {
        fragmentMap.LockOntoAircraft(detailItem);
        viewPager.setCurrentItem(0);
    }

    public void output(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentData.ProcessNewMessages(text);
            }
        });
    }

    public void onWebSocketOpen()
    {
        isWebSocketActive = true;
    }

    public void onWebSocketClose()
    {
        isWebSocketActive = false;
    }


    public void NewFlightPlanMessageProcessed(String gufi)
    {
        Log.i("flightplan", "flightplandraw");
        fragmentMap.DrawFlightPlans(gufi);
    }

    public void NewAircraftPositionMessageProcessed(String gufi)
    {
        fragmentMap.DrawAircraft(gufi);
        Log.i("drawAircraft", "DrawAircraft");
        fragmentDetails.UpdateDetails();
    }

    public void OnWebSocketClose()
    {
        ws.close(1000, "closing websocket");
        Log.i("websocketclose","websocketclose");
        fragmentMap.EraseAll();
        currentData.aircraft.clear();
        currentData.flightplans.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OnWebSocketClose();
    }
}