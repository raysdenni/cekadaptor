package id.my.dess.cekadaptor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //variable nilai sensor
    TextView voltageh, currenth, powerh, quality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inisialisasi komponen
        voltageh = findViewById(R.id.voltageh);
        currenth = findViewById(R.id.currenth);
        powerh = findViewById(R.id.powerh);
        quality = findViewById(R.id.quality);


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
                if (Float.parseFloat(voltase) <= 2)
                    quality.setText("Tidak terhubung");
                else if (Float.parseFloat(voltase) > 2 && Float.parseFloat(voltase) < 18.05)
                    quality.setText("Under Voltage");
                else if (Float.parseFloat(voltase) >18.05 && Float.parseFloat(voltase) <19.95)
                    quality.setText("Normal");
                else
                    quality.setText("Over Voltage");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}