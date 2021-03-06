package ipt.gestrepair;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Obscu on 03/10/2017.
 */

public class Budgets2 extends AppCompatActivity implements View.OnClickListener{


    RequestQueue rq,queue;

    TextView txtRegistration, txtState, txtPrice, txtProcess, txtRepair;
    Button RemoveBudget, ApproveBudget;

    String SRegistration, SState, SPrice, SProcess, SRepair, SIdBudget, dataJ;

    String username, password, iduser;
    Ip ip = new Ip();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);
        rq = Volley.newRequestQueue(this);
        queue = Volley.newRequestQueue(this);

        txtRegistration = (TextView) findViewById(R.id.txt_RegistrationValue);
        txtState = (TextView) findViewById(R.id.txt_StatValue);
        txtPrice = (TextView) findViewById(R.id.txt_PriceValue);
        txtProcess= (TextView) findViewById(R.id.txt_StateValue);
        txtRepair = (TextView) findViewById(R.id.txt_RepairTimeValue);
        RemoveBudget = (Button) findViewById(R.id.btn_RemoveBudget);
        ApproveBudget = (Button) findViewById(R.id.btn_ApproveBudget);

        Intent Intent = getIntent();
        username = Intent.getStringExtra("username");
        password = Intent.getStringExtra("password");
        iduser = Intent.getStringExtra("iduser");
        String url= ip.stIp()+"/budget/"+iduser;

        ApproveBudget.setOnClickListener(this);
        RemoveBudget.setOnClickListener(this);

        sendjsonrequest(url);
    }

    public void sendjsonrequest(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = null;
                try {

                    jsonArray = response.getJSONArray("data");

                    Intent Intent = getIntent();
                    int intValue = Intent.getIntExtra("position", 0);
                    Log.i("TAG", intValue + "");

                    final JSONObject jsonObject = (JSONObject) jsonArray.get(intValue);
                    SRegistration = jsonObject.getString("vehicle");
                    SState = jsonObject.getString("state");
                    SPrice = jsonObject.getString("price");
                    SProcess = jsonObject.getString("processOpen");
                    SRepair = jsonObject.getString("repairTime");
                    SIdBudget = jsonObject.getString("idBudget");

                    DateTime TM = new DateTime();

                    txtRegistration.setText(SRegistration);
                    txtState.setText(SState);
                    txtPrice.setText(SPrice + "€");
                    txtProcess.setText(TM.DateTime(SProcess));
                    txtRepair.setText(SRepair + " dias");
                    Log.i("Tag", "State: "+SState);
                    if(SState.equals("Avaliação")){
                        ApproveBudget.setVisibility(View.VISIBLE);
                        RemoveBudget.setVisibility(View.VISIBLE);
                    }
                    else{
                        ApproveBudget.setVisibility(View.INVISIBLE);
                        RemoveBudget.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //typeService.setText("Ups, ocorreu um erro");

            }
        })
        {@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            String credentials = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Basic " + base64EncodedCredentials);
            return headers;
        }
        };
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ApproveBudget:
                String url = ip.stIp() + "/budget/"+SIdBudget+"/aprove/";
                StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                String[] data = new String[2];
                                data[0] = iduser;
                                data[1] = "3";

                                Intent i = new Intent(Budgets2.this, MainActivity2.class);
                                String[] datas = new String[3];
                                datas[0] = username;
                                datas[1] = password;
                                datas[2] = iduser;
                                Bundle bundle = new Bundle();
                                i.putExtra("username", datas[0]);
                                i.putExtra("password", datas[1]);
                                i.putExtra("iduser", datas[2]);
                                i.putExtras(bundle);

                                Context context = getApplicationContext();
                                CharSequence text = "Orçamento aprovado com sucesso";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                startActivityForResult(i, 2404);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("state", "3");
                        return params;
                    }
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        try {
                            Map<String, String> map = new HashMap<String, String>();
                            String key = "Authorization";
                            String encodedString = Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.NO_WRAP);
                            String value = String.format("Basic %s", encodedString);
                            map.put(key, value);
                            return map;
                        } catch (Exception e) {
                            Log.d("Tag","denied");
                        }

                        return super.getHeaders();
                    }
                };
                queue.add(postRequest);
                break;
            case R.id.btn_RemoveBudget:
                url = ip.stIp() + "/budget/"+SIdBudget+"/aprove/";

                StringRequest postRequest2 = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response

                                String[] data = new String[2];
                                data[0] = iduser;
                                data[1] = "4";

                                Intent i = new Intent(Budgets2.this, MainActivity2.class);
                                String[] datas = new String[3];
                                datas[0] = username;
                                datas[1] = password;
                                datas[2] = iduser;
                                Bundle bundle = new Bundle();
                                i.putExtra("username", datas[0]);
                                i.putExtra("password", datas[1]);
                                i.putExtra("iduser", datas[2]);
                                i.putExtras(bundle);

                                Context context = getApplicationContext();
                                CharSequence text = "Orçamento reprovado com sucesso";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                startActivityForResult(i, 2404);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("state", "4");
                        return params;
                    }
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        try {
                            Map<String, String> map = new HashMap<String, String>();
                            String key = "Authorization";
                            String encodedString = Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.NO_WRAP);
                            String value = String.format("Basic %s", encodedString);
                            map.put(key, value);
                            return map;
                        } catch (Exception e) {
                            Log.d("Tag","denied");
                        }

                        return super.getHeaders();
                    }
                };
                queue.add(postRequest2);
                break;
        }
    }
}


