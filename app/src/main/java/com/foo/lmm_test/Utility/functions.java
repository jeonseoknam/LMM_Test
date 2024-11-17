package com.foo.lmm_test.Utility;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class functions {

    public static void updateUserData(FirebaseAuth mAuth, FirebaseFirestore db){
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("userInfo").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("logchk", "Document exists");
                        userData.userName = (String) document.getData().get("Name");
                        userData.userSchool = (String) document.getData().get("School");
                        userData.userNickname = (String) document.getData().get("Nickname");
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
