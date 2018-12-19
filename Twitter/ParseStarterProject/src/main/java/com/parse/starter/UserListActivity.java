package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.tweets,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.tweet){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Whats new??? Tweet it!");
            final EditText tweetContent = new EditText(this);
            builder.setView(tweetContent);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ParseObject tweet = new ParseObject("Tweets");
                    tweet.put("username",ParseUser.getCurrentUser().getUsername());
                    tweet.put("tweetContent",tweetContent.getText().toString());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Toast.makeText(UserListActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(UserListActivity.this,"Tweet posted!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else if(item.getItemId() == R.id.feeds){
            Intent intent = new Intent(getApplicationContext(),FeedsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.logout){
            ParseUser.logOut();
            Intent intent = new Intent(UserListActivity.this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        this.setTitle("UserList");

        if(ParseUser.getCurrentUser().get("isFollowing") == null){
            ArrayList<ParseUser> emptyList = new ArrayList<>();
            ParseUser.getCurrentUser().put("isFollowing",emptyList);
        }

        final ListView usersListView = (ListView) findViewById(R.id.userListView);
        usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_checked,users);
        usersListView.setAdapter(arrayAdapter);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(checkedTextView.isChecked()){
                    Log.i("tappedItem","selected");
                    ParseUser.getCurrentUser().add("isFollowing",users.get(position));
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.i("isFollowing",e.getMessage());
                            }
                        }
                    });
                }
                else {
                    Log.i("tappedItem", "unselected");

                    List<String> list = new ArrayList<>();
                    list.clear();
                    list = (List)ParseUser.getCurrentUser().get("isFollowing");
                    if (ParseUser.getCurrentUser().getList("isFollowing").contains(users.get(position))) {
                        list.remove(users.get(position));
                        ParseUser.getCurrentUser().put("isFollowing", list);
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                }
            }
        });

        users.clear();
        ParseQuery query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){

                        for(ParseUser user : objects){
                            users.add(user.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();
                        for(String username:users){
                            if(ParseUser.getCurrentUser().getList("isFollowing").contains(username)){
                                usersListView.setItemChecked(username.indexOf(username),true);
                            }
                        }
                    }
                }
            }
        });

    }
}
