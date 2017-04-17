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

        Activity act = (Activity) this;

        //画面のパーツを取得
        final SeekBar sb0 = (SeekBar)act.findViewById(R.id.seekBar01);
        final TextView tv0 = (TextView)act.findViewById(R.id.TextView00);
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
                        // ツマミを離したときに呼ばれる
                        //マップの中心座標を、プレイス検索するメソッドに渡すため
                        final double lat = mMap.getCameraPosition().target.latitude;
                        final double lon = mMap.getCameraPosition().target.longitude;
                        //ワーカースレッド上で動かす
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // マルチスレッドにしたい処理 ここから
                                getData(sb0.getProgress()*200, lat, lon);

                                // マルチスレッドにしたい処理 ここまで
                            }
                        }).start();
                    }
                }
        );

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // マルチスレッドにしたい処理 ここから
//                getData();
//
//                // マルチスレッドにしたい処理 ここまで
//            }
//        }).start();

    }

    public void getData(int radius, double latitude, double longtitude){
        try {
            // ワーカースレッドにて実行しなければならない(Webアクセスだから)
            Log.i("DEBUG", "start data retreiving");

            // URL生成（現在地を元に）
            //Location loc = LocationServices.FusedLocationApi.getLastLocation(sSelf.mGoogleApiClient);
//            double lat = loc.getLatitude();
//            double lng = loc.getLongitude();

            LatLng  latLng = new LatLng(35, 139);
            double lat = latLng.latitude;
            double lng = latLng.longitude;

//            StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json");
//            urlStrBuilder.append("?location=" + lat + "," + lng);
//            urlStrBuilder.append("&sensor=true&rankby=distance&types=convenience_store&key=AIzaSyBXUpKxiq_jDSgsPysP-2LePVEmneRjuNo");

//            StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json");
//            urlStrBuilder.append("?location=" + latitude + "," + longtitude);
//            urlStrBuilder.append("&sensor=true&radius=" + radius+"&types=convenience_store&key=AIzaSyBXUpKxiq_jDSgsPysP-2LePVEmneRjuNo");
            StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
            urlStrBuilder.append("?location=" + latitude + "," + longtitude);
            //クエリストリングでは空白を+か%20にするが、ちゃんと検索できてるのかよくわからない...。「寺 or 神社」で検索
            urlStrBuilder.append("&sensor=true&language=ja&keyword=寺%20神社&radius=" + radius+"&key=AIzaSyBXUpKxiq_jDSgsPysP-2LePVEmneRjuNo");

            URL u = new URL(urlStrBuilder.toString());

            Log.i("DEBUG", "processing...");
            // APIを叩いてJSONをダウンロード
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedInputStream is = new BufferedInputStream(con.getInputStream());

            Log.i("DEBUG", "In Proccesing, showing results.");
            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            String jsonResult="";
            while ((bytesRead = is.read(buffer)) != -1) {
                String buf = new String(buffer, 0, bytesRead);
                jsonResult += buf;
                Log.i("DEBUG", buf);
            }
            //なぜかJSONObjectのtoString(たぶん...)だと全部表示されない
            //Log.i("DEBUG", jsonResult);

//            String path = Environment.getExternalStorageDirectory() + "/tekitou/";
//            String fileName = "tekitou.json";
//            File dir = new File(path);
//            dir.mkdirs();
//            Log.i("ARE!", "CreateFile");
//            File outputFile = new File(dir, fileName);
//            Log.i("ARE!", "OpenForWrite");
//            //FileOutputStream fos = new FileOutputStream(outputFile);
//            FileOutputStream fos  = openFileOutput(fileName, MODE_PRIVATE);
//
//            bytesRead = -1;
//            buffer = new byte[1024];
//
//            while ((bytesRead = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, bytesRead);
//            }
//            fos.flush();
//            fos.close();
            is.close();
            Log.i("ARE!", "Finished data retrieving");

            //JSONをパースする
            JSONObject jsonObject = new JSONObject(jsonResult);
            final JSONArray datas = jsonObject.getJSONArray("results");
            //GUIを操作するなら、ワーカースレッドじゃダメでメインorUIスレッドとやらでやる必要がある
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        //一件一件、マップ上にマーカー設置
                        for (int i = 0; i < datas.length(); i++) {
                            JSONObject data = datas.getJSONObject(i);
                            JSONObject geometry = datas.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");

                            // 名前を取得
                            String name = data.getString("name");
                            // 近所(おおまかな地名？)を取得
                            String vic = data.getString("vicinity");

                            double lat = Double.parseDouble(geometry.getString("lat"));
                            double lng = Double.parseDouble(geometry.getString("lng"));

                            LatLng latLng = new LatLng(lat, lng);

                            if(data.isNull("photos")){
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                            }else {
                                //プレイスフォトがある場合は青色のマーカーを表示する
                                Log.i("JSON", "It has photos: " + name +"("+ lat + "," + lng+")");
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            }
                            Log.i("DEBUG", " Marker creation: "+name + "  ++" + vic +  "(" +lat + "  "+lng+")");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }});

        }catch(Exception e){
            e.printStackTrace();
        }

        //this.showResults();
    }

    public void showResults(){
        try {
            Log.i("ARE!", "SHOWING RESULTS...");
            // ファイル読み込み
            FileInputStream fileInputStream;
            String path = Environment.getExternalStorageDirectory() + "/tekitou/";
            String fileName = "tekitou.json";
            File dir = new File(path);
            File inputFile = new File(dir, fileName);
            Log.i("ARE!", "SHOWING RESULTS...2222");
            //fileInputStream = new FileInputStream(inputFile);
            fileInputStream = openFileInput(fileName);
            byte[] readBytes = new byte[fileInputStream.available()];
            Log.i("ARE!", "SHOWING RESULTS...3333");
            fileInputStream.read(readBytes);
            String json = new String(readBytes);
            Log.i("ARE!", "SHOWING RESULTS...4444");
            Log.i("ARE!", json + "-JSONENDENDENDEND");
            JSONObject jsonObject = new JSONObject(json);
            final JSONArray datas = jsonObject.getJSONArray("results");
            //Log.i("ARE!", jsonObject.getString("results"));

            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);
                // 名前を取得
                String name = data.getString("name");
                // 年齢を取得
                String vic = data.getString("vicinity");
                Log.i("ARE!", name + "  ++" + vic);
            }

            Log.i("ARE!", "Handler!");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
