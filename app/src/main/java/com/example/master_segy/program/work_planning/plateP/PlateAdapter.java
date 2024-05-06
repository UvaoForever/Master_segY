package com.example.master_segy.program.work_planning.plateP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.plateP.Plate;


import java.util.List;

public class PlateAdapter extends RecyclerView.Adapter<PlateAdapter.MyViewHolder >{
    private final PlateAdapter.OnPlateClickListener onClickListener;
    public interface OnPlateClickListener {
        void onPlateClick(Plate plate, int position);
    }
    public PlateAdapter (Context context, PlateAdapter.OnPlateClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.context = context;
    }
    private Context context;
    private List<Plate> plateList;
    public void setPlateList(List<Plate> plateList){
        this.plateList = plateList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_plate_row,parent, false);
        return new PlateAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlateAdapter.MyViewHolder holder, int position) {
        holder.tvTitlePlate.setText(this.plateList.get(position).get_titlePlate());

        String description=context.getString(R.string.description) + ": "+ this.plateList.get(position).get_descriptionPlate();
        holder.tvDescriptionPlate.setText(description);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onClickListener.onPlateClick(plateList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.plateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitlePlate;
        TextView tvDescriptionPlate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePlate= itemView.findViewById(R.id.tvTitlePlate);
            tvDescriptionPlate= itemView.findViewById(R.id.tvDescriptionPlate);
        }
    }
}