package com.example.charles.u_map;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class DataBase extends AsyncTask <String, String, String> {

    private String query = "";
    private String z = "";
    private Boolean isSuccess = false;
    private ResultSet result;
    private Connection databaseLink;


    //Makes a query and retrieves the data

    public ResultSet makeQuery(String query) throws SQLException {
        this.query = query;
        try {
            this.execute("").get();
            return result;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Close the connection to the server

    public void closeConnection() {
        try {
            databaseLink.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handle query

    @Override
    protected String doInBackground(String...params){
        try {
            databaseLink = connectionClass();
            if (databaseLink == null){
                z = "check internet access";
            }
            else {
                Statement stmt = databaseLink.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    result = rs;
                    z = "Query successful";
                    isSuccess = true;
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

    //Fill data of the server and start connection

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
