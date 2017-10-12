package ipt.gestrepair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

/**
 * Created by Obscu on 03/10/2017.
 */

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

    RequestQueue rq, rq2, rq3;
    Ip ip = new Ip();
    String url = ip.stIp() + "/login";
    FrameLayout FLVehicles, FLSchedules;
    ArrayList<String> Vehicles = new ArrayList<String>();
    ArrayList<String> Schedules = new ArrayList<String>();

    ImageButton Ibt_Services, Ibt_ListVehicles, Ibt_Schedules, Ibt_SetSchedules, Ibt_Repairs, Ibt_Budgets,
            Ibt_About, Ibt_Exit;
    TextView txtMainUsr;
    String urlSchedule;

    String username, password, response, iduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        rq = Volley.newRequestQueue(this);
        Ibt_Services = (ImageButton) findViewById(R.id.btnServices);
        Ibt_ListVehicles = (ImageButton) findViewById(R.id.btnVehicle);
        Ibt_Schedules = (ImageButton) findViewById(R.id.btnSchedule);
        Ibt_SetSchedules = (ImageButton) findViewById(R.id.btnSetSchedule);
        Ibt_Repairs = (ImageButton) findViewById(R.id.btnRepairs);
        Ibt_Budgets = (ImageButton) findViewById(R.id.btnBudget);
        Ibt_About = (ImageButton) findViewById(R.id.btnAbout);
        Ibt_Exit = (ImageButton) findViewById(R.id.btnExit);
        txtMainUsr = (TextView) findViewById(R.id.txtUsername);

        Ibt_Services.setOnClickListener(this);
        Ibt_ListVehicles.setOnClickListener(this);
        Ibt_Schedules.setOnClickListener(this);
        Ibt_SetSchedules.setOnClickListener(this);
        Ibt_Repairs.setOnClickListener(this);
        Ibt_Budgets.setOnClickListener(this);
        Ibt_About.setOnClickListener(this);
        Ibt_Exit.setOnClickListener(this);


        Intent Intent = getIntent();
        username = Intent.getStringExtra("username");
        password = Intent.getStringExtra("password");
        response = Intent.getStringExtra("response");
        iduser = Intent.getStringExtra("iduser");
        txtMainUsr.setText(username);

        if(response!=null) {
            iduser = response.substring(response.indexOf("idUser") + 2);
            iduser=iduser.substring(5,10);
            iduser=iduser.replaceAll("[^\\.0123456789]","");
        }
        urlSchedule = ip.stIp() + "/vehicle/" + iduser + "/user";

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnServices:
                url= ip.stIp()+"/service";
                Intent i = new Intent(MainActivity2.this, ListServices.class);
                startActivity(i);
                break;
            case R.id.btnVehicle:
                i = new Intent(MainActivity2.this, ListVehicles.class);
                String[] data = new String[3];
                data[0] = username;
                data[1] = password;
                data[2] = iduser;
                Bundle bundle = new Bundle();
                i.putExtra("username", data[0]);
                i.putExtra("password", data[1]);
                i.putExtra("iduser", data[2]);
                i.putExtras(bundle);
                startActivityForResult(i, 2404);
                break;
            case R.id.btnSchedule:
                i = new Intent(MainActivity2.this, ListScheduleService.class);
                data = new String[3];
                data[0] = username;
                data[1] = password;
                data[2] = iduser;
                bundle = new Bundle();
                i.putExtra("username", data[0]);
                i.putExtra("password", data[1]);
                i.putExtra("iduser", data[2]);
                i.putExtras(bundle);
                startActivityForResult(i, 2404);
                break;
            case R.id.btnSetSchedule:
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlSchedule, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int tempPosition;
                        try {
                            JSONArray data = (JSONArray) response.get("data");
                            String[][] name = new String[data.length()][3];
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject datas = (JSONObject) data.get(i);
                                name[i][0] = datas.getString("registration");
                                Vehicles.add(name[i][0]);
                            }

                            if(Vehicles.isEmpty()){
                                Context context = getApplicationContext();
                                CharSequence text = "Não é possivel efetuar agendamentos sem uma viatura associada à sua conta\n Por favor contacte uma das oficinas GestRepair para adicionar a sua viatura.";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            else{
                                Intent i = new Intent(MainActivity2.this, SetScheduleService.class);
                                String [] data2= new String[3];
                                data2[0] = username;
                                data2[1] = password;
                                data2[2] = iduser;
                                Bundle bundle = new Bundle();
                                i.putExtra("username", data2[0]);
                                i.putExtra("password", data2[1]);
                                i.putExtra("iduser", data2[2]);
                                i.putExtras(bundle);
                                startActivityForResult(i, 2404);
                            }

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
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String credentials = username + ":" + password;
                        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Basic " + base64EncodedCredentials);
                        return headers;
                    }
                };
                rq.add(jsonObjectRequest);
                break;
            case R.id.btnBudget:
                i = new Intent(MainActivity2.this, ListBudgets.class);
                data = new String[3];
                data[0] = username;
                data[1] = password;
                data[2] = iduser;
                bundle = new Bundle();
                i.putExtra("username", data[0]);
                i.putExtra("password", data[1]);
                i.putExtra("iduser", data[2]);
                i.putExtras(bundle);
                startActivityForResult(i, 2404);
                break;
            case R.id.btnRepairs:
                i = new Intent(MainActivity2.this, ListRepair.class);
                data = new String[3];
                data[0] = username;
                data[1] = password;
                data[2] = iduser;
                bundle = new Bundle();
                i.putExtra("username", data[0]);
                i.putExtra("password", data[1]);
                i.putExtra("iduser", data[2]);
                i.putExtras(bundle);
                startActivityForResult(i, 2404);
                break;
            case R.id.btnAbout:
                Context context = getApplicationContext();
                CharSequence text = "A ser implementado numa versão futura";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
            case R.id.btnExit:
                i = new Intent(MainActivity2.this, Login.class);
                startActivity(i);
                break;
        }
    }
}