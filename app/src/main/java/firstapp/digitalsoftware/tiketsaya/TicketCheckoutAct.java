package firstapp.digitalsoftware.tiketsaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class TicketCheckoutAct extends AppCompatActivity {

    LinearLayout btn_back;
    Button btn_buy_ticket, btn_plus, btn_minus;
    TextView jumlah_ticket, user_balance, texttotal_harga, nama_wisata, ketentuan, lokasi;
    Integer nilai_jumlah_ticket = 1;
    Integer mybalance = 0;
    Integer nilai_total_harga = 0;
    Integer nilai_harga_ticket = 0;
    ImageView notice_uang;
    Integer sisa_balance = 0;

    DatabaseReference reference, reference2, reference3, reference4;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    String date_wisata = "";
    String time_wisata = "";

    Integer nomor_transaksi = new Random().nextInt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_checkout);

        getUsernameLocal();

        //mengambil data dari intent
        Bundle bundle = getIntent().getExtras();
        final String jenis_tiket_baru = bundle.getString("jenis_tiket");

        btn_back = findViewById(R.id.btn_back);
        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        jumlah_ticket = findViewById(R.id.jumlah_ticket);
        user_balance = findViewById(R.id.user_balance);
        texttotal_harga = findViewById(R.id.texttotal_harga);
        notice_uang = findViewById(R.id.notice_uang);

        nama_wisata = findViewById(R.id.nama_wisata);
        ketentuan = findViewById(R.id.ketentuan);
        lokasi = findViewById(R.id.lokasi);

        jumlah_ticket.setText(nilai_jumlah_ticket.toString());

        btn_minus.animate().alpha(0).setDuration(300).start();
        btn_minus.setEnabled(false);
        notice_uang.setVisibility(View.GONE);

        //mengambil data user
        reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mybalance = Integer.valueOf(snapshot.child("user_balance").getValue().toString());
                user_balance.setText("US$ " + mybalance + "" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //mengambil data di wisata firebase berdasar intent
        reference = FirebaseDatabase.getInstance().getReference().child("Wisata").child(jenis_tiket_baru);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //menimpa data lama dgn yg baru
                nama_wisata.setText(snapshot.child("nama_wisata").getValue().toString());
                lokasi.setText(snapshot.child("lokasi").getValue().toString());
                ketentuan.setText(snapshot.child("ketentuan").getValue().toString());
                date_wisata = snapshot.child("date_wisata").getValue().toString();
                time_wisata = snapshot.child("time_wisata").getValue().toString();
                nilai_harga_ticket = Integer.valueOf(snapshot.child("harga_tiket").getValue().toString());

                nilai_total_harga = nilai_harga_ticket * nilai_jumlah_ticket;
                texttotal_harga.setText("US$ " + nilai_harga_ticket + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nilai_jumlah_ticket+=1;
                jumlah_ticket.setText(nilai_jumlah_ticket.toString());
                if (nilai_jumlah_ticket > 1){
                    btn_minus.animate().alpha(1).setDuration(300).start();
                    btn_minus.setEnabled(true);
                }

                nilai_total_harga = nilai_harga_ticket * nilai_jumlah_ticket;
                texttotal_harga.setText("US$ " + nilai_total_harga + "");

                if (nilai_total_harga > mybalance){
                    btn_buy_ticket.animate().alpha(0).translationY(250).setDuration(300).start();
                    btn_buy_ticket.setEnabled(false);
                    user_balance.setTextColor(Color.parseColor("#D1206B"));
                    notice_uang.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nilai_jumlah_ticket-=1;
                jumlah_ticket.setText(nilai_jumlah_ticket.toString());
                if (nilai_jumlah_ticket < 2){
                    btn_minus.animate().alpha(0).setDuration(300).start();
                    btn_minus.setEnabled(false);
                }

                nilai_total_harga = nilai_harga_ticket * nilai_jumlah_ticket;
                texttotal_harga.setText("US$ " + nilai_total_harga + "");

                if (nilai_total_harga < mybalance){
                    btn_buy_ticket.animate().alpha(1).translationY(0).setDuration(300).start();
                    btn_buy_ticket.setEnabled(true);
                    user_balance.setTextColor(Color.parseColor("#203DD1"));
                    notice_uang.setVisibility(View.GONE);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goback = new Intent(TicketCheckoutAct.this, TicketDetailAct.class);
                startActivity(goback);
            }
        });

        btn_buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menyimpan data user ke dalam firebase
                reference3 = FirebaseDatabase.getInstance().getReference().child("MyTickets").child(username_key_new).child(nama_wisata.getText().toString() + nomor_transaksi);
                reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference3.getRef().child("id_ticket").setValue(nama_wisata.getText().toString() + nomor_transaksi);
                        reference3.getRef().child("nama_wisata").setValue(nama_wisata.getText().toString());
                        reference3.getRef().child("lokasi").setValue(lokasi.getText().toString());
                        reference3.getRef().child("ketentuan").setValue(ketentuan.getText().toString());
                        reference3.getRef().child("jumlah_tiket").setValue(nilai_jumlah_ticket.toString());
                        reference3.getRef().child("date_wisata").setValue(date_wisata);
                        reference3.getRef().child("time_wisata").setValue(time_wisata);

                        Intent gotobuysuccess = new Intent(TicketCheckoutAct.this, SuccessBuyTicketAct.class);
                        startActivity(gotobuysuccess);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //update data balance
                reference4 = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
                reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sisa_balance = mybalance - nilai_total_harga;
                        reference4.getRef().child("user_balance").setValue(sisa_balance);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    public void getUsernameLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
