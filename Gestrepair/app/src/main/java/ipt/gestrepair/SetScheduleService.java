package ipt.gestrepair;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetScheduleService extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    RequestQueue rq, rq2, rq3;
    String [][] name;
    String [][] nameS;
    ListView list;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    String username, password, iduser, selectedVehicle, selectedService, dateFinal, SelectedHour, SidService, SidVehicle;
    Button btnDate, btnHour, btnconfirm;
    TextView description;
    TextView etxtDate, etxtHour, txtDateFinal;
    int year, month, day, hour, minutes;
    int yearfinal, monthfinal, dayfinal, hourfinal, minutesfinal;
    Spinner spinnerService, spinnerVehicles, spinnerHours;

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";


    ArrayList<String> Vehicles = new ArrayList<String>();
    ArrayList<String> idVehicle = new ArrayList<String>();
    ArrayList<String> Service = new ArrayList<String>();
    ArrayList<String> idService = new ArrayList<String>();
    ArrayList<String> Hours = new ArrayList<String>();
    Ip ip = new Ip();
    String url2 = ip.stIp() + "/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_schedule__service);
        rq = Volley.newRequestQueue(this);
        rq2 = Volley.newRequestQueue(this);
        rq3 = Volley.newRequestQueue(this);

        btnDate = (Button) findViewById(R.id.btnDate);
        btnconfirm = (Button) findViewById(R.id.btn_ConfirmSchedule);
        etxtDate = (TextView) findViewById(R.id.etxtDate);
        etxtHour = (TextView) findViewById(R.id.etxtHour);
        spinnerService = (Spinner) findViewById(R.id.spn_Service);
        spinnerVehicles = (Spinner) findViewById(R.id.spn_Vehicle);
        spinnerHours = (Spinner) findViewById(R.id.spn_hours);
        txtDateFinal = (TextView) findViewById(R.id.txtDateFinal);
        description = (TextView) findViewById(R.id.txt_SchSetDescriptions);
        btnconfirm.setEnabled(false);

        spinnerHours.setActivated(false);
        btnconfirm.setActivated(false);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                day++;

                DatePickerDialog datePickerDialog = new DatePickerDialog(SetScheduleService.this, SetScheduleService.this,
                        year, month, day);
                datePickerDialog.getDatePicker().setMinDate((System.currentTimeMillis() - 1000) + 86400000);
                datePickerDialog.show();
            }
        });


        btnconfirm.setOnClickListener(new View.OnClickListener() {
            String url3 = ip.stIp() + "/schedule";

            @Override
            public void onClick(View v) {
                StringRequest postRequest = new StringRequest(Request.Method.POST, url3,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response

                                String[] data = new String[3];
                                data[0] = SidService;
                                data[1] = SidVehicle;
                                data[2] = dateFinal;
                                Log.i("Tag", "Teste: "+SidService+ " - "+SidVehicle+" - "+dateFinal);

                                Intent i = new Intent(SetScheduleService.this, MainActivity2.class);
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
                                CharSequence text = "Agendamento criado com sucesso";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                startActivityForResult(i, 2404);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Tag", "Erro"+SidService+ " - "+SidVehicle+" - "+dateFinal);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        Log.i("Tag", "Erro"+SidService+ " - "+SidVehicle+" - "+dateFinal);
                        params.put("service", SidService);
                        params.put("vehicle", SidVehicle);
                        params.put("date", dateFinal);
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
                            Log.d("Tag", "denied");
                        }
                        return super.getHeaders();
                    }
                };
                rq3.add(postRequest);
            }
        });

        Intent Intent = getIntent();
        username = Intent.getStringExtra("username");
        password = Intent.getStringExtra("password");
        iduser = Intent.getStringExtra("iduser");
        String url = ip.stIp() + "/vehicle/" + iduser + "/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = (JSONArray) response.get("data");
                    name = new String[data.length()][2];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datas = (JSONObject) data.get(i);
                        name[i][0] = datas.getString("registration");
                        name[i][1] = datas.getString("idVehicle");

                        Vehicles.add(name[i][0]);
                        idVehicle.add(name[i][1]);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetScheduleService.this, R.layout.activity_list_vehicles_main, Vehicles);
                    Spinner spinnerVehicles = (Spinner) findViewById(R.id.spn_Vehicle);
                    spinnerVehicles.setAdapter(adapter);

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




        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray data = (JSONArray) response.get("data");
                    nameS = new String[data.length()][3];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datas = (JSONObject) data.get(i);
                        nameS[i][0] = datas.getString("nameService");
                        nameS[i][1] = datas.getString("idService");

                        Service.add(nameS[i][0]);
                        idService.add(nameS[i][1]);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetScheduleService.this, R.layout.activity_list_vehicles_main, Service);
                    spinnerService.setAdapter(adapter);

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
        });

        rq2.add(jsonObjectRequest2);

        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int tempPosition=spinnerService.getSelectedItemPosition();
                Log.d("Tag", "Serviços: "+tempPosition);
                SidService=(nameS[tempPosition][1]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerVehicles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int tempPositionV=spinnerVehicles.getSelectedItemPosition();
                Log.d("Tag", "Serviço Position:"+tempPositionV);
                SidVehicle=(name[tempPositionV][1]);
                Log.d("Tag", "Serviço: "+SidVehicle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedHour = spinnerHours.getSelectedItem().toString();
                etxtHour.setText(" " + SelectedHour + ":00");
                dateFinal = etxtDate.getText().toString()+""+etxtHour.getText().toString();
                txtDateFinal.setText(dateFinal);
                Log.d("Tag", "Data final: " + dateFinal);
                enablebutton();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void enablebutton(){
        if(etxtHour.getText().toString()!=null && etxtDate.getText().toString()!=null)
            btnconfirm.setEnabled(true);
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        yearfinal = year;
        monthfinal = month + 1;
        dayfinal = dayOfMonth;

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minutes = cal.get(Calendar.MINUTE);
        etxtDate.setText(yearfinal + "-" + monthfinal + "-" + dayfinal);

        //Alarm Service testing
           /* AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calAlarm = Calendar.getInstance();
            calAlarm.add(Calendar.SECOND, 5);
            Intent intent = new Intent("");
            PendingIntent.getBroadcast(this, 100, new Intent(""), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager.setExact(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(), );*/
        //***********************

        for (int i = 8; i <= 12; i++) {
            Hours.add(i + "");
        }
        for (int i = 14; i <= 17; i++) {
            Hours.add(i + "");
        }
        //Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetScheduleService.this, R.layout.activity_list_vehicles_main, Hours);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);

        spinnerHours.setAdapter(adapter);
        dateFinal = etxtDate.getText().toString();
        spinnerHours.setActivated(true);
        enablebutton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("param", "value");
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2404) {
            if (data != null) {
                String value = data.getStringExtra("param");
            }
        }
    }
}