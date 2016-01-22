package restaurant.example.com.lazeezadmin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Prakhar Gupta on 26/12/2015.
 */
public class Orders extends AppCompatActivity {

    private String urlJsonObj = "http://lazeez.pythonanywhere.com/order";
    private String urlJsonObj2 = "http://lazeez.pythonanywhere.com/remove";
    private ProgressDialog pDialog;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        listView = (ListView) findViewById(R.id.content_list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        makeJsonObjectRequest();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(Orders.this,Content.class);

                Integer order_id = feedItems.get(position).getOrder_id();
                Integer bill = feedItems.get(position).getBill();
                Long phone = feedItems.get(position).getPhone();

                intent.putExtra("order_id", order_id);
                intent.putExtra("phone",phone);
                intent.putExtra("bill", bill);

                startActivity(intent);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder logout = new AlertDialog.Builder(Orders.this);
                logout.setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getApplication(),"make req",LENGTH_SHORT).show();
                                makeJsonObjectRequest2();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = logout.create();
                alert.setTitle("Remove Order/Order Delivered ?");
                alert.show();
                return true;
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

    private  void reload() {
        finish();
        startActivity(getIntent());
    }

    private void makeJsonObjectRequest() {

        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, urlJsonObj, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    JSONObject jsonObject = new JSONObject(response);

                    int count = Integer.parseInt(jsonObject.getString("count"));

                    if(count == 0)
                    {
                        Toast.makeText(getApplication(),"No new Order",Toast.LENGTH_LONG).show();;
                    }
                    else
                    {

                        JSONArray order = new JSONArray(jsonObject.getString("order"));
                        for (int i = 0; i < count; i++)
                        {
                            FeedItem item = new FeedItem();

                            JSONObject obj2 = new JSONObject(order.get(i).toString());

                            int order_id = Integer.parseInt(obj2.getString("order_id"));
                            item.setOrder_id(order_id);

                            long phone = Long.parseLong(obj2.getString("phone"));
                            item.setPhone(phone);


                            int bill = Integer.parseInt(obj2.getString("bill"));
                            item.setBill(bill);

                            item.setType(0);

                            feedItems.add(item);
                        }
                        listAdapter.notifyDataSetChanged();
                        hidepDialog();
                    }

                }catch (Exception e)
                {
                    Toast.makeText(getApplication(), "Something went wrong. Please try again :/", Toast.LENGTH_SHORT).show();
                    hidepDialog();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"No internet connection",LENGTH_SHORT).show();
                hidepDialog();
                finish();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
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

    private void makeJsonObjectRequest2() {

        showpDialog();

        StringRequest sr = new StringRequest(Request.Method.POST, urlJsonObj2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success"))
                    {
                        Toast.makeText(getApplication(),"Order Removed",Toast.LENGTH_SHORT).show();
                        reload();
                    }else
                    {
                        Toast.makeText(getApplication(),"Order not Removed",Toast.LENGTH_SHORT).show();
                    }
                    hidepDialog();
                }catch (Exception e)
                {
                    Toast.makeText(getApplication(), "Something went wrong. Please try again :/", Toast.LENGTH_SHORT).show();
                    hidepDialog();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"No internet connection",LENGTH_SHORT).show();
                hidepDialog();
                finish();
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("order_id", "3");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder logout = new AlertDialog.Builder(Orders.this);
            logout.setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
                            final SharedPreferences.Editor ed = sp.edit();

                            ed.putString("username", "N/A");
                            ed.commit();
                            intent = new Intent(Orders.this,Login_Page.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = logout.create();
            alert.setTitle("Are you sure?");
            alert.show();
        }
        if (id == R.id.refresh) {
            reload();
        }

        return super.onOptionsItemSelected(item);
    }
}
