/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  public void getToastMessage(String string){
    Toast.makeText(MainActivity.this,string,Toast.LENGTH_LONG).show();
  }

  public void reDirectUser(){
    if(ParseUser.getCurrentUser() != null){
      Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
      startActivity(intent);
    }
  }
  public void signUpOrLogin(View view){
    final EditText userName = (EditText) findViewById(R.id.userName);
    final EditText password = (EditText) findViewById(R.id.password);

    ParseUser.logInInBackground(userName.getText().toString(), password.getText().toString(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if(e != null){
          ParseUser parseUser = new ParseUser();
          parseUser.setUsername(userName.getText().toString());
          parseUser.setPassword(password.getText().toString());
          parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
              if(e == null){
                getToastMessage("SignUp-Successfull");
                reDirectUser();
              }
              else {
                getToastMessage(e.getMessage().substring(e.getMessage().indexOf(" ")));
              }
            }
          });
        }
        else{
          getToastMessage("Login-Successfull");
          reDirectUser();
        }
      }
    });


  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    this.setTitle("Twitter-Signup/Login");

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}