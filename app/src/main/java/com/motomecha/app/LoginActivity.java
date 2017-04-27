package com.motomecha.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.motomecha.app.dbhandler.SQLiteHandler;
import com.motomecha.app.dbhandler.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
EditText Emobile_number;
    ImageButton Ilogin_continue;
    String Smobilenumber;
    Context context=LoginActivity.this;
    private SessionManager session;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
Emobile_number=(EditText) findViewById(R.id.mobile_text);
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Emobile_number.setText(mPhoneNumber);
        Ilogin_continue=(ImageButton) findViewById(R.id.login_continue);
        Ilogin_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Smobilenumber=Emobile_number.getText().toString();
                if (Smobilenumber.length()>12)
                {
                    logincheck(Smobilenumber);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_LONG).show();
                }
            }
        });
        CheckEnableGPS();
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());


        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, BasicActivity.class);
            startActivity(intent);
        }
    }
    public  void logincheck(final String smobilenumber)
    {
        StringRequest stringRequest =new StringRequest(Request.Method.POST, GlobalUrlInit.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean exits = jObj.getBoolean("exits");
                    if (!exits) {
                        JSONObject user = jObj.getJSONObject("users");
                        String mobile_number = user.getString("mobile_number");
                        String otp = user.getString("otp");
                        String getpass =user.getString("getpass");
                        Toast.makeText(LoginActivity.this, "Otp has been sent to registered mobile number.!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                        intent.putExtra("mobile_number",mobile_number);
                        intent.putExtra("otp",otp);
                        intent.putExtra("getpass",getpass);
                        startActivity(intent);

                    }
                    else {
                        JSONObject user = jObj.getJSONObject("users");
                        String mobile_number = user.getString("mobile_number");
                        String otp = user.getString("otp");
                        String getpass =user.getString("getpass");
                        String name =user.getString("name");
                        String email   =user.getString("email");
                        String address =user.getString("address");
                        String uid =user.getString("uid");;
                        String kpincode = user.getString("pincode");
                        String klat = user.getString("slat");
                        String klng = user.getString("slng");
                        Toast.makeText(LoginActivity.this, "Otp has been sent to registered mobile number.!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                        intent.putExtra("mobile_number",mobile_number);
                        intent.putExtra("otp",otp);
                        intent.putExtra("getpass",getpass);
                        intent.putExtra("name",name);
                        intent.putExtra("email",email);
                        intent.putExtra("address",address);
                        intent.putExtra("uid",uid);
                        intent.putExtra("pincode",kpincode);
                        intent.putExtra("slat",klat);
                        intent.putExtra("slng",klng);
                        startActivity(intent);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Try again after sometime",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("mobile_number",smobilenumber);
                return params;

            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,"CHECKING");
    }
    private void CheckEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {

        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

}
