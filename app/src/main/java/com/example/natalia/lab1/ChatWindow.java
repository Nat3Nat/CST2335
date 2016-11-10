package com.example.natalia.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;


import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    protected static String ACTIVITY_NAME = "ChatWindow";
    ListView lv;
    EditText et;
    Button bt;
    SQLiteDatabase sqlDb;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter adapter;

     ChatAdapter messageAdapter;

    public class ChatAdapter extends ArrayAdapter<String>{
        public ChatAdapter(Context ctx) {
            super(ctx,0);
        }

        public int getCount() {

            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();

            View result = null;
            if (position % 2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(   getItem(position)  ); // get the string at position
            return result;

        }

    }


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        final ChatDatabaseHelper chatDataHelp = new ChatDatabaseHelper(this.getApplicationContext());
        //this.getApplicationContext().deleteDatabase("MyDatabase");
        sqlDb=chatDataHelp.getWritableDatabase();
        Cursor c = sqlDb.rawQuery("select * from " + ChatDatabaseHelper.TABLE_NAME, null);
        int colIndex = c.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE);
        Log.i(ACTIVITY_NAME, "Cursor count =" + c.getCount());
        c.moveToFirst(); 
        for(int i = 0; i < c.getCount(); i++) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" );
        }
        int columnCount = c.getColumnCount();
        Log.i(ACTIVITY_NAME, "Cursor's column count =" + columnCount);
        for(int i = 0; i < columnCount; i++)
            Log.i(ACTIVITY_NAME, "Cursor's column " + i + " name = " + c.getColumnName(i));


        lv = (ListView) findViewById(R.id.listView);
        et = (EditText) findViewById(R.id.chatTextView);
        bt = (Button) findViewById(R.id.ChatButton);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = et.getText().toString();
                list.add(result);
                boolean dbAddResult = chatDataHelp.insertData(result,sqlDb);
                Log.i(ACTIVITY_NAME, "DB Add Result" + dbAddResult);
                messageAdapter.notifyDataSetChanged();
                et.setText("");
            }

        });
         messageAdapter = new ChatAdapter(this);

        lv.setAdapter (messageAdapter);
    }


    public void onBtnClick() {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = et.getText().toString();
                list.add(result);

                messageAdapter.notifyDataSetChanged();
                et.setText("");
            }

        });
    }

    protected void onStart(){
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");

    }

    protected void onPause(){
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");

    }

    protected void onStop(){
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    protected void onDestroy(){
        super.onDestroy();
        sqlDb.close();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}


