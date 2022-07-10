package id.my.dess.cekadaptor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //variable
    TextView voltageh, currenth, powerh, quality, statusadaptor, tavoltase, tacurrent, tapower, tmaxvoltage, tminvoltage;
    EditText edtvoltage, edtpower, edtcurrent;
    LinearLayout qualitybg;
    ImageView dotstatus;
    Button btnedit, btnupdate;
    AlertDialog dialogedit;
    Float minavoltase, maxavoltase;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inisialisasi komponen
        voltageh = findViewById(R.id.voltageh);
        currenth = findViewById(R.id.currenth);
        powerh = findViewById(R.id.powerh);
        tavoltase = findViewById(R.id.tavoltase);
        tacurrent = findViewById(R.id.tacurrent);
        tapower = findViewById(R.id.tapower);
        quality = findViewById(R.id.quality);
        qualitybg = findViewById(R.id.qualitybg);
        dotstatus = findViewById(R.id.dotstatus);
        statusadaptor = findViewById(R.id.statusadaptor);
        btnedit = findViewById(R.id.btnedit);
        tminvoltage = findViewById(R.id.tminvoltage);
        tmaxvoltage = findViewById(R.id.tmaxvoltage);


        //inisialisasi dialog untuk ubah spesifikasi adaptor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.edit_dialog, null);
        edtvoltage = view.findViewById(R.id.edtvoltage);
        edtcurrent = view.findViewById(R.id.edtcurrent);
        edtpower = view.findViewById(R.id.edtpower);
        btnupdate = view.findViewById(R.id.btnupdate);


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
                String avoltase = snapshot.child("avoltase").getValue().toString();
                String acurrent = snapshot.child("acurrent").getValue().toString();
                String apower = snapshot.child("apower").getValue().toString();

                //Atur nilai ke dalam ID textview
                voltageh.setText(voltase);
                currenth.setText(ampere);
                powerh.setText(daya);
                tavoltase.setText(avoltase + " V");
                tacurrent.setText(acurrent + " A");
                tapower.setText(apower + " Watt");
                edtvoltage.setText(avoltase);
                edtcurrent.setText(acurrent);
                edtpower.setText(apower);

                //rumus untuk toleransi tegangan 5%
                minavoltase = Float.parseFloat(avoltase) - ((Float.parseFloat(avoltase) * 5)/100);
                maxavoltase = Float.parseFloat(avoltase) + ((Float.parseFloat(avoltase) * 5)/100);

                //Atur nilai min dan max ke dalam textview
                tminvoltage.setText(minavoltase.toString() + " Volt");
                tmaxvoltage.setText(maxavoltase.toString() + " Volt");

                //Uji nilai sensor untuk menentukan kualitas
                if (Float.parseFloat(voltase) <= 2){
                    quality.setText("HUBUNGKAN ADAPTOR!");
                    quality.setTextColor(Color.RED);
                    dotstatus.setImageResource(R.drawable.offlinedot);
                    statusadaptor.setText("Adaptor tidak terhubung!");
                    qualitybg.setBackgroundResource(R.color.default_color);
                }
                else if (Float.parseFloat(voltase) > 2 && Float.parseFloat(voltase) < minavoltase){
                    quality.setText("UNDER VOLTAGE");
                    dotstatus.setImageResource(R.drawable.onlinedot);
                    statusadaptor.setText("Adaptor terhubung!");
                    quality.setTextColor(Color.parseColor("#FF7A00"));
                    qualitybg.setBackgroundResource(R.color.uv_color);
                }
                else if (Float.parseFloat(voltase) >= minavoltase && Float.parseFloat(voltase) <= maxavoltase){
                    quality.setText("NORMAL");
                    statusadaptor.setText("Adaptor terhubung!");
                    dotstatus.setImageResource(R.drawable.onlinedot);
                    quality.setTextColor(Color.parseColor("#24B400"));
                    qualitybg.setBackgroundResource(R.color.normal_color);
                }
                else {
                    quality.setText("OVER VOLTAGE");
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
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                koneksi.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().child("avoltase").setValue(edtvoltage.getText().toString());
                        snapshot.getRef().child("acurrent").setValue(edtcurrent.getText().toString());
                        snapshot.getRef().child("apower").setValue(edtpower.getText().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dialogedit.dismiss();
            }
        });
        builder.setView(view);
        dialogedit = builder.create();
        dialogedit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Tombol ubah untuk menampilkan dialog
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogedit.show();
            }
        });
    }
}