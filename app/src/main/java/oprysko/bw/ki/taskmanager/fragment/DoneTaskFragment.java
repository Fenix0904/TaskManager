package oprysko.bw.ki.taskmanager.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.adapter.DoneTaskAdapter;
import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.model.Task;


public class DoneTaskFragment extends TaskFragment {

    OnTaskRestoreListener onTaskRestoreListener;

    public interface OnTaskRestoreListener {
        void onTaskRestore(Task modelTask);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.onTaskRestoreListener = (OnTaskRestoreListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskRestoreListener");
        }
    }

    public DoneTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootVIew = inflater.inflate(R.layout.fragment_done_task, container, false);

        this.recyclerView = (RecyclerView) rootVIew.findViewById(R.id.rvDoneTasks);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        this.adapter = new DoneTaskAdapter(this);
        this.recyclerView.setAdapter(adapter);

        return rootVIew;
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

    @Override
    public void moveTask(Task task) {
        alarmHelper.setAlarm(task);
        this.onTaskRestoreListener.onTaskRestore(task);
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.getQueryManager().getTasks(DBHelper.SELECTION_STATUS,
                new String[]{Integer.toString(Task.STATUS_DONE)}, DBHelper.TASKS_DATE_COLUMN));

        for (Task task : tasks) {
            addTask(task, false);
        }
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.getQueryManager().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                + DBHelper.SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(Task.STATUS_DONE)},
                DBHelper.TASKS_DATE_COLUMN));

        for (Task task : tasks) {
            addTask(task, false);
        }
    }

    public void checkAdapter() {
        if (this.adapter == null) {
            this.adapter = new DoneTaskAdapter(this);
        }
    }
}
