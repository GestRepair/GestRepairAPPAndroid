package com.example.obscu.gestrepair5;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Obscu on 31/07/2017.
 */

public class Service extends Activity {

    RequestQueue rq;

    TextView typeService, priceService, descriptionService, imageService, googlePlusUrlText;
    ImageView iService;

    String name, description, jdescription, jimage, gplusUrl, iDService;

    Ip ip = new Ip();
    String url = ip.stIp() + "/service";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services);
        rq = Volley.newRequestQueue(this);

        typeService = (TextView) findViewById(R.id.ServiceType);
        priceService = (TextView) findViewById(R.id.ServicePrice);
        descriptionService = (TextView) findViewById(R.id.ServiceDescription);
        iService = (ImageView) findViewById(R.id.imgService);

        sendjsonrequest();

    }

    public void sendjsonrequest() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("data");
                    Intent Intent = getIntent();
                    int intValue = Intent.getIntExtra("position", 0);
                    Log.i("TAG", intValue + "");

                    JSONObject jsonObject = (JSONObject) jsonArray.get(intValue);
                    iDService = jsonObject.getString("idService");
                    name = jsonObject.getString("nameService");
                    jdescription = jsonObject.getString("priceService");
                    description = jsonObject.getString("description");
                    jimage = jsonObject.getString("photo");

                    typeService.setText(name);
                    priceService.setText(jdescription+"€");
                    descriptionService.setText(description);
                    //imageService.setText(jimage);
                    getJsonImage(iDService);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                typeService.setText("Ups, ocorreu um erro");
            }
        });

        rq.add(jsonObjectRequest);

    }

    private void getJsonImage(String iDService) {
        String url2 = ip.stIp() + "/service/img/" + iDService;
        Glide.with(this).load(url2).into(iService);

    }
}

