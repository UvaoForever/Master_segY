package com.example.master_segy.program.work_planning.objectLocationP;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;

import com.example.master_segy.data.objectLocationP.ObjectLocation;
import com.example.master_segy.databinding.FragmentWorkPlanningBinding;
import com.example.master_segy.program.CustomDividerItemDecoration;
import com.example.master_segy.program.work_planning.plateP.PlatesActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class WorkPlanningFragment extends Fragment {
    EditText searchEditText;
    RecyclerView recyclerView;
    private ObjectAdapter objectAdapter;
    private FloatingActionButton addObject;
    private FragmentWorkPlanningBinding binding;
    AppDataBase db;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkPlanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerView;
         db = AppDataBase.getInstance(getActivity().getApplicationContext());
        ObjectAdapter.OnObjectClickListener stateClickListener = new ObjectAdapter.OnObjectClickListener() {
            @Override
            public void onObjectClick(ObjectLocation objectLocation, int position) {
                Intent intent = new Intent(getContext(), PlatesActivity.class);
                intent.putExtra(PlatesActivity.OBJECT_ID, objectLocation.get_id());
                startActivity(intent);
            }
        };
        // создаем адаптер
        objectAdapter = new ObjectAdapter(getContext(), stateClickListener);
        loadObjects();
        initRecyclerView();
        addObject = binding.fab;

        addObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("g", "onClick: opening dialog.");
                Add_EditObjectLocation dialog = new Add_EditObjectLocation();

                dialog.show(getChildFragmentManager(), "MyDialogFragment");
            }
        });
        searchEditText = binding.searchEditText;
        searchEditText.addTextChangedListener(new TextWatcher() {
            // метод вызывается при изменении текста в EditText
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                objectAdapter.getFilter().filter(s.toString()); // запускаем фильтр адаптера
            }

            // методы не используются
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadObjects();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        int dividerColor = ContextCompat.getColor(getActivity().getApplicationContext(), R.color.table_border);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation(), dividerColor));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(objectAdapter);
    }

    @Override
    public void onResume() {
        loadObjects();
        super.onResume();
    }

    public void loadObjects() {
        objectAdapter.setObjectLocationList(db.objectDao().getAll());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}