package com.example.dronetracker2.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dronetracker2.CurrentData;
import com.example.dronetracker2.R;
import com.example.dronetracker2.ui.details.DetailItem;
import com.example.dronetracker2.ui.messages.FGObject;
import com.example.dronetracker2.ui.messages.OperationVolume;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.List;


public class MapFragment extends Fragment implements  OnMapReadyCallback{

    private GoogleMap gMap;
    private HashMap<String, Marker> aircraftMarkers = new HashMap<>();

    private boolean isMapReady = false;


    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        Log.i("mapisReady", "mapisReady");

        LatLng sjsu = new LatLng(37.3352, -121.8811);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sjsu, 16));

        isMapReady = true;

        //listener.mapisReady();
    }

    public void DrawFlightPlans(String gufi) {
        if (!isMapReady)
            return;

        for (OperationVolume operationVolume : CurrentData.Instance.flightplans.get(gufi).message.operation_volumes) {
            FGObject flightGeography = operationVolume.flight_geography;
            List<List<List<Double>>> coordinates = flightGeography.coordinates;
            PolygonOptions poly = new PolygonOptions();
            poly.fillColor(Color.GRAY);

            for (List<Double> coordinate : coordinates.get(0)) {
                double latitude = coordinate.get(1);
                double longitude = coordinate.get(0);
                LatLng latLng = new LatLng(latitude, longitude);
                poly.add(latLng);
            }
            gMap.addPolygon(poly);
        }
    }

    public void LockOntoAircraft(DetailItem detailItem) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(detailItem.getLatPlaceholder()),
                        Double.parseDouble(detailItem.getLngPlaceholder())))
                .zoom(18)
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void DrawAircraft(String gufi) {

        if (!isMapReady)
            return;

        List<Double> coordinate = CurrentData.Instance.aircraft.get(gufi).message.lla;
        double latitude = coordinate.get(0);
        double longitude = coordinate.get(1);
        LatLng newLatLng = new LatLng(latitude, longitude);

        if (aircraftMarkers.containsKey(gufi))
        {
            LatLng oldLatLng = aircraftMarkers.get(gufi).getPosition();
            float heading = (float) SphericalUtil.computeHeading(oldLatLng, newLatLng);
            aircraftMarkers.get(gufi).setRotation(heading);
            Log.d("WS", "" + gufi + ": old: " + oldLatLng + ", new: " + newLatLng + ", heading: " + heading);

            aircraftMarkers.get(gufi).setPosition(newLatLng);
        }
        else
        {
            String callsign = "<empty>";
            if (CurrentData.Instance.flightplans.containsKey(gufi))
            {
                callsign = CurrentData.Instance.flightplans.get(gufi).message.callsign;
            }
            BitmapDescriptor bitmapDescriptor = bitmapDescriptorFromVector(getActivity(), R.drawable.ic_flight_black_24dp);
            Marker newMarker = gMap.addMarker(new MarkerOptions().position(newLatLng).icon(bitmapDescriptor).title(callsign));
            aircraftMarkers.put(gufi, newMarker);
        }
    }

    private float degreesFromCoordinate(LatLng latlngOld, LatLng latlngNew) {

        double deltaLng = latlngNew.longitude - latlngOld.longitude;
        double lat1 = latlngOld.latitude;
        double lat2 = latlngNew.latitude;

        double angle = Math.atan2(Math.sin(deltaLng) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) *
                        Math.cos(lat2) * Math.cos(deltaLng));

        return (float)Math.toDegrees(angle);
    }

    public void EraseAll()
    {
        if (gMap == null)
            return;

        gMap.clear();
        aircraftMarkers.clear();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}