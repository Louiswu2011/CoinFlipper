package com.wulouis.coinflipper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

public class coinFlip extends AppCompatActivity {



    boolean coin=false;
    int flipscount=0;
    int mscount=0;
    int times=0;
    int upcount=0;
    int downcount=0;
    int p=0;

    int targetcount=0;

    boolean opstatus=true;  //For true is "Start", for false is "Stop"





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sp=getSharedPreferences("values",0);
        final ProgressBar progress=(ProgressBar)findViewById(R.id.progressBar);
        final TextView fliptimes=(TextView)findViewById(R.id.times);
        final TextView uptimes=(TextView)findViewById(R.id.up);
        final TextView downtimes=(TextView)findViewById(R.id.down);
        final TextView probability=(TextView)findViewById(R.id.p);
        final TextView timeused=(TextView)findViewById(R.id.time);
        final Button op=(Button)findViewById(R.id.op);

        targetcount=sp.getInt("fliptimes",10000);

        final Handler flipschange=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                fliptimes.setText(Integer.toString(msg.what)+"/"+Integer.toString(targetcount));
            }
        };
        final Handler upchange=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                uptimes.setText(Integer.toString(msg.what));
            }
        };
        final Handler downchange=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                downtimes.setText(Integer.toString(msg.what));
            }
        };
        final Handler probabilitychange=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                float a=Integer.parseInt((String) uptimes.getText());
                float b=Integer.parseInt((String)downtimes.getText());
                float result=(a/(a+b))*100;
                BigDecimal bd=new BigDecimal(result);
                double c=bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                String display=Double.toString(c);
                probability.setText(display+"%");
            }
        };
        final Handler timechange=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                timeused.setText(Integer.toString(msg.what)+"s");
            }
        };
        final Handler progresschange=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progress.setProgress(msg.what);
            }
        };


        setUI();

        final Timer flip=new Timer();
        final TimerTask fliptask=new TimerTask() {
            @Override
            public void run() {
                Message flipmessage=new Message();
                Message upmessage=new Message();
                Message downmessage=new Message();
                Message timemessage=new Message();
                Message probabilitymessage=new Message();
                Message progressmessage=new Message();
                if(flipscount==targetcount){
                    flip.cancel();
                    setValue();
                    report();
                    //TODO: Here to insert the finishing code.
                }else {
                    flipscount++;
                    flipmessage.what=flipscount;
                    flipschange.sendMessage(flipmessage);
                    coin = CoinOperation.getCoinState();
                    if (coin) {
                        upcount++;
                        upmessage.what=upcount;
                        upchange.sendMessage(upmessage);
                    } else {
                        downcount++;
                        downmessage.what=downcount;
                        downchange.sendMessage(downmessage);
                    }
                    mscount++;
                    if (mscount == 1000) {
                        mscount = 0;
                        times++;
                        timemessage.what=times;
                        timechange.sendMessage(timemessage);
                        //TODO: Fix this method: cpu.setText(CoinOperation.getCPUState());
                        p=CoinOperation.getProbabilityA(upcount,downcount);
                        probabilitymessage.what=p;
                        probabilitychange.sendMessage(probabilitymessage);
                    } else {
                    }
                    progressmessage.what=flipscount;
                    progresschange.sendMessage(progressmessage);
                }
            }
        };

        op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opstatus) {
                    opstatus = false;
                    setUI();
                    flip.schedule(fliptask, 1000, 1);   //TODO here to change the period of timer, default is 1
                } else {
                    opstatus = true;
                    flip.cancel();
                    flip.purge();
                    //TODO here to add activity switch to "REPORT"
                }
            }
        });
    }

    public void setUI(){
        final ProgressBar progress=(ProgressBar)findViewById(R.id.progressBar);
        final TextView fliptimes=(TextView)findViewById(R.id.times);
        final TextView uptimes=(TextView)findViewById(R.id.up);
        final TextView downtimes=(TextView)findViewById(R.id.down);
        final TextView probability=(TextView)findViewById(R.id.p);
        final TextView timeused=(TextView)findViewById(R.id.time);
        final TextView cpu=(TextView)findViewById(R.id.cpu);
        final Button op=(Button)findViewById(R.id.op);
        SharedPreferences sp=getSharedPreferences("values",0);
        progress.setMax(sp.getInt("fliptimes",10000)); // TODO: Here to add SharedPreferences to load the default times, for default is 10000 times
        progress.setProgress(0);
        fliptimes.setText("0/0");
        uptimes.setText("0");
        downtimes.setText("0");
        probability.setText("0");
        timeused.setText("0s");
        cpu.setText("0%");
        op.setText("Start");
    }

    public void gotoResult(){
        setValue();
    }

    public void setValue(){
        SharedPreferences sp=getSharedPreferences("values",0);
        SharedPreferences.Editor speditor=sp.edit();
        speditor.putInt("fliptimes",flipscount);
        speditor.putInt("uptimes",upcount);
        speditor.putInt("downtimes", downcount);
        speditor.putString("probability", Integer.toString(p)+"%");
        speditor.putInt("usetime", times);
        speditor.commit();
    }

    public void report(){
        Intent intent=new Intent(this,ReportActivity.class);
        startActivity(intent);
    }

}
