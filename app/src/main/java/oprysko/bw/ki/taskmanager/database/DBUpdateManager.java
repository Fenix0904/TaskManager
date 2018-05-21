package oprysko.bw.ki.taskmanager.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import oprysko.bw.ki.taskmanager.model.Task;

public class DBUpdateManager {

    SQLiteDatabase database;

    public DBUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }


    public void updateStatus(long timestamp, int status) {
        update(DBHelper.TASKS_STATUS_COLUMN, timestamp, status);
    }

    public void updateTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TASKS_TITLE_COLUMN, task.getTitle());
        values.put(DBHelper.TASKS_CONTENT_COLUMN, task.getContent());
        values.put(DBHelper.TASKS_DATE_COLUMN, task.getDate());
        values.put(DBHelper.TASKS_PRIORITY_COLUMN, task.getPriority());
        values.put(DBHelper.TASKS_STATUS_COLUMN, task.getStatus());
        database.update(DBHelper.TASKS_TABLE, values, DBHelper.TASKS_TIME_STAMP_COLUMN + " = " + task.getTimeStamp(), null);
    }

    private void update(String column, long key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        database.update(DBHelper.TASKS_TABLE, cv, DBHelper.TASKS_TIME_STAMP_COLUMN + " = " + key, null);
    }

    private void update(String column, long key, long value) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        database.update(DBHelper.TASKS_TABLE, cv, DBHelper.TASKS_TIME_STAMP_COLUMN + " = " + key, null);
    }
}
