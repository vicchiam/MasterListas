package org.vchisvert.masterlistas;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import tyrantgit.explosionfield.ExplosionField;

public class DetalleListaActivity extends AppCompatActivity {

    ImageView imageViewApple;
    ExplosionField explosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lista);

        int numeroLista = (int) getIntent().getExtras().getSerializable( "numeroLista");
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textWhite));
        toolbar.setTitle("");

        ImageView imageView = (ImageView) findViewById(R.id.imagen);
        if (numeroLista == 0) {
            toolbar.setTitle(R.string.lista_trabajo);
            imageView.setImageResource(R.drawable.trabajo);
        } else {
            toolbar.setTitle(R.string.lista_personal);
            imageView.setImageResource(R.drawable.casa);
        }

        this.imageViewApple=(ImageView) findViewById(R.id.img_apple);

        this.explosionField = ExplosionField.attach2Window(this);

        FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                explosion();
            }
        });

    }

    private void explosion(){
        explosionField.explode(imageViewApple);
    }
}
