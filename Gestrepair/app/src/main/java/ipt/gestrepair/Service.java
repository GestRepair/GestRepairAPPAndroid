package ipt.gestrepair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Obscu on 31/07/2017.
 */

public class Service extends AppCompatActivity {

    RequestQueue rq;

    TextView typeService, priceService, descriptionService, imageService, googlePlusUrlText;
    ImageView iService;
    FileCacher<String> stringCacher = new FileCacher<>(Service.this, "offline.txt");
    String name, description, jdescription, jimage, gplusUrl, iDService;

    Ip ip = new Ip();
    String url = ip.stIp() + "/service";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services);
        rq = Volley.newRequestQueue(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        typeService = (TextView) findViewById(R.id.ServiceType);
        priceService = (TextView) findViewById(R.id.ServicePrice);
        descriptionService = (TextView) findViewById(R.id.ServiceDescription);
        iService = (ImageView) findViewById(R.id.imgService);

        if(AppStatus.getInstance(this).isOnline()) {
            sendjsonrequest();
        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "OFFLINE MODE!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            serviceoffline();
        }
    }

    private void serviceoffline() {
        if (stringCacher.hasCache()) {
            try {
                Intent Intent = getIntent();
                int intValue = Intent.getIntExtra("position", 0);
                Log.i("TAG", intValue + "");
                Log.i("TAG", "Cache Available");
                String text= stringCacher.readCache();
                Log.d("Tag", text+" Cache");
                JSONObject jObject = new JSONObject(text);
                JSONArray jArray = jObject.getJSONArray("data");
                jObject = (JSONObject) jArray.get(intValue);
                Log.d("Tag", jObject+" Cache Json");



                iDService = jObject.getString("idService");
                name = jObject.getString("nameService");
                jdescription = jObject.getString("priceService");
                description = jObject.getString("description");
                jimage = jObject.getString("photo");

                typeService.setText(name);
                priceService.setText(jdescription+"€");
                descriptionService.setText(description);
                //imageService.setText(jimage);
                getJsonImage(iDService);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    String offline = response.toString();

                    //Offline work
                    try {
                        stringCacher.writeCache(offline);
                        Log.i("TAG", "Cache Sucessful"+offline);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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
            //JSONArray jsonArray = null;
            @Override
            public void onErrorResponse(VolleyError error) {
                serviceoffline();
               /* typeService.setText("Ups, ocorreu um erro");
                if (stringCacher.hasCache()) {
                    try {
                        Intent Intent = getIntent();
                        int intValue = Intent.getIntExtra("position", 0);
                        Log.i("TAG", intValue + "");
                        Log.i("TAG", "Cache Available");
                        String text= stringCacher.readCache();
                        Log.d("Tag", text+" Cache");
                        JSONObject jObject = new JSONObject(text);
                        JSONArray jArray = jObject.getJSONArray("data");
                        jObject = (JSONObject) jArray.get(intValue);
                        Log.d("Tag", jObject+" Cache Json");



                        iDService = jObject.getString("idService");
                        name = jObject.getString("nameService");
                        jdescription = jObject.getString("priceService");
                        description = jObject.getString("description");
                        jimage = jObject.getString("photo");

                        typeService.setText(name);
                        priceService.setText(jdescription+"€");
                        descriptionService.setText(description);
                        //imageService.setText(jimage);
                        getJsonImage(iDService);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        rq.add(jsonObjectRequest);

    }

    private void getJsonImage(String iDService) {
        String url2 = ip.stIp() + "/service/img/" + iDService;
        Glide.with(this).load(url2).into(iService);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent= new Intent();
                intent.putExtra("param", "value");
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2404) {
            if(data != null) {
                String value = data.getStringExtra("param");
            }
        }
    }
}

