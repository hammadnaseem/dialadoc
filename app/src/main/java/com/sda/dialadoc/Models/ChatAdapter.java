package com.sda.dialadoc.Models;

/**
 * Created by Touseef Rao on 8/16/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sda.dialadoc.Activities.ChatActivity;
import com.sda.dialadoc.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemHolder>{
    Context context;
    ArrayList<User> usersArrayList;

    public ChatAdapter(Context context, ArrayList<User> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }


    @Override
    public ChatAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_users_layout, null);
        ChatAdapter.ItemHolder itemHolder = new ChatAdapter.ItemHolder(v);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final User friends = usersArrayList.get(position);
        holder.txtName.setText(friends.getName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_id", friends.getUserId());
                intent.putExtra("user_name", friends.getName());
                context.startActivity(intent);
            }
        });


      /*  holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Ok!!",Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {


        public TextView txtName;
        private RelativeLayout relativeLayout;


        public ItemHolder(View itemView) {
            super(itemView);


            txtName = (TextView) itemView.findViewById(R.id.user_single_name);
            relativeLayout=(RelativeLayout)itemView.findViewById(R.id.chat_list_layout);



        }
    }
}
