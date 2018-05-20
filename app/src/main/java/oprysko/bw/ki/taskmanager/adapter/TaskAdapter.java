package oprysko.bw.ki.taskmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import oprysko.bw.ki.taskmanager.fragment.TaskFragment;
import oprysko.bw.ki.taskmanager.model.Item;
import oprysko.bw.ki.taskmanager.model.Separator;
import oprysko.bw.ki.taskmanager.model.Task;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Item> items;
    private TaskFragment taskFragment;

    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorFuture;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorToday;

    public TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
        items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addItem(int position, Item item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void updateItem(Task newTask) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).isTask()) {
                Task task = (Task) getItem(i);
                if (task.getTimeStamp() == newTask.getTimeStamp()) {
                    removeItem(i);
                    getTaskFragment().addTask(newTask, false);
                }
            }
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position <= getItemCount() - 1) {
            items.remove(position);
            notifyItemRemoved(position);
            if (position - 1 >= 0 && position <= getItemCount() - 1) {
                if (!getItem(position).isTask() && !getItem(position - 1).isTask()) {
                    Separator separator = (Separator) getItem(position - 1);
                    checkSeparators(separator.getType());
                    items.remove(position - 1);
                    notifyItemRemoved(position - 1);
                }
            } else if (getItemCount() - 1 >= 0 && !getItem(getItemCount() - 1).isTask()) {
                Separator separator = (Separator) getItem(getItemCount() - 1);
                checkSeparators(separator.getType());

                int positionTemp = getItemCount() - 1;
                items.remove(positionTemp);
                notifyItemRemoved(positionTemp);
            }
        }
    }

    private void checkSeparators(int type) {
        switch (type) {
            case Separator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case Separator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case Separator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case Separator.TYPE_FUTURE:
                containsSeparatorFuture = false;
                break;
        }
    }

    public void removeAllItems() {
        if (getItemCount() != 0) {
            items = new ArrayList<>();
            notifyDataSetChanged();
            containsSeparatorOverdue = false;
            containsSeparatorToday = false;
            containsSeparatorTomorrow = false;
            containsSeparatorFuture = false;
        }
    }

    public TaskFragment getTaskFragment() {
        return taskFragment;
    }

    protected class TaskViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView content;
        protected TextView date;
        protected ImageView priority;
        protected ImageView icon;

        public TaskViewHolder(View itemView, TextView title, TextView content, TextView date, ImageView priority, ImageView icon) {
            super(itemView);
            this.title = title;
            this.content = content;
            this.date = date;
            this.priority = priority;
            this.icon = icon;
        }
    }

    protected class SeparatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;

        public SeparatorViewHolder(View itemView, TextView type) {
            super(itemView);
            this.type = type;
        }
    }
}
