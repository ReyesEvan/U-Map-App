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
    public Connection con;

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

    private void validateUser(String id, String password){
        if(id.contentEquals("155713") && password.contentEquals("admin")) {
            Intent goToAreaSelector = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToAreaSelector);
        }

        CheckDatabase checkDatabase = new CheckDatabase();
        checkDatabase.execute("");

    }

    public class  CheckDatabase extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        String edificio = "";
        int salon = 0;
        double longitud = 0;
        double latitud = 0;

        @Override
        protected void onPostExecute(String r){
            if (isSuccess){
                Log.d("STATE_TEST", edificio + " " + salon + " " + longitud + " " + latitud);
            }
        }

        @Override
        protected String doInBackground(String...params){
            try {
                con = connectionClass();
                if (con == null){
                    z = "check internet access";
                }
                else {
                    String query = "SELECT * FROM Destinos WHERE CONVERT(VARCHAR, Edificio) = 'IA';" ;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()){
                        edificio = rs.getString("Edificio");
                        salon = rs.getInt("Salon");
                        latitud = rs.getDouble("Latitud");
                        longitud = rs.getDouble("Longitud");
                        z = "Query success full";
                        isSuccess = true;
                        con.close();
                    }
                    else {
                        z = "Invalid Query";
                        isSuccess = false;
                    }
                }
            } catch (SQLException e) {
                Log.d("SQL_ERROR_IN_QUERY", "Error in QUERY");
                e.printStackTrace();
            }

            return z;
        }

    }

    @SuppressLint("NewApi")
    public Connection connectionClass(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionUrl = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionUrl = "jdbc:jtds:sqlserver://u-maps.database.windows.net:1433;DatabaseName=U-Map;user=Umap@u-maps;password=FindDaWa3.;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connection = DriverManager.getConnection(connectionUrl);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("CLASS_NOT_FOUND","Class not found");
        } catch (SQLException e) {
            Log.d("SQL_EXCEPTION","SQL Problem");
            e.printStackTrace();
        }

        return connection;
    }

}
