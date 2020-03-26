package com.youseforex.support_and_restincance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

public class NotificationHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private List<CoinHistory> itemsList;
    private NotificationHistoryActivity.NotificationHistoryActivityAdapter mAdapter;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TinyDB tinyDB;
    private SwipeRefreshLayout pullToRefresh;
    private final int limit = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coins_history_layout);

        StartAppSDK.init(this, "207042135", false);

        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.VIDEO);
        startAppAd.showAd(); // show the ad

        tinyDB = new TinyDB(this);
        initSwipeRefresh();
        initToolbar();
        initDrawer();
        initRecyclerView();

        for (String s : tinyDB.getListString("timeframe_list")){
            Log.d("plmnbgj", s);
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
        mAdapter = new NotificationHistoryActivity.NotificationHistoryActivityAdapter(NotificationHistoryActivity.this, itemsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NotificationHistoryActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        fillList();
    }


    private void initToolbar(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.history));
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

        if (tinyDB.getListString("state_list") != null){

            ArrayList<String> state_list = tinyDB.getListString("state_list");
            ArrayList<String> timeframe_list= tinyDB.getListString("timeframe_list");
            ArrayList<String> date_list= tinyDB.getListString("date_list");
            ArrayList<String> price_list= tinyDB.getListString("price_list");
            ArrayList<String> symbol_list= tinyDB.getListString("symbol_list");
            ArrayList<String> time_list= tinyDB.getListString("time_list");

            CoinHistory coinHistory;

            for (int a = state_list.size()-1 ; a >=0 ; a--){

                 coinHistory = new CoinHistory(symbol_list.get(a) , timeframe_list.get(a) ,  state_list.get(a) , price_list.get(a) ,
                         date_list.get(a) , time_list.get(a));

                 itemsList.add(coinHistory);
            }

            mAdapter.notifyDataSetChanged();

        }

    }

    public class NotificationHistoryActivityAdapter extends RecyclerView.Adapter<NotificationHistoryActivity.NotificationHistoryActivityAdapter.MyViewHolder> {
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

        public NotificationHistoryActivityAdapter(Context context, List<CoinHistory> subjectList) {
            this.context = context;
            this.subjectList = subjectList;

        }

        @Override
        public NotificationHistoryActivity.NotificationHistoryActivityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coin_history_item, parent, false);

            return new NotificationHistoryActivity.NotificationHistoryActivityAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final NotificationHistoryActivity.NotificationHistoryActivityAdapter.MyViewHolder holder, final int position) {

            final CoinHistory coin = subjectList.get(position);

            holder.d1h4.setText(coin.getD1h4());
            holder.date.setText(coin.getDate());
            holder.price.setText(coin.getPrice());

            if (coin.getSr().equals("Support") || coin.getSr().equals("support")) {
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
            Intent intent = new Intent(NotificationHistoryActivity.this , MainActivity.class);
            intent.putExtra("fyn" , "no");
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_fav) {

            Intent intent = new Intent(NotificationHistoryActivity.this , MainActivity.class);
            intent.putExtra("fyn" , "yes");
            startActivity(intent);
            finish();

        } else if (id == R.id.contact_us) {

            startActivity(new Intent(NotificationHistoryActivity.this , ContactUs.class));
        }
        else if (id == R.id.supp_res) {

            Intent intent = new Intent(NotificationHistoryActivity.this , ActivityAboutUs.class);
            intent.putExtra("text" , R.string.about_us);
            startActivity(intent);
        }
        else if (id == R.id.trade_on) {

            Intent intent = new Intent(NotificationHistoryActivity.this , ActivityAboutUs.class);
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

