package oprysko.bw.ki.taskmanager.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import oprysko.bw.ki.taskmanager.model.Task;

public class DBQueryManager {

    private SQLiteDatabase database;

    DBQueryManager (SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
    }

    public List<Task> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<Task> tasks = new ArrayList<>();

        Cursor c = database.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);
        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(DBHelper.TASKS_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASKS_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASKS_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASKS_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASKS_TIME_STAMP_COLUMN));

                Task task = new Task(title, date, priority, status, timeStamp);
                tasks.add(task);
            } while (c.moveToNext());
        }
        c.close();

        return tasks;
    }

}
