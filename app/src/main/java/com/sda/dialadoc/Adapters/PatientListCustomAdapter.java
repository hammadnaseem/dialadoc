package com.sda.dialadoc.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sda.dialadoc.Activities.Appointment;
import com.sda.dialadoc.Models.AppointmentModel;
import com.sda.dialadoc.R;

import java.util.ArrayList;
//inherit with the adapter that we can already make

public class PatientListCustomAdapter extends RecyclerView.Adapter<PatientListCustomAdapter.Holder> {
    private Context context;
    private ArrayList<AppointmentModel> appointList;

    public PatientListCustomAdapter(Context context, ArrayList<AppointmentModel> appointList) {
        this.context = context;
        this.appointList = appointList;
    }

    @NonNull
    @Override
    //Hold the values that we want to show on screen
    public PatientListCustomAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.doc_single_list_view_layout,viewGroup,false);
        PatientListCustomAdapter.Holder viewHolder=new PatientListCustomAdapter.Holder(view);
        return viewHolder;
    }

    @Override
    //bind the values that we create in holder function
    public void onBindViewHolder(@NonNull final PatientListCustomAdapter.Holder holder, int i) {
        //create new object
        AppointmentModel appointmentModel=new AppointmentModel();
        appointmentModel=appointList.get(i);

        //getting all the values by using appointment model
        holder.name.setText(appointmentModel.getName());
        holder.date.setText(appointmentModel.getDate());
        holder.time.setText(appointmentModel.getTime());
//handle card view layout
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //changing screen
                Intent intent=new Intent(context, Appointment.class);
                //send data to other screen
                intent.putExtra("appo",appointList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    //return the size of layout that store in database means users
    public int getItemCount() {
        return appointList.size();
    }
    //inheritance
    class Holder extends RecyclerView.ViewHolder {
        //values that using in layout
        TextView name,date,time;
        CardView cardView;


        public Holder(@NonNull View itemView) {
            super(itemView);
            //casting
            name=itemView.findViewById(R.id.user_name);
            date=itemView.findViewById(R.id.user_appointment_date);
            time=itemView.findViewById(R.id.user_appointment_time);
            cardView=itemView.findViewById(R.id.doc_layout);
        }
    }
}
