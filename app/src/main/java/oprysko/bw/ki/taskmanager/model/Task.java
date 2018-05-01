package oprysko.bw.ki.taskmanager.model;

/**
 * Created by Святослав on 01.05.2018.
 */

public class Task implements Item {

    private String title;
    private long date;

    @Override
    public boolean isTask() {
        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
