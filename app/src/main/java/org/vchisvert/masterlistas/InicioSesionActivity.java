package org.vchisvert.masterlistas;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class InicioSesionActivity extends AppCompatActivity {

    private ArrayList bloqueo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);


        Button buttonBloqueo=(Button) findViewById(R.id.boton_facebook);
        buttonBloqueo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                incrementaIndiceBloqueo(view);
            }
        });

        Button buttonANR=(Button) findViewById(R.id.boton_google);
        buttonANR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                incrementaIndiceDeANR(view);
            }
        });

        MobileAds.initialize(this,Params.ID_APLICACION);

    }

    public void loguearCheckbox(View v) {
        CheckBox recordarme= (CheckBox) findViewById(R.id.recordarme);
        String s = getString(R.string.recordar_datos_usuario) + (recordarme.isChecked() ? getString(android.R.string.yes) : getString(android.R.string.no));
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void mostrarContrasenya(View v) {
        EditText contraseña = (EditText) findViewById(R.id.contrasena);
        CheckBox mostrar = (CheckBox) findViewById(R.id.mostrar_contraseña);
        if (mostrar.isChecked()) {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }
        else {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void acceder (View view){
        Intent intent = new Intent(this, ListasActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        else{
            startActivity(intent);
        }
    }

    public void registro (View view){
        EditText usuario = (EditText) findViewById(R.id.usuario);
        Intent intent = new Intent(this, RegistroActivity.class);
        if(usuario.getText().toString().contains("@")){
            intent.putExtra("mail",usuario.getText().toString());
        }
        startActivity(intent);
    }

    public void borrarCampos (View view){
        EditText usuario = (EditText) findViewById(R.id.usuario);
        EditText contraseña = (EditText) findViewById(R.id.contrasena);
        usuario.setText("");
        contraseña.setText("");
        usuario.requestFocus();
    }

    public void incrementaIndiceBloqueo(View v){
        bloqueo.add(null);
    }

    public void incrementaIndiceDeANR(View v){
        try{
            Thread.sleep(15000);;
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
