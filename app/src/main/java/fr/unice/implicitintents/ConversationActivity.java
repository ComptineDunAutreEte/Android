package fr.unice.implicitintents;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import model.Conversation;
import model.ConversationDataBase;
import model.MsgInfo;

public class ConversationActivity extends AppCompatActivity {
    private ListView listview;
    public static ConversationDataBase database;

    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);



        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                listview = (ListView) findViewById(R.id.conversationlv);
                database = ConversationDataBase.getDatabase(getApplication());
                List<Conversation> msgs = database.getConDAO().findAllConversations();
                list = new ArrayList<>();
                if(msgs != null){
                    for(Conversation c : msgs){
                        list.add(c.getId());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), R.layout.text_view, R.id.tv, list);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){


                        Intent intent = new Intent(getApplication(),ReadSms.class);
                        intent.putExtra("number", list.get(position));
                        //based on item add info to intent
                        startActivity(intent);
                    }
                });
            }});


    }
}
