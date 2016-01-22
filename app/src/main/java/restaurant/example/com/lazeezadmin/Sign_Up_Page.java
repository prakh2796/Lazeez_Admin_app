package restaurant.example.com.lazeezadmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Prakhar Gupta on 26/12/2015.
 */
public class Sign_Up_Page extends Activity {

    EditText email_id,name,pass,cpass;
    Button signup;
    String email,username,password,cpassword;
    int pstatus = 0;

    private String urlJsonObj = "http://gameworld.pythonanywhere.com/signup";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        email_id = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        cpass = (EditText) findViewById(R.id.cpassword);
        signup = (Button) findViewById(R.id.signup);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assignValues();
                checkPassword();

                if (pstatus == 2) {
                    Toast.makeText(getApplication(), "Password cannot be blank :/", Toast.LENGTH_LONG).show();
                    return;
                } else if (pstatus == 0) {
                    Toast.makeText(getApplication(), "Passwords do not match :/", Toast.LENGTH_LONG).show();
                    return;
                } else if (pstatus == 1) {
                    makeJsonObjectRequest();
                }
            }
        });

    }

    private void assignValues()
    {
        email = email_id.getText().toString();
        username = name.getText().toString();
        password = pass.getText().toString();
        cpassword = cpass.getText().toString();
    }

    private void checkPassword()
    {
        pstatus = 0;
        if (password.equals(""))
        {
            pstatus = 2;
        }
        else if (password != null && cpassword != null)
        {
            if (password.equals(cpassword))
            {
                pstatus = 1;
            }
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void makeJsonObjectRequest()
    {
        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, urlJsonObj, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    if (status.equals("success"))
                    {
                        Toast.makeText(getApplication(),"Account Successfully Created :D",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Sign_Up_Page.this,Login_Page.class);
                        startActivity(intent);
                    }else if (status.equals("error"))
                    {
                        Toast.makeText(getApplication(),"Email or Username already exists :/",Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(getApplication(),"wtf server",Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e)
                {
                    Toast.makeText(getApplication(), "Sign Up Failed :(", Toast.LENGTH_SHORT).show();
                    System.out.println(e);
                }

                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"No internet connection",LENGTH_SHORT).show();
                System.out.println(error);
                hidepDialog();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("email",email);
                params.put("username",username);
                params.put("password",password);
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
