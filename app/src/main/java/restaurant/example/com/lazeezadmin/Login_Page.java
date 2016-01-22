package restaurant.example.com.lazeezadmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class Login_Page extends Activity {

    EditText email,password;

    private String urlJsonObj = "http://lazeez.pythonanywhere.com/";
    private ProgressDialog pDialog;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = (Button) findViewById(R.id.login);
        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();

        String str = sp.getString("username", "N/A");
        if (!str.equals("N/A"))
        {
            intent = new Intent(Login_Page.this,Orders.class);
            startActivity(intent);
            finish();
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (EditText) findViewById(R.id.email);
                password = (EditText) findViewById(R.id.password);

                ed.putString("email", email.getText().toString());
                ed.putString("password", password.getText().toString());
                ed.commit();

                makeJsonObjectRequest();
            }
        });

        TextView signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Login_Page.this, Sign_Up_Page.class);
                startActivity(intent);
            }
        });
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void makeJsonObjectRequest() {

        showpDialog();

        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();

        StringRequest sr = new StringRequest(Request.Method.POST, urlJsonObj, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
//                    Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();

                    String status = jsonObject.getString("status");
                    String uname = jsonObject.getString("username");

                    if (status.equals("success"))
                    {
                        intent = new Intent(Login_Page.this,Orders.class);

                        ed.putString("username",uname);
                        ed.commit();

                        Toast.makeText(getApplication(), "Login Successfull", Toast.LENGTH_SHORT).show();

                        startActivity(intent);
                        finish();

                    }else if (status.equals("error"))
                    {
                        Toast.makeText(getApplication(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(getApplication(),"wtf server",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getApplication(), "Login Failed :(", Toast.LENGTH_SHORT).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"No internet connection",LENGTH_SHORT).show();
                hidepDialog();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr);
    }
}
