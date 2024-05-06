package com.example.master_segy.program.work_planning.reportP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;

import com.example.master_segy.data.reportP.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder >{
    private final OnReportClickListener onClickListener;
    public interface OnReportClickListener {
        void onReportClick(Report report, int position);
    }
    public ReportAdapter(Context context, OnReportClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.context = context;
    }
    private Context context;
    private List<Report> reportList;
    public void setReportList(List<Report> reportList){
        this.reportList = reportList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_report_row,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitleReport.setText(this.reportList.get(position).get_titleReport());
        // обработка нажатия
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onReportClick(reportList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.reportList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitleReport;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleReport = itemView.findViewById(R.id.tvTitleReport);
        }
    }

}