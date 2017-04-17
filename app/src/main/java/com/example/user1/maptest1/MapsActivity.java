package com.example.user1.maptest1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;

// branch test

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handler = new Handler();

        //画面のパーツを取得
        final SeekBar sb0 = (SeekBar)this.findViewById(R.id.seekBar01);
        final TextView tv0 = (TextView)this.findViewById(R.id.TextView00);

        // シークバーの初期値をTextViewに表示
        tv0.setText("設定値:"+sb0.getProgress());

        //シークバーを操作したときの動作をセット
        sb0.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        // ツマミをドラッグしたときに呼ばれる
                        tv0.setText("設定値:"+sb0.getProgress());
                        updateCircle(sb0.getProgress());
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // ツマミに触れたときに呼ばれる
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //ツマミを離したときに呼ばれる
                        //マップの中心座標を、プレイス検索するメソッドに渡すため
                        Double[] params = new Double[3];
                        params[0] = (double)sb0.getProgress()*200;
                        params[1] = mMap.getCameraPosition().target.latitude;
                        params[2] = mMap.getCameraPosition().target.longitude;

                        HttpGetData get = new HttpGetData(mMap);
                        get.execute(params);
                    }
                }
        );

    }


    CircleOptions circleOptions = null;
    Circle circle = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mMapUISettings = mMap.getUiSettings();

        // Add a marker in Tokyo and move the camera
        LatLng tokyo = new LatLng(35, 139);
        mMap.addMarker(new MarkerOptions().position(tokyo).title("Marker in Tokyo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tokyo));
        mMapUISettings.setZoomControlsEnabled(true);
        // Instantiates a new CircleOptions object and defines the center and radius
        circleOptions = new CircleOptions().center(new LatLng(35, 139)).radius(10000);
        // In meters
        circle = mMap.addCircle(circleOptions);
    }

    public void updateCircle(Integer size){
        circle.setRadius((double) size * 200.0);
        circle.setCenter( new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude) );
    }
}
