package com.example.charles.u_map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogIn extends AppCompatActivity {

    private EditText idInput;
    private EditText passwordInput;
    private Button signIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        idInput = (EditText) findViewById(R.id.idInputBox);
        passwordInput = (EditText) findViewById(R.id.passwordInputBox);
        signIn = (Button) findViewById(R.id.signInButton);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser(idInput.getText().toString(), passwordInput.getText().toString());
            }
        });


    }

    private void validateUser(String id, String password) {

       ResultSet result;

        if (id.contentEquals("155713") && password.contentEquals("admin")) {
            Intent goToAreaSelector = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToAreaSelector);
        }

        DataBase db = new DataBase();
        try {
            result = db.makeQuery("SELECT * FROM Destinos WHERE CONVERT(VARCHAR, Edificio) = 'IA';");
            db.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
