package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Riley on 4/13/2016.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        final EditText username = (EditText) findViewById(R.id.ev_username);
        EditText password = (EditText) findViewById(R.id.password);
        Button login = (Button) findViewById(R.id.b_Login);
        TextView register = (TextView) findViewById(R.id.tv_Register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                intent.putExtra("username", username.getEditableText().toString());
                startActivity(intent);
            }
        });

    }




}
