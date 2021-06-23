package com.sda.dialadoc.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sda.dialadoc.Models.Message;
import com.sda.dialadoc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Touseef Rao on 8/15/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    //declare Arraylist and variables
    private ArrayList<Message> mMessageList=new ArrayList<>();
    private Context mContext;
    private String currentUser;
    private String senderMessage;


    //make Adapter
    public MessageAdapter(Context mContext, ArrayList<Message> mMessageList, String currentUser, String senderMessage) {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
        this.currentUser=currentUser;
        this.senderMessage=senderMessage;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {


        if(mMessageList.isEmpty()) {
            //for testing
            Log.d("customadapterempty", "ok" );
        }
        else{
            Message temp = mMessageList.get(position);
            String from_user=temp.getFrom();

            Log.d("Currentuser", currentUser );
            Log.d("formuser", from_user );
            if(from_user.equals(currentUser))
            {
                if(temp.getUrl().equals(""))
                {
                    holder.messageText.setVisibility(View.GONE);
                    holder.senderMessageView.setText(temp.getMessage());
                    holder.profileImg.setVisibility(View.GONE);
                    holder.sendImageView.setVisibility(View.GONE);

                    Log.d("sendmessages", "ok");
                }else {

                    holder.profileImg.setVisibility(View.GONE);
                    holder.messageText.setVisibility(View.GONE);
                    holder.senderMessageView.setVisibility(View.GONE);
                    Picasso.get().load(temp.getUrl()).into(holder.sendImageView);
                    holder.sendImageView.setVisibility(View.VISIBLE);

                }

                 /*holder.messageText.setBackgroundResource(R.drawable.sender_message_shape);
                 holder.messageText.setTextColor(Color.BLACK);*/

            }
            else
            {
                if(temp.getUrl().equals("")) {
                    holder.senderMessageView.setVisibility(View.GONE);
                    holder.receiveImgView.setVisibility(View.GONE);
                    holder.messageText.setText(temp.getMessage());
                    Log.d("recievemessages", "ok");
              /* holder.messageText.setBackgroundResource(R.drawable.message_shape);
               holder.messageText.setTextColor(Color.WHITE);*/
                }else {
                    holder.senderMessageView.setVisibility(View.GONE);
                    holder.messageText.setVisibility(View.GONE);
                    Picasso.get().load(temp.getUrl()).into(holder.receiveImgView);
                    holder.receiveImgView.setVisibility(View.VISIBLE);
                }
            }
            Log.d("customadapterm", temp.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
    //inherit class
    public class ViewHolder extends RecyclerView.ViewHolder {
        //declares variables
        TextView messageText;
        TextView senderMessageView;
        CircleImageView profileImg;
        RelativeLayout relativeLayout;
        ImageView sendImageView,receiveImgView;


        //  RelativeLayout list_item;
        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_list_view);
            messageText=(TextView)itemView.findViewById(R.id.single_message);
            profileImg=(CircleImageView)itemView.findViewById(R.id.message_profile_picture);
            senderMessageView=(TextView)itemView.findViewById(R.id.sender_single_message);
            sendImageView=(ImageView)itemView.findViewById(R.id.sender_image);
            receiveImgView=(ImageView)itemView.findViewById(R.id.receive_image);


        }
    }
}
