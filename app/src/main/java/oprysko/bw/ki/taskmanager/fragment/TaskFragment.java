package oprysko.bw.ki.taskmanager.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import oprysko.bw.ki.taskmanager.MainActivity;
import oprysko.bw.ki.taskmanager.adapter.TaskAdapter;
import oprysko.bw.ki.taskmanager.model.Task;

public abstract class TaskFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;

    public MainActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }

        addTaskFromDB();
    }

    public abstract void addTask(Task newTask, boolean saveToDB);

    public abstract void moveTask(Task task);

    public abstract void addTaskFromDB();
}
