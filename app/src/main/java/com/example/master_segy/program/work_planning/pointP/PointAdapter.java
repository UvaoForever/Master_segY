package com.example.master_segy.program.work_planning.pointP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.pointP.Point;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.MyViewHolder >{
    private final PointAdapter.OnPointClickListener onClickListener;
    public interface OnPointClickListener {
        void onPointClick(Point point, int position);
    }
    public PointAdapter (Context context, PointAdapter.OnPointClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.context = context;
    }
    private Context context;
    private List<Point> pointList;
    public void setPointList(List<Point> pointList){
        this.pointList = pointList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PointAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_point_row,parent, false);
        return new PointAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointAdapter.MyViewHolder holder, int position) {

        String coordinateX=context.getString(R.string.x) + ": "+ this.pointList.get(position).get_coordinate_X();
        String coordinateY=context.getString(R.string.y) + ": "+ this.pointList.get(position).get_coordinate_Y();
        holder.tvCoordinateX.setText(coordinateX);
        holder.tvCoordinateY.setText(coordinateY);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onClickListener.onPointClick(pointList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.pointList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvCoordinateX;
        TextView tvCoordinateY;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCoordinateX= itemView.findViewById(R.id.tvCoordinateX);
            tvCoordinateY= itemView.findViewById(R.id.tvCoordinateY);
        }
    }
}
