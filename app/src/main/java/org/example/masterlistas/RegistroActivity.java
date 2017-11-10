package org.example.masterlistas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        if(getIntent().hasExtra("mail")){
            String mail=getIntent().getStringExtra("mail");
            EditText correo = (EditText) findViewById(R.id.correo);
            correo.setText(mail);
        }

    }

    public void cancelar (View view){
        finish();
    }
}
