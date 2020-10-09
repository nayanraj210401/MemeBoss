package com.mb.memeboss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class developmentMode extends AppCompatActivity {
        TextView msg;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        msg = findViewById(R.id.textView6);
        DocumentReference documentReference = db.collection("data").document("admindata");
        final String[] msgVal = new String[1];
        final boolean[] tempDev = new boolean[1];

        documentReference
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getString("url"));
                        msgVal[0] = document.getString("msgdev");
                        tempDev[0] = document.getBoolean("development_done");
                        msg.setText(msgVal[0]);
                        if(tempDev[0]){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }

                    } else {
                        Log.d("Data", "No such document");
                    }
                } else {
                    Log.d("Data", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemePreferenceActivity.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_development_mode);
    }
}