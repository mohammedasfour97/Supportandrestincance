package com.youseforex.support_and_restincance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private String TAG = "Timers";
    private int Your_X_SECS = 5;
    private String state , timeframe , index , date , price , symbol, time;
    private TinyDB tinyDB;
    private int length;
    private NotificationChannel mChannel;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        tinyDB = new TinyDB(this);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
     //   startTimer();
        super.onDestroy();


    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("BootReceiver");
        broadcastIntent.setClass(this, BootReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
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

                        checkNotification();
                    }
                });
            }
        };
    }

    private void checkNotification(){

        JsonArrayRequest strreq = new JsonArrayRequest(Request.Method.GET,
                SingletonRequestQueue.getInstance(NotificationService.this).getUrl() + "api.php",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (tinyDB.getString("l").equals("") || tinyDB.getString("l").equals("0")){

                          tinyDB.putString("l" , String.valueOf(response.length()));
                        }

                        else if (Integer.parseInt(tinyDB.getString("l")) < response.length()){
                            length = response.length();
                            Log.d("fffffffff", length + "/" + tinyDB.getString("l"));

                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = new java.util.Date();
                            long diff = 0;

                            for (int a = Integer.parseInt(tinyDB.getString("l")) ; a <= response.length(); a++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(response.length()-a);

                                    try {

                                        Date date2 = df.parse(jsonObject.getString("date"));
                                        diff = date1.getTime() - date2.getTime();

                                    } catch (ParseException e) {
                                        Log.e("dfghjkl", "Exception", e);
                                    }

                                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <2){

                                        checkIfNotified(jsonObject);
                                    }


                                } catch (JSONException e) {
                                    Toast.makeText(NotificationService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                            tinyDB.putString("l" , String.valueOf(length));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
            }
        });
        strreq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SingletonRequestQueue.getInstance(NotificationService.this).getRequestQueue().add(strreq);
    }

    private void checkIfNotified(JSONObject jsonObject){

        try {
            state = completedState(jsonObject.getString("sr"));
            timeframe = jsonObject.getString("d1h4");
            index = jsonObject.getString("index");
            date = jsonObject.getString("date");
            price = jsonObject.getString("price");
            symbol = jsonObject.getString("symbol");
            time = jsonObject.getString("time");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (tinyDB.getListString("fav").contains(symbol)) {
            Log.d("contains_symbol", index);


            ArrayList<String> state_list;
            ArrayList<String> timeframe_list;
            ArrayList<String> date_list;
            ArrayList<String> price_list;
            ArrayList<String> symbol_list;
            ArrayList<String> time_list;


            if (tinyDB.getListString("state_list") == null){
                state_list = new ArrayList<>();
                state_list.add(state);
                tinyDB.putListString("state_list" , state_list);
            }
            else {
                state_list = tinyDB.getListString("state_list");
                state_list.add(state);
                tinyDB.putListString("state_list" , state_list);
            }



            if (tinyDB.getListString("timeframe_list") == null){
                timeframe_list = new ArrayList<>();
                timeframe_list.add(timeframe);
                tinyDB.putListString("timeframe_list" , timeframe_list);
            }
            else {
                timeframe_list = tinyDB.getListString("timeframe_list");
                timeframe_list.add(timeframe);
                tinyDB.putListString("timeframe_list" , timeframe_list);
            }



            if (tinyDB.getListString("date_list") == null){
                date_list = new ArrayList<>();
                date_list.add(date);
                tinyDB.putListString("date_list" , date_list);
            }
            else {
                date_list = tinyDB.getListString("date_list");
                date_list.add(date);
                tinyDB.putListString("date_list" , date_list);
            }



            if (tinyDB.getListString("price_list") == null){
                price_list = new ArrayList<>();
                price_list.add(price);
                tinyDB.putListString("price_list" , price_list);
            }
            else {
                price_list = tinyDB.getListString("price_list");
                price_list.add(price);
                tinyDB.putListString("price_list" , price_list);
            }



            if (tinyDB.getListString("symbol_list") == null){
                symbol_list = new ArrayList<>();
                symbol_list.add(symbol);
                tinyDB.putListString("symbol_list" , symbol_list);
            }
            else {
                symbol_list = tinyDB.getListString("symbol_list");
                symbol_list.add(symbol);
                tinyDB.putListString("symbol_list" , symbol_list);
            }



            if (tinyDB.getListString("time_list") == null){
                time_list = new ArrayList<>();
                time_list.add(time);
                tinyDB.putListString("time_list" , time_list);
            }
            else {
                time_list = tinyDB.getListString("time_list");
                time_list.add(time);
                tinyDB.putListString("time_list" , time_list);
            }

            checkIfNotifiedd(state , timeframe , index , date , price ,  symbol , time);
        }
        }


        private void checkIfNotifiedd(final String st , final String tf , final String in , final String dat , final String pr ,
                                      final String symbol , final String ti){

            StringRequest strreq = new StringRequest(Request.Method.GET,
                    SingletonRequestQueue.getInstance(NotificationService.this).getUrl() + "notifayconfirm.php?u="
                            + new TinyDB(NotificationService.this).getString("id") + "&i=" + in,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("No")) {
                                Log.d("noninoni", in + " " + response);
                                makeNotification(st, tf, in, dat, pr, symbol, ti);
                            }

                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    e.printStackTrace();
                }

            });
            strreq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            SingletonRequestQueue.getInstance(NotificationService.this).getRequestQueue().add(strreq);
        }


    private String completedState(String sr){
        String sup_res = "";
        if (sr.equals("S") || sr.equals("s")){
            sup_res = "Support";
        }
        else {
            sup_res = "Resistance";
        }

        return sup_res;
    }


    private void makeNotification(String state , String timrframe , String index , String date , String price , String symbol , String time){

        ArrayList<String> list;

        if (tinyDB.getListString("main_notify") == null){
            list = new ArrayList<>();
            list.add(symbol + index);
            tinyDB.putListString("main_notify" , list);
        }
        else {
            list = tinyDB.getListString("main_notify");
            list.add(symbol + index);
            tinyDB.putListString("main_notify" , list);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground));
        mBuilder.setContentTitle("Support and Resistance");
        mBuilder.setContentText("Symbol : " + symbol + "  ||  " + "Time frame :" + timrframe);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/raw/eventually");
        mBuilder.setSound(alarmSound);

        mBuilder.setVibrate(new long[]{0, 500, 1000});

       /* Intent intent = new Intent(this, CoinsHistoryActivity.class);
        intent.putExtra("coin" , symbol);
        */


        Intent intent = new Intent(this, CoinHistoryItemActivity.class);
        intent.putExtra("state" , state);
        intent.putExtra("timrframe" , timrframe);
        intent.putExtra("date" , date);
        intent.putExtra("price" , price);
        intent.putExtra("symbol" , symbol + index);
        intent.putExtra("time" , time);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);


        mBuilder.setContentIntent(pIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "channel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
           mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            mChannel.setSound(alarmSound , att);
            mChannel.enableVibration(true);
           mNotificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(NotificationService.this)
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                    .setContentTitle("Support and Resistance")
                    .setChannelId(CHANNEL_ID)
                    .setContentText("Symbol : " + symbol + "  ||  " + "Time frame :" + timrframe)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .build();

            mNotificationManager.notify(Integer.parseInt(index), notification);
        }

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(Integer.parseInt(index), mBuilder.build());


// Create a notification and set the notification channel.

        setNotified(index);
    }


    private void setNotified(final String index){

        StringRequest strreq = new StringRequest(Request.Method.GET,
                SingletonRequestQueue.getInstance(NotificationService.this).getUrl() + "notifayupdate.php?u="
                        + new TinyDB(NotificationService.this).getString("id") + "&i=" + index ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });

        SingletonRequestQueue.getInstance(NotificationService.this).getRequestQueue().add(strreq);
        }

}
