package com.youseforex.support_and_restincance;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private List<String> itemsList;
    private CoinsAdapter mAdapter;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TinyDB tinyDB;
    private ImageView select_all;
    private boolean selected;
    private String first;
    Intent mServiceIntent;
    private NotificationService notificationService;
    private TimerTask timerTask;
    private String TAG = "Timers";
    private int Your_X_SECS = 3;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartAppSDK.init(this, "207042135", false);

        StartAppAd.disableSplash();

        initToolbar();

        tinyDB = new TinyDB(this);

        initRecyclerView();

        initDrawer();

        first = tinyDB.getString("first");

        if (first.equals("no")){
            selected = false;
        }

        else {
            select_all.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_fill_24dp));
            selected = true ;
            tinyDB.putListString("fav" , (ArrayList<String>) fillList());
            tinyDB.putString("first" , "no");
        }


        notificationService = new NotificationService();
        mServiceIntent = new Intent(this, notificationService.getClass());
        if (!isMyServiceRunning(notificationService.getClass())) {
            startService(mServiceIntent);
        }


        if (tinyDB.getString("id").equals("")){
            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            tinyDB.putString("id" , android_id);

        }

        Log.d("iddddddddd", Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));

        startTimer();

        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = new java.util.Date();
            Date date2 = df.parse("28/10/2019");
            long diff = date1.getTime() - date2.getTime();
            Log.e("aqsxcfgb" , String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
        } catch (ParseException e) {
            Log.e("TEST", "Exception", e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

    }


    private void initRecyclerView(){

        recyclerView = findViewById(R.id.recyclerview);
        select_all = findViewById(R.id.select_all);
        itemsList = new ArrayList<>();
        mAdapter = new CoinsAdapter(MainActivity.this, itemsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        if (getIntent().getStringExtra("fyn") != null && getIntent().getStringExtra("fyn").equals("yes")){
            fillFav();

        }
        else {
            fillList();
        }

        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selected){
                    select_all.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_fill_24dp));
                    selected = true ;
                    tinyDB.putListString("fav" , (ArrayList<String>) fillList());
                }
                else {
                    select_all.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_strokek_24dp));
                    selected = false ;
                    itemsList.clear();
                    tinyDB.putListString("fav" , (ArrayList<String>) itemsList);
                }

                initRecyclerView();

            }
        });
    }


    private void initToolbar(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getStringExtra("fyn") != null && getIntent().getStringExtra("fyn").equals("yes")){
            getSupportActionBar().setTitle(getResources().getString(R.string.menu_fav));

        }
        else {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }


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


    private List<String> fillList(){

        itemsList.clear();
        itemsList.add("AUDUSD");
        itemsList.add("EURUSD");
        itemsList.add("GBPUSD");
        itemsList.add("USDCAD");
        itemsList.add("USDJPY");
        itemsList.add("USDCHF");
        itemsList.add("AUDCAD");
        itemsList.add("AUDCHF");
        itemsList.add("AUDJPY");
        itemsList.add("AUDNZD");
        itemsList.add("CADCHF");
        itemsList.add("CHFJPY");
        itemsList.add("EURAUD");
        itemsList.add("EURCHF");
        itemsList.add("EURCAD");
        itemsList.add("EURGBP");
        itemsList.add("EURJPY");
        itemsList.add("EURNZD");
        itemsList.add("GBPAUD");
        itemsList.add("GBPCAD");
        itemsList.add("GBPCHF");
        itemsList.add("GBPNZD");
        itemsList.add("GBPJPY");
        itemsList.add("NZDUSD");
        itemsList.add("NZDJPY");
        itemsList.add("NZDCHF");
        itemsList.add("NZDCAD");
        itemsList.add("CADJPY");
        itemsList.add("XAUUSD");
        itemsList.add("XTIUSD");

        Collections.sort(itemsList);

        mAdapter.notifyDataSetChanged();

        return itemsList;
    }


    private void fillFav(){

        itemsList.clear();
        if (tinyDB.getListString("fav")!=null)
        itemsList.addAll(tinyDB.getListString("fav"));

        Collections.sort(itemsList);

        mAdapter.notifyDataSetChanged();
    }


    final Handler handler = new Handler();


    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 3000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    private void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        initRecyclerView();
                    }
                });
            }
        };
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.MyViewHolder> {
        private Context context;
        private List<String> subjectList;
        private ArrayList<String> favList;



        public class MyViewHolder extends RecyclerView.ViewHolder {
            Context context;
            private TextView title ;
            private ImageView fav ;
            private ImageView not;


            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                fav = view.findViewById(R.id.fav);
                context = itemView.getContext();
                not = view.findViewById(R.id.notification);


            }
        }

        public CoinsAdapter(Context context, List<String> subjectList) {
            this.context = context;
            this.subjectList = subjectList;

            if (tinyDB.getListString("fav") == null){
                favList = new ArrayList<>();
                tinyDB.putListString("fav" , favList);
            }
            else {
                favList = tinyDB.getListString("fav");
            }

        }

        @Override
        public CoinsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coin_item, parent, false);

            return new CoinsAdapter.MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(final CoinsAdapter.MyViewHolder holder, final int position) {

            final String coin = subjectList.get(position);

            holder.title.setText(coin);

            if (checkFav(coin)){
                holder.fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_fill_24dp));
            }
            else {
                holder.fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_strokek_24dp));
            }

            holder.fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (checkFav(coin)){
                        holder.fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_strokek_24dp));
                        if (selected){
                            selected = false ;
                            itemsList.clear();
                            tinyDB.putListString("fav" , (ArrayList<String>) itemsList);
                            initRecyclerView();
                        }
                        else {
                            favList.remove(coin);
                            tinyDB.putListString("fav" , favList);
                        }

                    }
                    else {
                        holder.fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_fill_24dp));
                        favList.add(coin);
                        tinyDB.putListString("fav" , favList);
                    }
                }
            });


            for (String c : tinyDB.getListString("main_notify")){

                if (c.contains(coin)){

                    holder.not.setVisibility(View.VISIBLE);
                    break;
                }
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this , CoinsHistoryActivity.class);
                    intent.putExtra("coin" , coin);
                    startActivity(intent);
                }
            });

        }

        private boolean checkFav(String coin){

            boolean fav ;

            if (favList.contains(coin)){
                fav = true;
            }
            else {
                fav = false;
            }

            return fav ;
        }

        @Override
        public int getItemCount() {
            return subjectList.size();
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this , MainActivity.class);
            intent.putExtra("fyn" , "no");
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_fav) {

            Intent intent = new Intent(MainActivity.this , MainActivity.class);
            intent.putExtra("fyn" , "yes");
            startActivity(intent);
            finish();

        } else if (id == R.id.contact_us) {

            startActivity(new Intent(MainActivity.this , ContactUs.class));
        }
        else if (id == R.id.supp_res) {

            Intent intent = new Intent(MainActivity.this , ActivityAboutUs.class);
            intent.putExtra("text" , R.string.about_us);
            startActivity(intent);
        }
        else if (id == R.id.trade_on) {

            Intent intent = new Intent(MainActivity.this , ActivityAboutUs.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
    }
}
