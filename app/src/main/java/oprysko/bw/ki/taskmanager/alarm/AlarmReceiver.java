package oprysko.bw.ki.taskmanager.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import oprysko.bw.ki.taskmanager.MainActivity;
import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.model.Task;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper dbHelper = new DBHelper(context);
        String title = intent.getStringExtra("title");
        long timeStamp = intent.getLongExtra("time_stamp", 0);
        int color = intent.getIntExtra("color", 0);

        // FIXME
        dbHelper.getUpdateManager().updateStatus(timeStamp, Task.STATUS_DONE);

        Intent result = new Intent(context, MainActivity.class);
        result.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                (int) timeStamp, result, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(title)
                .setColor(context.getResources().getColor(color))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setAutoCancel(true)
                .setLights(Color.BLUE, 700, 1500)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) timeStamp, builder.build());


        Task task = dbHelper.getQueryManager().getTask(timeStamp);
        Bundle bundle = new Bundle();
        bundle.putString("title", task.getTitle());
        bundle.putString("content", task.getContent());
        bundle.putLong("date", task.getDate());
        bundle.putInt("priority", task.getPriority());
        bundle.putLong("time_stamp", task.getTimeStamp());
        bundle.putInt("status", task.getStatus());

        Intent updateIntent = new Intent("BROADCAST_REFRESH");
        updateIntent.putExtra("task", bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
        dbHelper.close();
    }
}
