package org.vchisvert.masterlistas;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListasActivity extends AppCompatActivity {

    private FlowingDrawer mDrawer;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    private FloatingActionButton fab;

    //AdMob
    private AdView addView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd ad;

    //Compras en App
    private IInAppBillingService serviceBilling;
    private ServiceConnection serviceConnection;
    private final int INAPP_BILLING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        //Inicializar los elementos
        List items = new ArrayList();
        items.add(new Lista(R.drawable.trabajo, "Trabajo", 2));
        items.add(new Lista(R.drawable.casa, "Personal", 3));
        //Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        // Crear un nuevo adaptador
        adapter = new ListaAdapter(this,items); recycler.setAdapter(adapter);

        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(ListasActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View v, int position) {
                        Intent intent = new Intent(ListasActivity.this, DetalleListaActivity.class);
                        intent.putExtra("numeroLista", position);

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(ListasActivity.this,
                                        new Pair<View, String>(v.findViewById(R.id.imagen),getString(R.string.transition_name_img)),
                                        new Pair<View, String>(fab,getString(R.string.transition_name_boton))
                        );
                        ActivityCompat.startActivity(ListasActivity.this, intent, options .toBundle());
                    }
                })
        );

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById( R.id.vNavigation);
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_compartir:
                        compatirTexto("http://play.google.com/store/apps/details?id=" + getPackageName());
                        break;
                    case R.id.nav_compartir_lista:
                        compatirTexto("LISTA DE LA COMPRA: patatas, leche, huevos. ---- "+ "Compartido por: http://play.google.com/store/apps/details?id="+ getPackageName());
                        break;
                    case R.id.nav_compartir_logo:
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                        compatirBitmap(bitmap, "Compartido por: "+ "http://play.google.com/store/apps/details?id="+getPackageName());
                        break;
                    case R.id.nav_compartir_desarrollador:
                        compatirTexto( "https://play.google.com/store/apps/dev?id=7027410910970713274");
                        break;
                    case R.id.nav_1:
                        if (ad.isLoaded()) {
                            ad.show();
                        }
                        break;
                    case R.id.nav_articulo_no_recurrente:
                        comprarProducto();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });

        Transition lista_enter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lista_enter = TransitionInflater.from(this) .inflateTransition(R.transition.transition_lista_enter);
            getWindow().setEnterTransition(lista_enter);
        }

        new RateMyApp(this).app_launched();

        showCrossPromoDialog();

        //AdMod
        addView=(AdView) findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().addTestDevice(Params.ID_DISPOSITIVO_FISICO).build();
        addView.loadAd(adRequest);

        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId(Params.ID_BLOQUE_INTERSTICIAL);
        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(Params.ID_DISPOSITIVO_FISICO).build());

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                interstitialAd.loadAd(new AdRequest.Builder()
                        .addTestDevice(Params.ID_DISPOSITIVO_FISICO).build());
            }
        });

        fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
                else {
                    Toast.makeText(ListasActivity.this, "El Anuncio no esta disponible aun", Toast.LENGTH_LONG).show();
                }
            }
        });

        ad = MobileAds.getRewardedVideoAdInstance(this);
        ad.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Toast.makeText(ListasActivity.this,"Video bonificado cargando",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {}

            @Override
            public void onRewardedVideoStarted() {}

            @Override
            public void onRewardedVideoAdClosed() {
                ad.loadAd(Params.ID_BLOQUE_BONIFICADO, new AdRequest.Builder()
                        .addTestDevice(Params.ID_DISPOSITIVO_FISICO).build());
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(ListasActivity.this, "onRewarded: moneda virtual: " + rewardItem.getType() + " aumento: " + rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {}

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {}

        });

        ad.loadAd(Params.ID_BLOQUE_BONIFICADO, new AdRequest.Builder(). addTestDevice(Params.ID_DISPOSITIVO_FISICO).build());

        serviceConectInAppBilling();

    }

    @Override public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        }
        else {
            super.onBackPressed();
        }
    }

    private void compatirTexto(String texto) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(i, "Selecciona aplicación"));
    }

    private void compatirBitmap(Bitmap bitmap, String texto) {
        // guardamos bitmap en el directorio cache
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream s = new FileOutputStream(cachePath+"/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, s); s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Obtenemos la URI usando el FileProvider
        File path = new File(getCacheDir(), "images");
        File file = new File(path, "image.png");
        Uri uri= FileProvider.getUriForFile(this, "org.vchisvert.masterlistas.fileprovider", file);
        //Compartimos la URI
        if (uri != null) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // temp permission for receiving app to read this file
            i.setDataAndType(uri,getContentResolver().getType(uri));
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.putExtra(Intent.EXTRA_TEXT, texto);
            startActivity(Intent.createChooser(i, "Selecciona aplicación"));
        }
    }

    private void showCrossPromoDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);
        dialog.setContentView(R.layout.dialog_crosspromotion);
        dialog.setCancelable(true);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button boton = (Button) dialog.findViewById(R.id.buttonDescargar);
        boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse( "https://play.google.com/store/apps/details?" + "id=com.mimisoftware.emojicreatoremoticonosemoticones")));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void serviceConectInAppBilling() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceBilling = null;
            }

            @Override public void onServiceConnected(ComponentName name, IBinder service) {
                serviceBilling=IInAppBillingService.Stub.asInterface(service);
            }
        };

        Intent serviceIntent = new Intent( "com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void comprarProducto() {
        if (serviceBilling != null) {
            Bundle buyIntentBundle = null;
            try {
                buyIntentBundle= serviceBilling.getBuyIntent(3,getPackageName(), Params.ID_ARTICULO_NO_RECURRENTE, "inapp", Params.PAY_PASS);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }

            PendingIntent pendingIntent = buyIntentBundle .getParcelable("BUY_INTENT");

            try {
                if(pendingIntent!=null) {
                    startIntentSenderForResult(pendingIntent.getIntentSender(), INAPP_BILLING,new Intent(), 0, 0, 0);
                }
            }
            catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "InApp Billing service not available", Toast.LENGTH_LONG).show();
        }
    }

    public void backToBuy(String token){
        if (serviceBilling != null) {
            try {
                int response = serviceBilling.consumePurchase( 3, getPackageName(), token);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INAPP_BILLING: {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra( "INAPP_DATA_SIGNATURE");
                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");
                        String developerPayload = jo.getString("developerPayload");
                        String purchaseToken = jo.getString("purchaseToken");
                        if (sku.equals(Params.ID_ARTICULO_NO_RECURRENTE)) {
                            Toast.makeText(this,"Compra completada", Toast.LENGTH_LONG).show();
                            backToBuy(purchaseToken);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
