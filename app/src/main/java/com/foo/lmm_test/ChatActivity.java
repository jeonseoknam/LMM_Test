package com.foo.lmm_test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foo.lmm_test.Utility.userData;
import com.foo.lmm_test.databinding.ChatBinding;
import com.foo.lmm_test.databinding.MychatBinding;
import com.foo.lmm_test.databinding.OtherchatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String chatname = "안녕하세요";
    private final int MY_CHAT = 1, OTHER_CHAT = 0;
    ChatAdapter adapter;



    ArrayList<MessageItem> messageItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatBinding binding = ChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        adapter = new ChatAdapter(messageItems);
        binding.recycler.setAdapter(adapter);


        final CollectionReference docRef = db.collection("chatRoom").document("singleChat").collection(chatname);
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentChange> documentChanges = value.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges){
                    if (error != null) {
                        Log.w("logchk", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        DocumentSnapshot snapshot = documentChange.getDocument();
                        List<DocumentSnapshot> snapshots = value.getDocuments();
                        Map<String,Object> msg = snapshot.getData();
                        String profileUrl = msg.get("profileUrl").toString();
                        String message = msg.get("message").toString();
                        String name = msg.get("name").toString();
                        String time = msg.get("time").toString();

                        messageItems.add(new MessageItem(name, message, time, profileUrl));
                        adapter.notifyDataSetChanged();
                        adapter.notifyItemInserted(messageItems.size()-1);
                        binding.recycler.scrollToPosition(messageItems.size()-1);
                        Log.d("logchk", "onEvent: " + messageItems.get(messageItems.size()-1).getMessage());


                    } else {
                        Log.d("logchk", "Current data: null");
                    }
                }
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
                MessageItem item = new MessageItem(nickname,message,time, profileUrl);

                docRef.document("msg" + System.currentTimeMillis()).set(item);
                binding.et.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

            }
        });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civ;
        TextView name;
        TextView message;
        TextView time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            civ = itemView.findViewById(R.id.civ);
            name = itemView.findViewById(R.id.tv_name);
            message = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.tv_time);
        }
    }
    private class ChatAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private ArrayList<MessageItem> messageItems;

        private ChatAdapter(ArrayList<MessageItem> messageItems) {
            this.messageItems = messageItems;
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MessageItem item = messageItems.get(position);

                holder.name.setText(item.name);
                holder.message.setText(item.message);
                holder.time.setText(item.time);
                Glide.with(ChatActivity.this).load(item.profileUrl).into(holder.civ);
        }

        @Override
        public int getItemViewType(int position) {
            if (messageItems.get(position).name.equals(userData.userNickname)){
                return MY_CHAT;
            } else { return OTHER_CHAT;}
        }

        @Override
        public int getItemCount() {
            return messageItems.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = null;
            if (viewType == MY_CHAT) {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mychat, parent, false);
            } else {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.otherchat,parent,false);
            }
            return new MyViewHolder(itemView);

        }



    }
}