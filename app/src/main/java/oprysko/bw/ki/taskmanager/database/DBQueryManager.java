package oprysko.bw.ki.taskmanager.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import oprysko.bw.ki.taskmanager.model.Task;

public class DBQueryManager {

    private SQLiteDatabase database;

    DBQueryManager(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
    }

    public Task getTask(long timeStamp) {
        Task task = null;
        Cursor cursor = database.query(DBHelper.TASKS_TABLE, null, DBHelper.SELECTION_TIME_STAMP,
                new String[]{Long.toString(timeStamp)}, null, null, null);
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(DBHelper.TASKS_TITLE_COLUMN));
            String content = cursor.getString(cursor.getColumnIndex(DBHelper.TASKS_CONTENT_COLUMN));
            long date = cursor.getLong(cursor.getColumnIndex(DBHelper.TASKS_DATE_COLUMN));
            int priority = cursor.getInt(cursor.getColumnIndex(DBHelper.TASKS_PRIORITY_COLUMN));
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.TASKS_STATUS_COLUMN));

            task = new Task(title, content, date, priority, status, timeStamp);
        }
        cursor.close();
        return task;
    }

    public List<Task> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<Task> tasks = new ArrayList<>();

        Cursor c = database.query(DBHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);
        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(DBHelper.TASKS_TITLE_COLUMN));
                String content = c.getString(c.getColumnIndex(DBHelper.TASKS_CONTENT_COLUMN));
                long date = c.getLong(c.getColumnIndex(DBHelper.TASKS_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASKS_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASKS_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASKS_TIME_STAMP_COLUMN));

                Task task = new Task(title, content, date, priority, status, timeStamp);
                tasks.add(task);
            } while (c.moveToNext());
        }
        c.close();

        return tasks;
    }

}
