package com.foo.lmm_test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.foo.lmm_test.Utility.MessageAdapter;
import com.foo.lmm_test.Utility.MessageItem;
import com.foo.lmm_test.Utility.userData;
import com.foo.lmm_test.databinding.ChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ChatBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String chatname = "안녕하세요";
    CollectionReference colRef;

    MessageAdapter adapter;

    ArrayList<MessageItem> messageItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        getSupportActionBar().setTitle(chatname);
        getSupportActionBar().setSubtitle("상대이름?");


        adapter = new MessageAdapter(this, messageItems);
        binding.recycler.setAdapter(adapter);

        colRef = db.collection("chatRoom").document("singleChat").collection(chatname);

        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentChange> documentChanges = value.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges){
                    DocumentSnapshot snapshot = documentChange.getDocument();

                    Map<String, Object> msg = snapshot.getData();

                    String name = msg.get("nickName").toString();
                    String message = msg.get("message").toString();
                    String profileUrl = msg.get("profileUrl").toString();
                    String time = msg.get("time").toString();

                    messageItems.add(new MessageItem(name, message, profileUrl, time));

                    adapter.notifyItemInserted(messageItems.size()-1);
                    binding.recycler.scrollToPosition(messageItems.size()-1);
                }

                Toast.makeText(ChatActivity.this, ""+messageItems.size(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = userData.userNickname;
                String message = binding.et.getText().toString();
                String profileUrl = userData.profileURI;

                Calendar calendar = Calendar.getInstance();
                String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                MessageItem item = new MessageItem(nickname,message,profileUrl,time);

                colRef.document("msg" + System.currentTimeMillis()).set(item);
                binding.et.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);// 스머프내놔 getSystemService
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

            }
        });
    }
}