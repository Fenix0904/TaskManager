package oprysko.bw.ki.taskmanager.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import oprysko.bw.ki.taskmanager.fragment.ActualTaskFragment;
import oprysko.bw.ki.taskmanager.fragment.CurrentTaskFragment;
import oprysko.bw.ki.taskmanager.fragment.DoneTaskFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;
    public static final int ACTUAL_TASK_FRAGMENT_POSITION = 0;
    public static final int CURRENT_TASK_FRAGMENT_POSITION = 1;
    public static final int DONE_TASK_FRAGMENT_POSITION = 2;

    private CurrentTaskFragment currentTaskFragment;
    private DoneTaskFragment doneTaskFragment;
    private ActualTaskFragment actualTaskFragment;

    public TabAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        this.currentTaskFragment = new CurrentTaskFragment();
        this.doneTaskFragment = new DoneTaskFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ActualTaskFragment();
            case 1:
                return currentTaskFragment;
            case 2:
                return doneTaskFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