//                        JSONObject geometry = datas.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
//                        double lat = Double.parseDouble(geometry.getString("lat"));
//                        double lng = Double.parseDouble(geometry.getString("lng"));
//                        Log.i("ARE!Marker", lat + "  "+lng);
//                        LatLng latLng = new LatLng(lat, lng);
//                        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker First Shop"));

                        for (int i = 0; i < datas.length(); i++) {
                            JSONObject data = datas.getJSONObject(i);
                            JSONObject geometry = datas.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                            // 名前を取得
                            String name = data.getString("name");
                            // 年齢を取得
                            String vic = data.getString("vicinity");


                            double lat = Double.parseDouble(geometry.getString("lat"));
                            double lng = Double.parseDouble(geometry.getString("lng"));

                            LatLng latLng = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                            Log.i("ARE!", name + "  ++" + vic);
                            Log.i("ARE!Marker", lat + "  "+lng);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


//            // JSONのパース with Jackson
//            ObjectMapper mapper = new ObjectMapper();
//            Object root = mapper.readValue(json, Object.class);
//            Map<?, ?> rootMap = mapper.readValue(json, Map.class);
//            ArrayList nextArray = (ArrayList) rootMap.get("results");
//            //ArrayList mLatlngList = new ArrayList<LatLngName>();
//
//            for (int i = 0; i < nextArray.size(); i++) {
//                Map<?, ?> thirdMap = (Map<?, ?>) nextArray.get(i);
//                Map<?, ?> forthMap = (Map<?, ?>) ((Map<?, ?>) thirdMap.get("geometry")).get("location");
//                Double lat = (Double) forthMap.get("lat");
//                Double lng = (Double) forthMap.get("lng");
//                String name = (String) thirdMap.get("name");
//                Log.i("ARE!", "lat=" + lat + " lng=" + lng + " name=");
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    CircleOptions circleOptions = null;
    Circle circle = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mMapUISettings = mMap.getUiSettings();

        // Add a marker in Sydney and move the camera
        LatLng tokyo = new LatLng(35, 139);
        mMap.addMarker(new MarkerOptions().position(tokyo).title("Marker in Tokyo?"));
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
