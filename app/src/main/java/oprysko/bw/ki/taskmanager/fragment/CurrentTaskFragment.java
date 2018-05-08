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
        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());
//            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR) && calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
//                //newTask.setDateStatus(C0372R.string.separator_overdue);
////                if (!this.adapter.containsSeparatorOverdue) {
////                    this.adapter.containsSeparatorOverdue = true;
////                    separator = new ModelSeparator(C0372R.string.separator_overdue);
////                }
//            } else if (calendar.get(6) == Calendar.getInstance().get(6) && calendar.get(1) == Calendar.getInstance().get(1)) {
//                newTask.setDateStatus(C0372R.string.separator_today);
//                if (!this.adapter.containsSeparatorToday) {
//                    this.adapter.containsSeparatorToday = true;
//                    separator = new ModelSeparator(C0372R.string.separator_today);
//                }
//            } else if (calendar.get(6) == Calendar.getInstance().get(6) + 1 && calendar.get(1) == Calendar.getInstance().get(1)) {
//                newTask.setDateStatus(C0372R.string.separator_tomorrow);
//                if (!this.adapter.containsSeparatorTomorrow) {
//                    this.adapter.containsSeparatorTomorrow = true;
//                    separator = new ModelSeparator(C0372R.string.separator_tomorrow);
//                }
//            } else if (calendar.get(6) > Calendar.getInstance().get(6) + 1 || calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
//                newTask.setDateStatus(C0372R.string.separator_future);
//                if (!this.adapter.containsSeparatorFuture) {
//                    this.adapter.containsSeparatorFuture = true;
//                    separator = new ModelSeparator(C0372R.string.separator_future);
//                }
//            }
//        }
            if (position != -1) {
                if (!this.adapter.getItem(position - 1).isTask()) {
                    if (position - 2 < 0 || !this.adapter.getItem(position - 2).isTask()) {
                        if (position - 2 < 0 && newTask.getDate() == 0) {
                            position--;
                        }
                    } else if (((Task) this.adapter.getItem(position - 2)).getDateStatus() == newTask.getDateStatus()) {
                        position--;
                    }
                }
//            if (separator != null) {
//                this.adapter.addItem(position - 1, separator);
//            }
                this.adapter.addItem(position, newTask);
            } else {
//            if (separator != null) {
//                this.adapter.addItem(separator);
//            }
                this.adapter.addItem(newTask);
            }

            if (saveToDB) {
                activity.dbHelper.saveTask(newTask);
            }
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
