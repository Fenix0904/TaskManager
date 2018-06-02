package oprysko.bw.ki.taskmanager;

import android.app.DialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import oprysko.bw.ki.taskmanager.adapter.TabAdapter;
import oprysko.bw.ki.taskmanager.alarm.AlarmHelper;
import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.dialog.CreateEditTaskDialogFragment;
import oprysko.bw.ki.taskmanager.fragment.ActualTaskFragment;
import oprysko.bw.ki.taskmanager.fragment.CurrentTaskFragment;
import oprysko.bw.ki.taskmanager.fragment.DoneTaskFragment;
import oprysko.bw.ki.taskmanager.model.Task;

public class MainActivity extends AppCompatActivity implements CreateEditTaskDialogFragment.AddingTaskListener,
        CurrentTaskFragment.OnTaskDoneListener, DoneTaskFragment.OnTaskRestoreListener{

    private FragmentManager fragmentManager;
    private TabAdapter tabAdapter;
    private CurrentTaskFragment currentTaskFragment;
    private DoneTaskFragment doneTaskFragment;
    private ActualTaskFragment actualTaskFragment;
    private SearchView searchView;

    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmHelper.getInstance().init(getApplicationContext());

        dbHelper = new DBHelper(getApplicationContext());
        fragmentManager = getFragmentManager();

        setUI();
    }

    private void setUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.actual_tasks));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_tasks));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_tasks));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabAdapter = new TabAdapter(fragmentManager, tabLayout.getTabCount());

        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        actualTaskFragment = (ActualTaskFragment) tabAdapter.getItem(TabAdapter.ACTUAL_TASK_FRAGMENT_POSITION);
        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);

        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentTaskFragment.findTasks(newText);
                doneTaskFragment.findTasks(newText);
                actualTaskFragment.findTasks(newText);
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            DialogFragment addingTaskDialog = new CreateEditTaskDialogFragment();
            addingTaskDialog.show(fragmentManager, "CreateEditTaskDialogFragment");
        });
    }

    @Override
    public void onTaskAdded(Task task) {
        currentTaskFragment.addTask(task, true);
    }

    @Override
    public void onTaskAddedCancel() {
        Toast.makeText(this, "Task canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRestore(Task modelTask) {
        currentTaskFragment.addTask(modelTask, false);
    }

    @Override
    public void onTaskDone(Task modelTask) {
        doneTaskFragment.addTask(modelTask, false);
    }

    @Override
    public void onTaskEdited(Task task) {
        currentTaskFragment.updateTask(task);
        dbHelper.getUpdateManager().updateTask(task);
    }
}
