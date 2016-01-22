package restaurant.example.com.lazeezadmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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
 * Created by Prakhar Gupta on 27/12/2015.
 */
public class Content extends Activity {

    private String urlJsonObj = "http://lazeez.pythonanywhere.com/content";
    private ProgressDialog pDialog;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    Integer value;
    Long temp;
    String id;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        TextView order_id = (TextView) findViewById(R.id.order_id);
        TextView bill = (TextView) findViewById(R.id.bill);
        final TextView phone = (TextView) findViewById(R.id.phone);
        address = (TextView) findViewById(R.id.address);

        Intent intent = getIntent();

        value = intent.getIntExtra("order_id",0);
        order_id.setText("Order ID- "+value);
//        id = String.valueOf(order_id);
        id = "1";
        temp = intent.getLongExtra("phone", 0);
        phone.setText(""+temp);
        value = intent.getIntExtra("bill", 0);
        bill.setText("Bill-"+value);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        listView = (ListView) findViewById(R.id.content);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        makeJsonObjectRequest();

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phno=phone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phno));
                startActivity(callIntent);
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

        StringRequest sr = new StringRequest(Request.Method.POST, urlJsonObj, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    int count = Integer.parseInt(jsonObject.getString("count"));

                    String addr = jsonObject.getString("address");
                    address.setText(addr);

                    JSONArray content = new JSONArray(jsonObject.getString("content"));
                    for (int i = 0; i < count; i++)
                    {
                        FeedItem item = new FeedItem();

                        JSONObject obj2 = new JSONObject(content.get(i).toString());

                        String dish = obj2.getString("dish");
                        item.setDish(dish);

                        int quantity = Integer.parseInt(obj2.getString("quantity"));
                        item.setQuantity(quantity);

                        item.setType(1);

                        feedItems.add(item);
                    }
                    listAdapter.notifyDataSetChanged();
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
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("order_id", id);
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
