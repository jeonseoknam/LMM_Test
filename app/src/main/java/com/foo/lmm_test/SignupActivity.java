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

import com.foo.lmm_test.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String userName, userSchool, userNickname, userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = binding.editTextUsername.getText().toString();
                userSchool = binding.editTextSchoolName.getText().toString();
                userNickname = binding.editTextNickName.getText().toString();
                userEmail = binding.editTextEmail2.getText().toString();
                userPassword = binding.editTextPassword2.getText().toString();

                HashMap<String, Object> user = new HashMap<>();
                user.put("Name", userName);
                user.put("School", userSchool);
                user.put("Nickname", userNickname);
                user.put("EmailAddress", userEmail);
                user.put("Password", userPassword);


                if (userName != null && !userName.isEmpty() &&
                        userNickname != null && !userNickname.isEmpty() &&
                        userSchool != null && !userSchool.isEmpty() &&
                        userEmail != null && !userEmail.isEmpty() &&
                        userPassword != null && !userPassword.isEmpty() ){
                    signUp(userEmail, userPassword, user);
                } else {
                    Log.d("logchk", "createUserWithEmail:invalid");
                    Toast.makeText(getApplicationContext(), "잘못된 입력",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void signUp(String email, String password, HashMap<String, Object> user) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("logchk", "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "회원가입 성공",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.d("logchk", "createUserWithEmail:failure");
                            Toast.makeText(getApplicationContext(), "회원가입 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}