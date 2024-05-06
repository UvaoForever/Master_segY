package com.example.master_segy.program.work_planning.objectLocationP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.objectLocationP.ObjectLocation;

import java.util.ArrayList;
import java.util.List;

public class ObjectAdapter  extends RecyclerView.Adapter<ObjectAdapter.MyViewHolder > implements Filterable {
        private final OnObjectClickListener onClickListener;

        @Override
        public Filter getFilter() {
                return new Filter() {
                        // метод запускается в фоновом потоке для фильтрации элементов
                        @Override
                        protected FilterResults performFiltering(CharSequence constraint) {
                                String filterPattern = constraint.toString().toLowerCase().trim(); // получаем строку поиска
                                List<ObjectLocation> filteredList = new ArrayList<>();
                                // фильтруем элементы списка
                                for (ObjectLocation myObject : objectLocationList) {
                                        if (myObject.get_titleObject().toLowerCase().contains(filterPattern)) {
                                                filteredList.add(myObject);
                                        }
                                }
                                // создаем объект FilterResults с отфильтрованным списком
                                FilterResults results = new FilterResults();
                                results.values = filteredList;
                                return results;
                        }

                        // метод запускается в UI-потоке после завершения фильтрации
                        @Override
                        protected void publishResults(CharSequence constraint, FilterResults results) {
                                filteredList = (List<ObjectLocation>) results.values; // присваиваем отфильтрованный список
                                notifyDataSetChanged(); // обновляем адаптер
                        }
                };
        }

        public interface OnObjectClickListener {
                void onObjectClick(ObjectLocation objectLocation, int position);
        }
        public ObjectAdapter (Context context, OnObjectClickListener onClickListener) {
                this.onClickListener = onClickListener;
                this.context = context;
        }
        private Context context;
        private List<ObjectLocation> objectLocationList;
        private List<ObjectLocation> filteredList;
        public void setObjectLocationList(List<ObjectLocation> objectLocationList){
                this.objectLocationList=objectLocationList;
                this.filteredList = objectLocationList;
                notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ObjectAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_object_row,parent, false);
                return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ObjectAdapter.MyViewHolder holder, int position) {
                holder.tvTitle.setText(this.filteredList.get(position).get_titleObject());
                holder.tvDate.setText(this.filteredList.get(position).get_datePlaneWork());
                // обработка нажатия
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v)
                        {
                                // вызываем метод слушателя, передавая ему данные
                                onClickListener.onObjectClick(filteredList.get(position), position);
                        }
                });
        }

        @Override
        public int getItemCount() {
                return this.filteredList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
                TextView tvTitle;
                TextView tvDate;
                public MyViewHolder(@NonNull View itemView) {
                        super(itemView);
                        tvTitle= itemView.findViewById(R.id.tvTitleObject);
                        tvDate= itemView.findViewById(R.id.tvDatePlan);
                }
        }

}
