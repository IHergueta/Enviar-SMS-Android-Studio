package com.example.ejerciciosms;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityResultLauncher<String> requestPermissionLauncher;
    ExecutorService executorService;
    private static final String CHANNEL_ID="channel1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Este es el channel 1");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }else{

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
        }


        Button mandar = (Button) findViewById(R.id.mandar);
        mandar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.mandar){

            requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {

                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {

                        Toast.makeText(MainActivity.this,"Red OK", Toast.LENGTH_LONG).show();

                    } else {


                    }

                }
            });
            if (ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.SEND_SMS) ==
                    PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(MainActivity.this,"El permiso ya está en uso", Toast.LENGTH_LONG).show();




            } else {

                requestPermissionLauncher.launch(
                        Manifest.permission.SEND_SMS);
            }




            EditText editText = (EditText) findViewById(R.id.mensaje);

            boolean pass=false;
            String mensaje="";
            String codigos="";



                for(int i=0;i<8;i++){

                    int codigo=(int)Math.floor(Math.random()*10);
                    codigos+=""+codigo;

                }
                pass=false;


                mensaje = "El código de seguridad es: " + codigos;

            Toast.makeText(MainActivity.this,mensaje, Toast.LENGTH_LONG).show();

            //Use SmsManager.
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(editText.getText().toString(), null, mensaje, null, null);



            Intent I = new Intent(Intent.ACTION_SENDTO);

            I.setData(Uri.parse("smsto:"+editText.getText().toString()));
            I.putExtra("sms_body",mensaje);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, I, PendingIntent.FLAG_UPDATE_CURRENT);



            NotificationManagerCompat manager;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this,CHANNEL_ID);
            mBuilder.setContentTitle("Mensaje");
            mBuilder.setColor(ContextCompat.getColor(MainActivity.this, R.color.black));
            mBuilder.setSmallIcon(MainActivity.this.getApplicationInfo().icon);
            mBuilder.setContentText("Haga click para leer el mensaje");
            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(pendingIntent);
            manager = NotificationManagerCompat.from(this);
            manager.notify(0, mBuilder.build());

        }
    }
}
