package com.youseforex.support_and_restincance;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinsHistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private List<CoinHistory> itemsList;
    private CoinsHistoryActivity.CoinsHistoryActivityAdapter mAdapter;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TinyDB tinyDB;
    private Intent intent ;
    private SwipeRefreshLayout pullToRefresh;
    private final int limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coins_history_layout);

        StartAppSDK.init(this, "207042135", false);

        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC);
        startAppAd.showAd(); // show the ad


      // StartAppAd.showAd(this);

        intent = getIntent();

        initToolbar();

        initSwipeRefresh();

        tinyDB = new TinyDB(this);

        initRecyclerView();

        initDrawer();

        for (String coin : tinyDB.getListString("main_notify")){
            if (coin.contains(intent.getStringExtra("coin"))){
                ArrayList<String> list = tinyDB.getListString("main_notify");
                list.remove(coin);
                tinyDB.putListString("main_notify" , list);
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                StatusBarNotification[] notifications = new StatusBarNotification[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notifications = notificationManager.getActiveNotifications();
                    for (StatusBarNotification notification : notifications) {
                            if (notification.getId() == Integer.parseInt(coin.substring(6))) {
                                notificationManager.cancel(Integer.parseInt(coin.substring(6)));
                                break;

                        }
                    }
                }

                else {
                    notificationManager.cancel(Integer.parseInt(coin.substring(6)));
                }
            }
        }


    }


    private void initSwipeRefresh(){

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                fillList();
                pullToRefresh.setRefreshing(false);
            }
        });
    }


    private void initRecyclerView(){

        recyclerView = findViewById(R.id.recyclerview);
        itemsList = new ArrayList<>();
        mAdapter = new CoinsHistoryActivity.CoinsHistoryActivityAdapter(CoinsHistoryActivity.this, itemsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CoinsHistoryActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fillList();
    }


    private void initToolbar(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(intent.getStringExtra("coin"));
    }


    private void initDrawer(){

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void fillList(){

        itemsList.clear();
        mAdapter.notifyDataSetChanged();



        StringRequest strre = new StringRequest(Request.Method.GET,
                SingletonRequestQueue.getInstance(CoinsHistoryActivity.this).getUrl() + "doupdate.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("")) {

                            recyclerView.setVisibility(View.GONE);

                            Toast.makeText(CoinsHistoryActivity.this, "Application is stopped , please update it", Toast.LENGTH_LONG).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });


        SingletonRequestQueue.getInstance(CoinsHistoryActivity.this).getRequestQueue().add(strre);



        JsonArrayRequest strreq = new JsonArrayRequest(Request.Method.GET,
                SingletonRequestQueue.getInstance(CoinsHistoryActivity.this).getUrl() + "api.php?s=" +
                        intent.getStringExtra("coin"),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //    baseActivity.showProgress(R.string.loading);
                        for (int a = 0 ; a < response.length() ; a++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(a);
                                    CoinHistory coinHistory = new CoinHistory();
                                    coinHistory.setIndex(jsonObject.getString("index"));
                                    coinHistory.setD1h4(jsonObject.getString("d1h4"));
                                    coinHistory.setDate(jsonObject.getString("date"));
                                    coinHistory.setPrice(jsonObject.getString("price"));
                                    coinHistory.setSr(jsonObject.getString("sr"));
                                    coinHistory.setSymbol(jsonObject.getString("symbol"));
                                    coinHistory.setTime(jsonObject.getString("time"));
                                    itemsList.add(coinHistory);

                            } catch (JSONException e) {
                                Toast.makeText(CoinsHistoryActivity.this, getResources().getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Toast.makeText(CoinsHistoryActivity.this, getResources().getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        SingletonRequestQueue.getInstance(CoinsHistoryActivity.this).getRequestQueue().add(strreq);

    }


    public class CoinsHistoryActivityAdapter extends RecyclerView.Adapter<CoinsHistoryActivity.CoinsHistoryActivityAdapter.MyViewHolder> {
        private Context context;
        private List<CoinHistory> subjectList;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            Context context;
            private TextView symbol, d1h4, sr, price, date, time;
            private ImageView not;


            public MyViewHolder(View view) {
                super(view);
                symbol = view.findViewById(R.id.symbol);
                d1h4 = view.findViewById(R.id.d1h4);
                sr = view.findViewById(R.id.sr);
                price = view.findViewById(R.id.price);
                date = view.findViewById(R.id.date);
                time = view.findViewById(R.id.time);
             //   not = view.findViewById(R.id.notification);
                context = itemView.getContext();


            }
        }

        public CoinsHistoryActivityAdapter(Context context, List<CoinHistory> subjectList) {
            this.context = context;
            this.subjectList = subjectList;

        }

        @Override
        public CoinsHistoryActivity.CoinsHistoryActivityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coin_history_item, parent, false);

            return new CoinsHistoryActivity.CoinsHistoryActivityAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CoinsHistoryActivity.CoinsHistoryActivityAdapter.MyViewHolder holder, final int position) {

            final CoinHistory coin = subjectList.get(position);

            holder.d1h4.setText(coin.getD1h4());
            holder.date.setText(coin.getDate());
            holder.price.setText(coin.getPrice());

            if (coin.getSr().equals("S") || coin.getSr().equals("s")) {
                holder.sr.setText("Support");
            } else {
                holder.sr.setText("Resistance");
            }
            holder.symbol.setText(coin.getSymbol());
            holder.time.setText(coin.getTime());


        }

        @Override
        public int getItemCount() {
            if (itemsList.size() > limit) {
                return limit;
            } else {
                return itemsList.size();
            }

        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            this.finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(CoinsHistoryActivity.this , MainActivity.class);
            intent.putExtra("fyn" , "no");
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_fav) {

            Intent intent = new Intent(CoinsHistoryActivity.this , MainActivity.class);
            intent.putExtra("fyn" , "yes");
            startActivity(intent);
            finish();

        } else if (id == R.id.contact_us) {

            startActivity(new Intent(CoinsHistoryActivity.this , ContactUs.class));
        }
        else if (id == R.id.supp_res) {

            Intent intent = new Intent(CoinsHistoryActivity.this , ActivityAboutUs.class);
            intent.putExtra("text" , R.string.about_us);
            startActivity(intent);
        }
        else if (id == R.id.trade_on) {

            Intent intent = new Intent(CoinsHistoryActivity.this , ActivityAboutUs.class);
            intent.putExtra("text" , R.string.trade_on);
            startActivity(intent);
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, NotificationService.class));
    }
}

