package com.example.charles.u_map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogIn extends AppCompatActivity {

    private EditText idInput;
    private EditText passwordInput;
    private Button signIn;
    private TextView alerts;

    private String idObtained;
    private String passObtained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        idInput =  findViewById(R.id.idInputBox);
        passwordInput = findViewById(R.id.passwordInputBox);
        signIn = findViewById(R.id.signInButton);
        alerts = findViewById(R.id.alertsView);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    confirmInputs();
                }
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean validateUser() {
        String field = idInput.getText().toString().trim();

        if (field.isEmpty()) {
            idInput.setError("Field can not be empty");
            return false;
        } else if (!field.matches("[0-9]+")){
            idInput.setError("Username can only have numbers");
            return false;
        } else if (field.length() < 4) {
            idInput.setError("Username is too short");
            return false;
        } else {
            idInput.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {
        String field = passwordInput.getText().toString().trim();

        if (field.isEmpty()) {
            passwordInput.setError("Field can not be empty");
            return false;
        } else {
            passwordInput.setError(null);
            return true;
        }

    }

    public void confirmInputs() throws SQLException {
        String id = idInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (validateUser() && validatePassword()) {
            getUserOfDatabase(id);
            if ( id.compareTo( idObtained.trim() ) == 0 ) {                      //Si el id esta correcto

                if ( password.compareTo( passObtained.trim() ) == 0 ) {       //Si la contraseña esta correcta
                    alerts.setText("");
                    Intent goToAreaSelector = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(goToAreaSelector);
                }
                else {
                    alerts.setText("Incorrect username/password");
                }
            }
            else {
                alerts.setText("Incorrect username/password");
            }

        }
        else{
            alerts.setText("");
            Toast.makeText(getApplicationContext(),"Check fields",Toast.LENGTH_LONG).show();
        }

    }

    private void getUserOfDatabase(String id) throws SQLException {
        DataBase db = new DataBase();
        ResultSet result;

        result = db.makeQuery("SELECT ID,contraseña FROM Usuarios WHERE CONVERT(VARCHAR,ID) = '" + id + "' ");
        if (result != null) {
            idObtained = result.getString("ID");
            passObtained = result.getString("contraseña");
            db.closeConnection();
        }
        else {
            System.out.print("La query no encontro nada, por lo que result esta vacio");
        }
    }

}