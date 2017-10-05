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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetScheduleService extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    RequestQueue rq, rq2, rq3;
    String name;
    ListView list;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    String username, password, iduser, selectedVehicle, selectedService, dateFinal, SelectedHour, idService;
    Button btnDate, btnHour, btnconfirm;
    EditText etxtDate, etxtHour;
    int year, month, day, hour, minutes;
    int yearfinal, monthfinal, dayfinal, hourfinal, minutesfinal;

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";


    ArrayList<String> Vehicles = new ArrayList<String>();
    ArrayList<String> Service = new ArrayList<String>();
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
        btnHour = (Button) findViewById(R.id.btn_Hour);
        btnconfirm = (Button) findViewById(R.id.btn_ConfirmSchedule);
        etxtDate = (EditText) findViewById(R.id.etxtDate);

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

                                String[] data = new String[4];
                                data[0] = selectedService;
                                data[1] = selectedVehicle;
                                data[2] = dateFinal;
                                data[3] = idService;
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Tag", "Service:" + selectedService + "\n Vehicle:" + selectedVehicle + "\n dateFinal: " +dateFinal );
                        parseVolleyError(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("service", selectedService);
                        params.put("vehicle", selectedVehicle);
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
                    String[][] name = new String[data.length()][3];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datas = (JSONObject) data.get(i);
                        name[i][2] = datas.getString("registration");
                        Vehicles.add(name[i][2]);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetScheduleService.this, R.layout.activity_list_vehicles_main, Vehicles);
                    Spinner spinnerVehicles = (Spinner) findViewById(R.id.spn_Vehicle);
                    spinnerVehicles.setAdapter(adapter);
                    selectedVehicle = spinnerVehicles.getSelectedItem().toString();

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
                    String[][] name = new String[data.length()][3];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject datas = (JSONObject) data.get(i);
                        name[i][0] = datas.getString("nameService");
                        name[i][1] = datas.get("");

                        Service.add(name[i][0]);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetScheduleService.this, R.layout.activity_list_vehicles_main, Service);
                    Spinner spinnerService = (Spinner) findViewById(R.id.spn_Service);
                    spinnerService.setAdapter(adapter);
                    selectedService = spinnerService.getSelectedItem().toString();
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

        for (int i = 10; i <= 12; i++) {
            Hours.add(i + "");
        }
        for (int i = 14; i <= 17; i++) {
            Hours.add(i + "");
        }
        //Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetScheduleService.this, R.layout.activity_list_vehicles_main, Hours);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);
        Spinner spinnerHours = (Spinner) findViewById(R.id.spn_hours);
        spinnerHours.setAdapter(adapter);
        dateFinal = etxtDate.getText().toString();
        SelectedHour = spinnerHours.getSelectedItem().toString();
        dateFinal = etxtDate.getText().toString() + " " + SelectedHour + ":00";
        Log.d("Tag", "Data final: " + dateFinal);
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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}


