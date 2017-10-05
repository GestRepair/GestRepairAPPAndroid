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

    String username, password, response, iduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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
        txtMainUsr.setText(username);

        if(response!=null) {
            iduser = response.substring(response.indexOf("idUser") + 2);
            iduser=iduser.substring(5,10);
            iduser=iduser.replaceAll("[^\\.0123456789]","");
        }

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
                Context context = getApplicationContext();
                CharSequence text = data[0]+""+data[1]+""+data[2]+"lhurz";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
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
                i = new Intent(MainActivity2.this, SetScheduleService.class);
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
            case R.id.btnAbout:
                /*Context context = getApplicationContext();
                CharSequence text = "A ser implementado numa versão futura";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
                break;
            case R.id.btnExit:
                i = new Intent(MainActivity2.this, Login.class);
                startActivity(i);
                break;
        }
    }
}