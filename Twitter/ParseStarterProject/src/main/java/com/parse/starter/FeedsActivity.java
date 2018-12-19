package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        this.setTitle("Feeds");

        final ListView feedsListView = (ListView) findViewById(R.id.feedsListView);


        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tweets");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    List<Map<String,String>> tweetsData = new ArrayList<Map<String,String>>();
                    Map<String,String> tweetInfo = new HashMap<>();
                    if(objects.size() > 0) {
                        for (ParseObject obj : objects) {
                            tweetInfo.put("content",obj.get("tweetContent").toString());
                            tweetInfo.put("username",obj.get("username").toString());
                            tweetsData.add(tweetInfo);
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(FeedsActivity.this,tweetsData,android.R.layout.simple_expandable_list_item_2,new String[]{"content","username"},new int[]{android.R.id.text1,android.R.id.text2});
                        feedsListView.setAdapter(simpleAdapter);
                    }
                }
                else{
                    Log.i("find-Tweets",e.getMessage());
                }
            }
        });

    }
}
