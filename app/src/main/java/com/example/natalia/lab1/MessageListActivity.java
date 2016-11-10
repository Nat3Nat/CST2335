package com.example.natalia.lab1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.example.natalia.lab1.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Messages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MessageListActivity extends AppCompatActivity {
    protected static String ACTIVITY_NAME = "MessageListActivity";
    ListView lv;
    EditText et;
    Button bt;
    SQLiteDatabase sqlDb;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter adapter;

    ChatAdapter messageAdapter;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */


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

            LayoutInflater inflater = MessageListActivity.this.getLayoutInflater();

            View result = null;
            if (position % 2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            final String messageText = getItem(position) ;
            message.setText(messageText);
            if(getResources().getConfiguration().orientation==2)
                mTwoPane = true;
            else
                mTwoPane = false;
            result.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Log.i(ACTIVITY_NAME,"OnClickListener: Two Pane Mode");
                        Bundle arguments = new Bundle();
                        arguments.putString(MessageDetailFragment.ARG_ITEM_ID,messageText);
                        MessageDetailFragment fragment = new MessageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.message_detail_container, fragment)
                                .commit();
                    } else {
                        Log.i(ACTIVITY_NAME,"OnClickListener: One Pane Mode");
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, messageText);

                        context.startActivity(intent);
                    }
                }
            });
            return result;

        }

    }

    private boolean mTwoPane;
    @Override
    protected void onDestroy(){
        Log.i(ACTIVITY_NAME, "In onDestroy()");
        sqlDb.close();
        super.onDestroy();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
       /* View recyclerView = findViewById(R.id.message_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);*/

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_window);

        final ChatDatabaseHelper chatDataHelp = new ChatDatabaseHelper(this.getApplicationContext());
        //this.getApplicationContext().deleteDatabase("MyDatabase");
        sqlDb=chatDataHelp.getWritableDatabase();
        Cursor c = sqlDb.rawQuery("select * from " + ChatDatabaseHelper.TABLE_NAME, null);
        int colIndex = c.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE);
        Log.i(ACTIVITY_NAME, "Cursor count =" + c.getCount());
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + c.getString(colIndex) );
            list.add(c.getString(colIndex));
            c.moveToNext();
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
                    Log.i(ACTIVITY_NAME, "Sent text " + result);
                    list.add(result);

                    boolean dbAddResult = chatDataHelp.insertData(result,sqlDb);
                    Log.i(ACTIVITY_NAME, "DB Add Result " + dbAddResult);
                    messageAdapter.notifyDataSetChanged();
                    et.setText("");
                }

            });
            messageAdapter = new ChatAdapter(this);

            lv.setAdapter (messageAdapter);
        }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        MessageDetailFragment fragment = new MessageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.message_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
