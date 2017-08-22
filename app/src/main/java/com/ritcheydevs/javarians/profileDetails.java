package com.ritcheydevs.javarians;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class profileDetails extends AppCompatActivity {

    //Declarations and initializations
    ProgressDialog progressDia;
    String clickedUser= "";
    String clickedUserUrl = "";
    String mainRequest = "";
    private ConnectionHelper proimgConnectionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        //Getting ExtraMsg
        Intent intent = getIntent();
        clickedUser = intent.getStringExtra(MainActivity.EXTRA_MSG);
        clickedUserUrl = "https://github.com/"+clickedUser;

        TextView profileUsername = (TextView) findViewById(R.id.profileUsername);
        profileUsername.setText(clickedUser);

        final TextView profileUrl = (TextView) findViewById(R.id.profileUrl);
        profileUrl.setText(clickedUserUrl);

        profileUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = profileUrl.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //Toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(clickedUser);

        //Creating a ProgressDialog
        progressDia = new ProgressDialog(this);
        progressDia.setMessage("Loading...");
        progressDia.setCanceledOnTouchOutside(false);
        progressDia.show();


        //Setting Fab Action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome developer @"+ clickedUser +", " + clickedUserUrl);
                intent.setType("text/plain");
                startActivity(intent);

            }
        });

        //Running request
        mainRequest = "https://api.github.com/search/users?q=" + clickedUser;
        runMainRequest(mainRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                Toast.makeText(profileDetails.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                progressDia.show();
                runMainRequest(mainRequest);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    void runMainRequest(String apiURL) {
        ConnectionHelper con = ConnectionHelper.getInstance(this.getApplicationContext());

        // Requesting a JSON response from the provided URL.
        JsonObjectRequest newJsonRequest = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray items = response.getJSONArray("items");
                    JSONObject user = items.getJSONObject(0);
                    String avatar_url = user.getString("avatar_url");
                    Log.d("RESPonse", avatar_url);

                    proimgConnectionHelper = ConnectionHelper.getInstance(getApplicationContext());
                    CircularNetworkImageView mImageView = (CircularNetworkImageView) findViewById(R.id.profileDetailsAvatar);
                    mImageView.setImageUrl(avatar_url, proimgConnectionHelper.getImageLoader());

                    progressDia.cancel();

                } catch (JSONException e) {
                    Log.e("PARSING", "Invalid JSON");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("NETWORK", "Something went wrong");
                CoordinatorLayout profileView = (CoordinatorLayout) findViewById(R.id.profileView);
                Snackbar.make(profileView, "Check Your Internet Connection...", Snackbar.LENGTH_LONG).show();
                progressDia.cancel();

            }
        });

        con.addToRequestQueue(newJsonRequest);

    }

}
