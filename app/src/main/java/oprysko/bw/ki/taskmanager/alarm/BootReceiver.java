package oprysko.bw.ki.taskmanager.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.model.Task;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper dbHelper = new DBHelper(context);

        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(dbHelper.getQueryManager().getTasks(DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(Task.STATUS_CURRENT),
                Integer.toString(Task.STATUS_OVERDUE)}, DBHelper.TASKS_DATE_COLUMN));

        for (Task task : tasks) {
            if (task.getDate() != 0) {
                alarmHelper.setAlarm(task);
            }
        }

    }
}
