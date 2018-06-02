package oprysko.bw.ki.taskmanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.model.Separator;
import oprysko.bw.ki.taskmanager.model.Task;

public class ActualTaskFragment extends TaskFragment {

    public ActualTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actual_tasks, container, false);
    }

    @Override
    public void addTask(Task newTask, boolean saveToDB) {
    }


    @Override
    public void moveTask(Task task) {

    }

    @Override
    public void addTaskFromDB() {

    }

    @Override
    public void findTasks(String title) {

    }
}
