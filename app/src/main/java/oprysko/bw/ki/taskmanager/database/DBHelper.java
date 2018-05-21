package oprysko.bw.ki.taskmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import oprysko.bw.ki.taskmanager.model.Task;


public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "main_db";

    public static final String TASKS_TABLE = "tasks";
    public static final String TASKS_CONTENT_COLUMN = "task_title";
    public static final String TASKS_TITLE_COLUMN = "task_content";
    public static final String TASKS_DATE_COLUMN = "task_date";
    public static final String TASKS_PRIORITY_COLUMN = "task_priority";
    public static final String TASKS_STATUS_COLUMN = "task_status";
    public static final String TASKS_TIME_STAMP_COLUMN = "task_time_stamp";

    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE " + TASKS_TABLE
            + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASKS_TITLE_COLUMN + " TEXT NOT NULL, "
            + TASKS_CONTENT_COLUMN + " TEXT, "
            + TASKS_DATE_COLUMN + " LONG, "
            + TASKS_PRIORITY_COLUMN + " INTEGER, "
            + TASKS_STATUS_COLUMN + " INTEGER, "
            + TASKS_TIME_STAMP_COLUMN + " LONG);";

    public static final String SELECTION_STATUS = DBHelper.TASKS_STATUS_COLUMN + " = ?";
    public static final String SELECTION_TIME_STAMP = DBHelper.TASKS_TIME_STAMP_COLUMN + " = ?";
    public static final String SELECTION_LIKE_TITLE = DBHelper.TASKS_TITLE_COLUMN + " LIKE ?";

    private DBQueryManager queryManager;
    private DBUpdateManager updateManager;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        queryManager = new DBQueryManager(getReadableDatabase());
        updateManager = new DBUpdateManager(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASKS_TABLE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TASKS_TABLE);
        onCreate(db);
    }

    public void saveTask(Task task) {
        ContentValues newValues = new ContentValues();
        newValues.put(TASKS_TITLE_COLUMN, task.getTitle());
        newValues.put(TASKS_CONTENT_COLUMN, task.getContent());
        newValues.put(TASKS_DATE_COLUMN, task.getDate());
        newValues.put(TASKS_PRIORITY_COLUMN, task.getPriority());
        newValues.put(TASKS_STATUS_COLUMN, task.getStatus());
        newValues.put(TASKS_TIME_STAMP_COLUMN, task.getTimeStamp());

        getWritableDatabase().insert(TASKS_TABLE, null, newValues);
    }

    public void removeTask(long timeStamp) {
        getWritableDatabase().delete(TASKS_TABLE, SELECTION_TIME_STAMP, new String[]{Long.toString(timeStamp)});
    }

    public DBQueryManager getQueryManager() {
        return queryManager;
    }

    public DBUpdateManager getUpdateManager() {
        return updateManager;
    }
}









