package com.example.user1.maptest1;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.radius;

/**
 * Created by ryoyamomose on 2017/04/17.
 */

/**
 * 非同期処理のまとめは下記がわかりやすい．
 * http://tiro105.hateblo.jp/entry/2015/04/15/203125
 *
 * AsyncTask<doInBackgroundが受け取る型，onProgressUpdateが受け取る型，onPostExecuteが受け取る型>らしい．
 * ココらへんの説明は下記がわかりやすい．
 * https://sites.google.com/site/technoute/android/thread/params
 */

public class HttpGetData extends AsyncTask<Double, Void, JSONArray> {
    GoogleMap gMap;

    public HttpGetData(GoogleMap _gMap) {
        super();
        gMap = _gMap;
    }

    // doInBackgroundの事前準備処理（UIスレッド）
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // 別スレッド処理
    @Override
    protected JSONArray doInBackground(Double... param) {
        JSONArray datas = new JSONArray();

        // 引数の取得
        int radius = param[0].intValue();
        double latitude = param[1];
        double longtitude = param[2];

        try {

            StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
            urlStrBuilder.append("?location=" + latitude + "," + longtitude);
            urlStrBuilder.append("&sensor=true&language=ja&keyword=寺%20神社&radius=" + radius+"&key=AIzaSyBXUpKxiq_jDSgsPysP-2LePVEmneRjuNo");
            URL u = new URL(urlStrBuilder.toString());

            // HTTP request
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedInputStream is = new BufferedInputStream(con.getInputStream());


            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            String jsonResult="";
            while ((bytesRead = is.read(buffer)) != -1) {
                String buf = new String(buffer, 0, bytesRead);
                jsonResult += buf;
            }

            is.close();

            JSONObject jsonObject = new JSONObject(jsonResult);
            datas = jsonObject.getJSONArray("results");

        } catch(Exception e) {
            e.printStackTrace();
        }

        return datas;
    }


    // doInBackgroundの事後処理(UIスレッド)
    protected void onPostExecute(JSONArray status) {
        try{
            //一件一件、マップ上にマーカー設置
            for (int i = 0; i < status.length(); i++) {
                JSONObject data = status.getJSONObject(i);
                JSONObject geometry = data.getJSONObject("geometry").getJSONObject("location");

                // 名前と土地名を取得
                String name = data.getString("name");

                // 座標を取得
                double lat = Double.parseDouble(geometry.getString("lat"));
                double lng = Double.parseDouble(geometry.getString("lng"));
                LatLng latLng = new LatLng(lat, lng);

                //プレイスフォトがある場合は青色のマーカーを表示する
                if (data.isNull("photos")) {
                    gMap.addMarker(new MarkerOptions().position(latLng).title(name));
                } else {
                    gMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 進捗状況をUIに反映するための処理(UIスレッド)
    @Override
    protected void onProgressUpdate(Void... values) {
        // progressDialogなどで進捗表示したりする
    }

    // 非同期処理がキャンセルされた場合の処理
    @Override
    protected void onCancelled(JSONArray s) {
        super.onCancelled(s);
    }
}
