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

public class MainActivity extends AppCompatActivity implements AddingTaskDialogFragment.AddingTaskListener{

    private FragmentManager fragmentManager;

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
        TabAdapter tabAdapter = new TabAdapter(fragmentManager, 2);

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
    public void onTaskAdded() {
        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskAddedCancel() {
        Toast.makeText(this, "Task canceled", Toast.LENGTH_SHORT).show();
    }
}
