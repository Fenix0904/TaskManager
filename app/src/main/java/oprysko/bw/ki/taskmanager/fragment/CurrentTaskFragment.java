package oprysko.bw.ki.taskmanager.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.adapter.CurrentTaskAdapter;
import oprysko.bw.ki.taskmanager.model.Task;

public class CurrentTaskFragment extends Fragment {

    private RecyclerView rvCurrentTasks;
    private RecyclerView.LayoutManager layoutManager;
    private CurrentTaskAdapter adapter;

    public CurrentTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_current_task, container, false);

        rvCurrentTasks = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);

        layoutManager = new LinearLayoutManager(getActivity());
        rvCurrentTasks.setLayoutManager(layoutManager);

        adapter = new CurrentTaskAdapter();
        rvCurrentTasks.setAdapter(adapter);

        return rootView;
    }

    public void addTask(Task newTask) {
        int position = -1;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                Task task = (Task) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            adapter.addItem(position, newTask);
        } else {
            adapter.addItem(newTask);
        }
    }

}
