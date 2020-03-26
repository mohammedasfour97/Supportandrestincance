package com.youseforex.support_and_restincance;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.Timer;

public class CoinHistoryItemActivity extends BaseActivity {

    private TextView state , timeframe , index , date , price , symbol, time;
    private Intent intent ;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_history_item);

        StartAppSDK.init(this, "207042135", false);

        StartAppAd.disableSplash();

        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.VIDEO);
        startAppAd.showAd(); // show the ad

       // StartAppAd.showAd(this);

        symbol = findViewById(R.id.symbol);
        timeframe = findViewById(R.id.d1h4);
        state = findViewById(R.id.sr);
        price = findViewById(R.id.price);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);


        intent = getIntent();

        symbol.setText(intent.getStringExtra("symbol").substring(0, 6));
        Log.d("cftyh", intent.getStringExtra("symbol"));
        timeframe.setText(intent.getStringExtra("timrframe"));
        state.setText(intent.getStringExtra("state"));
        price.setText(intent.getStringExtra("price"));
        date.setText(intent.getStringExtra("date"));
        time.setText(intent.getStringExtra("time"));

        for (String coin : new TinyDB(this).getListString("main_notify")){
            if (coin.equals(intent.getStringExtra("symbol"))){
                ArrayList<String> list = new TinyDB(this).getListString("main_notify");
                list.remove(coin);
            new TinyDB(this).putListString("main_notify" , list);
        }
        }


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));

    }
}
