package com.foo.lmm_test.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foo.lmm_test.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class
MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    Context context;
    ArrayList<MessageItem> messageItems;
    final int TYPE_MY = 0;
    final int TYPE_OTHER = 1;

    public MessageAdapter(Context context, ArrayList<MessageItem> messageItems) {
        this.context = context;
        this.messageItems = messageItems;
    }




    @Override
    public int getItemViewType(int position) {
        if(messageItems.get(position).name.equals(userData.userNickname)){
            return TYPE_MY; //맞으면 내꺼
        }else {//틀리면 니꺼
            return TYPE_OTHER;
        }
    }



    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //28.홀더를 만드는데 무엇을 만들지 모르겠어... viewType을 설정해준다. viewType은 내가 정하는것이다.
        View itemview = null;

        //30. viewType에 따라 xml을 inflate하자
        if(viewType==TYPE_MY) itemview = LayoutInflater.from(context).inflate(R.layout.chat1,parent,false);
        else itemview=LayoutInflater.from(context).inflate(R.layout.chat2,parent,false);

        return new VH(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MessageItem item=messageItems.get(position);
        holder.tvName.setText(item.name);
        holder.tvMsg.setText(item.message);
        holder.tvTime.setText(item.time);
        Glide.with(context).load(item.profileUrl).into(holder.civ);
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    class VH extends RecyclerView.ViewHolder{

        CircleImageView civ;
        TextView tvName;
        TextView tvMsg;
        TextView tvTime;
        public VH(@NonNull View itemView) {
            super(itemView);

            civ=itemView.findViewById(R.id.civ);
            tvName=itemView.findViewById(R.id.tv_name);
            tvMsg=itemView.findViewById(R.id.msg);
            tvTime=itemView.findViewById(R.id.tv_time);

        }
    }

}