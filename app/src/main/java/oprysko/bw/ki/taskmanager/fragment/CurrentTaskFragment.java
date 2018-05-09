package oprysko.bw.ki.taskmanager.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.adapter.CurrentTaskAdapter;
import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.model.Task;

import static java.io.File.separator;

public class CurrentTaskFragment extends TaskFragment {

    OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener {
        void onTaskDone(Task modelTask);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.onTaskDoneListener = (OnTaskDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskDoneListener");
        }
    }

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new CurrentTaskAdapter(this);
        this.recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void addTask(Task newTask, boolean saveToDB) {
        int position = -1;
        checkAdapter();
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            if (this.adapter.getItem(i).isTask()) {
                if (newTask.getDate() < ((Task) this.adapter.getItem(i)).getDate()) {
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            this.adapter.addItem(position, newTask);
        } else {
            this.adapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    public void checkAdapter() {
        if (this.adapter == null) {
            this.adapter = new CurrentTaskAdapter(this);
        }
    }

    @Override
    public void moveTask(Task task) {
        this.onTaskDoneListener.onTaskDone(task);
    }

    @Override
    public void addTaskFromDB() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.getQueryManager().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(Task.STASUS_CURRENT),
                Integer.toString(Task.STASUS_OVERDUE)}, DBHelper.TASKS_DATE_COLUMN));

        for (Task task : tasks) {
            addTask(task, false);
        }
    }
}
