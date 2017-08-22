package com.ritcheydevs.javarians;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Declarations and initializations
    ListView list;
    LinearLayout mainLayout;
    public static final String EXTRA_MSG = "com.ritcheydevs.javarians.MSG";
    RelativeLayout relLayout;
    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> imgURLs = new ArrayList<>();
    ImageListAdapter adapter;
    TextView errorText;
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting an instance of an adapter
        adapter = new ImageListAdapter(this);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);  //Filling the listview with the adapter
        //setting onItemClickListener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedUser = adapter.userList.get(position);
                Intent intent = new Intent(MainActivity.this, profileDetails.class);
                intent.putExtra(EXTRA_MSG, clickedUser);
                startActivity(intent);

            }
        });
        //Setting the toolbar...(compatibility)
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Javarians");

        //Setting a textview to tell when an error occurs
        errorText = new TextView(MainActivity.this);
        errorText.setTextSize(20);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/trebuc.ttf");
        errorText.setTypeface(typeface);
        errorText.setVisibility(View.INVISIBLE);
        errorText.setGravity(Gravity.CENTER_HORIZONTAL);
        errorText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        errorText.setText(R.string.errorText);
        relLayout = (RelativeLayout) findViewById(R.id.rel_activity_main);
        relLayout.addView(errorText);

        LinearLayout progressWrapper = (LinearLayout) findViewById(R.id.progrsWrapper);

        //setting elevations, considering compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolBar.setElevation(5f);
            progressWrapper.setElevation(10f);
        }

        //The main request
        runMainRequest("https://api.github.com/search/users?q=location:>\"lagos\"+language:java");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
                //Hiding the Error Textview
                errorText.setVisibility(View.INVISIBLE);
                //refreshing the Listview
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(null);
                adapter.imgList.clear();
                adapter.userList.clear();
                adapter = new ImageListAdapter(this);

                //Showing the Progressbar
                LinearLayout progressBarWrapper = (LinearLayout) findViewById(R.id.progrsWrapper);
                progressBarWrapper.setVisibility(View.VISIBLE);

                //filling the adapter
                list.setAdapter(adapter);

                //Running the MainRequest again
                runMainRequest("https://api.github.com/search/users?q=location:>\"lagos\"+language:java");
                break;
            case R.id.abt:
                //Custom AlertDialog for About App
                AlertDialog.Builder abtDialog = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_about, null);
                abtDialog.setView(mView);
                AlertDialog abt_Dialog = abtDialog.create();
                abt_Dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void runMainRequest(String apiURL) {
        ConnectionHelper con = ConnectionHelper.getInstance(this.getApplicationContext());

        // Requesting a JSON response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject user = items.getJSONObject(i);
                        String user_name = user.getString("login");
                        Log.d("RESPonse", user_name);
                        String avatar_url = user.getString("avatar_url");
                        Log.d("RESPonse", avatar_url);
                        usernames.add(user_name);
                        imgURLs.add(avatar_url);
                        adapter.clear();
                        adapter.userList = usernames;
                        adapter.imgList = imgURLs;
                        adapter.addAll(usernames);
                        adapter.notifyDataSetChanged();
                        Log.d("DATA", user_name + avatar_url);

                        //Hiding the ProgressBar after the data has loaded successful.
                        LinearLayout progressWrapper = (LinearLayout) findViewById(R.id.progrsWrapper);
                        progressWrapper.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    Log.e("PARSING", "Invalid JSON");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("NETWORK", "Something went wrong");
                mainLayout = (LinearLayout) findViewById(R.id.activity_main);
                Snackbar.make(mainLayout, "Check Your Internet Connection...", Snackbar.LENGTH_LONG).show();

                list = (ListView) findViewById(R.id.list);
                list.setAdapter(null);
                errorText.setVisibility(View.VISIBLE);

                LinearLayout progressBarWrapper = (LinearLayout) findViewById(R.id.progrsWrapper);
                progressBarWrapper.setVisibility(View.INVISIBLE);
            }
        });

        con.addToRequestQueue(jsonRequest);

    }

}
