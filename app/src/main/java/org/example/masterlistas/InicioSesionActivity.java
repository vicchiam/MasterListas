package org.example.masterlistas;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class InicioSesionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
    }

    public void loguearCheckbox(View v) {
        CheckBox recordarme= (CheckBox) findViewById(R.id.recordarme);
        String s = "Recordar datos de usuario: " + (recordarme.isChecked() ? "Sí" : "No");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void mostrarContraseña(View v) {
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
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

}
