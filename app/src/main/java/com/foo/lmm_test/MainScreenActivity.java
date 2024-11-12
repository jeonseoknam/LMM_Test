package com.foo.lmm_test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.foo.lmm_test.databinding.ActivityMainScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private ActivityMainScreenBinding binding;
    private String name, school, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("userInfo").document(user.getUid());
        inQueryData(docRef);


        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                finish();
            }
        });
        binding.getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "안녕하세요, "+ school + "에 재학 중인 " + nickname + "인 " +name+"님."
                        ,Toast.LENGTH_SHORT).show();
            }
        });
        binding.deleteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("userInfo").document(user.getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("logchk", "계정정보 삭제 완료");
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("logchk", "회원탈퇴 완료");
                                                    Toast.makeText(getApplicationContext(),"회원탈퇴 성공"
                                                    , Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("logchk", "계정정보 삭제 실패", e);
                            }
                        });

            }
        });
    }
    private void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        // Check if there is no current user.
        if (firebaseAuth.getCurrentUser() == null)
            Log.d("logchk", "signOut:success");
        else
            Log.d("logchk", "signOut:failure");
    }

    private void inQueryData(DocumentReference docRef){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("logchk", "Document exists");
                        name = (String) document.getData().get("Name");
                        school = (String) document.getData().get("School");
                        nickname = (String) document.getData().get("Nickname");
                    } else {
                        Log.d("logchk", "No such document");
                    }
                } else {
                    Log.d("logchk", "get failed with ", task.getException());
                }
            }
        });
    }
}