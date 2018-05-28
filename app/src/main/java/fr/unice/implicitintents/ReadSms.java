package fr.unice.implicitintents;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import model.ConversationDataBase;
import model.MsgInfo;

public class ReadSms  extends AppCompatActivity {
    ListView listview;
    List<MsgInfo> msgs;
    private ConversationDataBase database;
    CustomListView clv;
    private BroadcastReceiver br = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String m = intent.getExtras().getString("message");
            final String number = intent.getExtras().getString("number");
            DecodeMessage.decode(m, number);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<MsgInfo> list = database.getMsgInfoDAO().findRepositoriesForUser(number);
                    if (list != null) {
                        msgs = list;

                    }
                }});
            //listview.invalidateViews();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(br,new IntentFilter("SMS_RECEIVED_ACTION"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(br);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readsms_activity);

        final String number = getIntent().getExtras().getString("number");


        database = ConversationDataBase.getDatabase(this.getApplication());
        msgs = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<MsgInfo> list = database.getMsgInfoDAO().findRepositoriesForUser(number);
                if(list!=null){
                    msgs.addAll(list);
                }

            }});

        listview = (ListView) findViewById(R.id.listview);

        clv = new CustomListView(this, msgs);
        listview.setAdapter(clv);

        //LayoutInflater inflater = LayoutInflater.from(getContext());
        //LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.msgbox, , false);
    }
}
