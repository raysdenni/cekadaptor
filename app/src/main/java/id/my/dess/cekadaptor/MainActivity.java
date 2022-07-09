package id.my.dess.cekadaptor;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //variable nilai sensor
    TextView voltageh, currenth, powerh, quality, statusadaptor;
    LinearLayout qualitybg;
    ImageView dotstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inisialisasi komponen
        voltageh = findViewById(R.id.voltageh);
        currenth = findViewById(R.id.currenth);
        powerh = findViewById(R.id.powerh);
        quality = findViewById(R.id.quality);
        qualitybg = findViewById(R.id.qualitybg);
        dotstatus = findViewById(R.id.dotstatus);
        statusadaptor = findViewById(R.id.statusadaptor);

        //Koneksi database
        DatabaseReference koneksi = FirebaseDatabase.getInstance().getReference();

        //Baca isi database
        koneksi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Tampilkan nilai sensor kedalam aplikasi
                //Baca nilai sensor

                String voltase = snapshot.child("voltase").getValue().toString();
                String ampere = snapshot.child("ampere").getValue().toString();
                String daya = snapshot.child("daya").getValue().toString();

                //Atur nilai ke dalam ID textview
                voltageh.setText(voltase);
                currenth.setText(ampere);
                powerh.setText(daya);

                //Uji nilai sensor untuk menentukan kualitas
                if (Float.parseFloat(voltase) <= 2){
                    quality.setText("HUBUNGKAN ADAPTOR!");
                    quality.setTextColor(Color.RED);
                    dotstatus.setImageResource(R.drawable.offlinedot);
                    statusadaptor.setText("Adaptor tidak terhubung!");
                    qualitybg.setBackgroundResource(R.color.default_color);
                }
                else if (Float.parseFloat(voltase) > 2 && Float.parseFloat(voltase) < 18.05){
                    quality.setText("UNDER VOLTAGE :(");
                    dotstatus.setImageResource(R.drawable.onlinedot);
                    statusadaptor.setText("Adaptor terhubung!");
                    quality.setTextColor(Color.parseColor("#FF7A00"));
                    qualitybg.setBackgroundResource(R.color.uv_color);
                }
                else if (Float.parseFloat(voltase) >18.05 && Float.parseFloat(voltase) <19.95){
                    quality.setText("NORMAL :)");
                    statusadaptor.setText("Adaptor terhubung!");
                    dotstatus.setImageResource(R.drawable.onlinedot);
                    quality.setTextColor(Color.parseColor("#24B400"));
                    qualitybg.setBackgroundResource(R.color.normal_color);
                }
                else {
                    quality.setText("OVER VOLTAGE :@");
                    statusadaptor.setText("Adaptor terhubung!");
                    dotstatus.setImageResource(R.drawable.onlinedot);
                    quality.setTextColor(Color.RED);
                    qualitybg.setBackgroundResource(R.color.ov_color);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}