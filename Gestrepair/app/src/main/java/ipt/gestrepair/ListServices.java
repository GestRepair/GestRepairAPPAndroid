package ipt.gestrepair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ListServices extends AppCompatActivity {

    RequestQueue rq;
    String name;
    ListView list;
    FileCacher<ArrayList<String>> CacheListServices = new FileCacher<>(ListServices.this, "offline.txt");
    ArrayList<String> servicedata = new ArrayList<String>();
    Ip ip = new Ip();
    String url = ip.stIp() + "/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_services);
        ListView list = (ListView) findViewById(R.id.lst_Service);

        rq = Volley.newRequestQueue(this);

        if(AppStatus.getInstance(this).isOnline()) {
            sendjsonrequest();
        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "OFFLINE MODE!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            try {
                serviceoffline();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void serviceoffline() throws IOException {
        if (CacheListServices.hasCache()) {
            ArrayList<String> text = CacheListServices.readCache();
            Log.i("Tag", "Cache Read " + text);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListServices.this, R.layout.activity_list_services_main, text);
            final ListView list = (ListView) findViewById(R.id.lst_Service);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListServices.this, Service.class);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 2404);
                    //startActivity(intent);
                }
            });
            list.setAdapter(adapter);
        }
    }



    public void sendjsonrequest() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        name = jsonObject.getString("nameService");
                        servicedata.add(name);
                    }
                    CacheListServices.writeCache(servicedata);
                    Log.i("Tag", "Cache Set: "+CacheListServices+" : "+servicedata);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListServices.this, R.layout.activity_list_services_main, servicedata);
                    final ListView list = (ListView) findViewById(R.id.lst_Service);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ListServices.this, Service.class);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, 2404);
                            //startActivity(intent);

                        }
                    });

                    list.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    serviceoffline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        rq.add(jsonObjectRequest);
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

