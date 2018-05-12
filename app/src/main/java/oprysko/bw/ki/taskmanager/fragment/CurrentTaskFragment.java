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
import oprysko.bw.ki.taskmanager.model.Separator;
import oprysko.bw.ki.taskmanager.model.Task;

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
        Separator separator = null;
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

            int instanceDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

            if (calendar.get(Calendar.DAY_OF_YEAR) < instanceDay) {
                newTask.setDateStatus(Separator.TYPE_OVERDUE);
                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    separator = new Separator(Separator.TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == instanceDay) {
                newTask.setDateStatus(Separator.TYPE_TODAY);
                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    separator = new Separator(Separator.TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == instanceDay + 1) {
                newTask.setDateStatus(Separator.TYPE_TOMORROW);
                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    separator = new Separator(Separator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > instanceDay + 1) {
                newTask.setDateStatus(Separator.TYPE_FUTURE);
                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    separator = new Separator(Separator.TYPE_FUTURE);
                }
            }
        }
        if (position != -1) {
            if (!adapter.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && adapter.getItem(position - 2).isTask()) {
                    Task task = (Task) adapter.getItem(position - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        position -= 1;
                    }
                } else if (position - 2 < 0 && newTask.getDate() == 0) {
                    position = -1;
                }
            }

            if (separator != null) {
                adapter.addItem(position - 1, separator);
            }
            this.adapter.addItem(position, newTask);
        } else {
            if (separator != null) {
                adapter.addItem(separator);
            }
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
        alarmHelper.removeAlarm(task.getTimeStamp());
        this.onTaskDoneListener.onTaskDone(task);
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.getQueryManager().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(Task.STATUS_CURRENT),
                Integer.toString(Task.STATUS_OVERDUE)}, DBHelper.TASKS_DATE_COLUMN));

        for (Task task : tasks) {
            addTask(task, false);
        }
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.getQueryManager().getTasks(DBHelper.SELECTION_LIKE_TITLE + " AND "
                        + DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(Task.STATUS_CURRENT),
                        Integer.toString(Task.STATUS_OVERDUE)}, DBHelper.TASKS_DATE_COLUMN));

        for (Task task : tasks) {
            addTask(task, false);
        }
    }
}
