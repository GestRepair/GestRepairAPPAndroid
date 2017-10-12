package ipt.gestrepair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListRepair extends AppCompatActivity {

    RequestQueue rq;
    ListView list;
    int n=1;
    String username, password, iduser;
    TextView title;

    ArrayList<String> Vehicles = new ArrayList<String>();
    Ip ip = new Ip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_repair);
        rq = Volley.newRequestQueue(this);

        Intent Intent = getIntent();
        username = Intent.getStringExtra("username");
        password = Intent.getStringExtra("password");
        iduser = Intent.getStringExtra("iduser");
        String url = ip.stIp() + "/repair/user/"+iduser;
        title = (TextView) findViewById(R.id.txtTitle_LstRepair);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = (JSONArray) response.get("data");
                    String[][] name = new String[data.length()][3];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datas = (JSONObject) data.get(i);
                        name[i][0] = datas.getString("vehicle");
                        name[i][1] = datas.getString("state");
                        name[i][2] = datas.getString("idRepair");
                        Vehicles.add("Reparação Nº "+name[i][2]+" - "+name[i][0]+" - "+name[i][1]);
                    }

                    if(Vehicles.isEmpty()){
                        title.setText("Não possui histórico de reparações associados à sua conta");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListRepair.this, R.layout.activity_list_vehicles_main, Vehicles);
                    final ListView list = (ListView) findViewById(R.id.lst_ListRepair);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ListRepair.this, Repair.class);
                            String[] data = new String[3];
                            data[0] = username;
                            data[1] = password;
                            data[2] = iduser;
                            Bundle bundle = new Bundle();
                            intent.putExtra("username", data[0]);
                            intent.putExtra("password", data[1]);
                            intent.putExtra("iduser", data[2]);
                            intent.putExtra("position", position);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 2404);
                            //startActivity(intent);
                        }
                    });
                    list.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Context context = getApplicationContext();
                CharSequence text = "Não foi possivel ligar à internet";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String credentials = username + ":" + password ;
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
}

