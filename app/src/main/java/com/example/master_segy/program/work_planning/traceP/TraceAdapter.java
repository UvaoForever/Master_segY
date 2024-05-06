package com.example.master_segy.program.work_planning.traceP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.traceP.Trace;

import java.util.List;

public class TraceAdapter extends RecyclerView.Adapter<TraceAdapter.MyViewHolder >{
    private final OnFileClickListener onClickListener;

    public TraceAdapter(Context context, OnFileClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.context = context;
    }

    public interface OnFileClickListener {
        void onFileClick(Trace trace, int position);
    }

    private Context context;
    private List<Trace> traceList;
    public void setTraceList(List<Trace> traceList){
        this.traceList = traceList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TraceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_file_row,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraceAdapter.MyViewHolder holder, int position) {
        holder.tvTitleTrace.setText(this.traceList.get(position).getTitle_trace());
        holder.deleteTrace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onFileClick(traceList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.traceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitleTrace;
        TextView deleteTrace;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleTrace = itemView.findViewById(R.id.tvTitleFile);
            deleteTrace = itemView.findViewById(R.id.tvDeleteFile);
        }
    }

}