package oprysko.bw.ki.taskmanager;

import android.app.DialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import oprysko.bw.ki.taskmanager.adapter.TabAdapter;
import oprysko.bw.ki.taskmanager.dialog.AddingTaskDialogFragment;
import oprysko.bw.ki.taskmanager.fragment.CurrentTaskFragment;
import oprysko.bw.ki.taskmanager.fragment.DoneTaskFragment;
import oprysko.bw.ki.taskmanager.model.Task;

public class MainActivity extends AppCompatActivity implements AddingTaskDialogFragment.AddingTaskListener{

    private FragmentManager fragmentManager;
    private TabAdapter tabAdapter;
    private CurrentTaskFragment currentTaskFragment;
    private DoneTaskFragment doneTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addingTaskDialog = new AddingTaskDialogFragment();
                addingTaskDialog.show(fragmentManager, "AddingTaskDialogFragment");
            }
        });
    }

    @Override
    public void onTaskAdded(Task task) {
        currentTaskFragment.addTask(task);
    }

    @Override
    public void onTaskAddedCancel() {
        Toast.makeText(this, "Task canceled", Toast.LENGTH_SHORT).show();
    }
}
