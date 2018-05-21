package oprysko.bw.ki.taskmanager.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import oprysko.bw.ki.taskmanager.MainActivity;
import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.adapter.TaskAdapter;
import oprysko.bw.ki.taskmanager.alarm.AlarmHelper;
import oprysko.bw.ki.taskmanager.dialog.CreateEditTaskDialogFragment;
import oprysko.bw.ki.taskmanager.model.Item;
import oprysko.bw.ki.taskmanager.model.Task;

public abstract class TaskFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;

    public MainActivity activity;
    public AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }
        alarmHelper = AlarmHelper.getInstance();
        addTaskFromDB();
    }

    public void updateTask(Task task) {
        adapter.updateItem(task);
    }

    public abstract void addTask(Task newTask, boolean saveToDB);

    public abstract void moveTask(Task task);

    public abstract void addTaskFromDB();

    public abstract void findTasks(String title);

    public void showEditTaskDialog(Task task) {
        DialogFragment dialog = CreateEditTaskDialogFragment.newInstance(task);
        dialog.show(getActivity().getFragmentManager(), "EditTaskDialogFragment");
    }

    public void removeTaskDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_remove_message);
        Item item = adapter.getItem(position);
        if (item.isTask()) {
            Task removingTask = (Task) item;
            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                adapter.removeItem(position);
                isRemoved[0] = true;
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout),
                        R.string.snackbar_removed, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.dialog_cancel, v -> {
                    addTask(activity.dbHelper.getQueryManager().getTask(timeStamp), false);
                    isRemoved[0] = false;
                });

                snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if (isRemoved[0]) {
                            alarmHelper.removeAlarm(timeStamp);
                            activity.dbHelper.removeTask(timeStamp);
                        }
                    }
                });
                snackbar.show();
                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("task");
            Task task = new Task();
            task.setTitle(bundle.getString("title"));
            task.setContent(bundle.getString("content"));
            task.setDate(bundle.getLong("date"));
            task.setPriority(bundle.getInt("priority"));
            task.setTimeStamp(bundle.getLong("time_stamp"));
            task.setStatus(bundle.getInt("status"));
            //TODO when notification shows up, move task into 'Done' section.
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        super.onResume();
    }
}
