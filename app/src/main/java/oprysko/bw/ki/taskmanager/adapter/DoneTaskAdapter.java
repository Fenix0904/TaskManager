package oprysko.bw.ki.taskmanager.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.DateUtils;
import oprysko.bw.ki.taskmanager.fragment.TaskFragment;
import oprysko.bw.ki.taskmanager.model.Item;
import oprysko.bw.ki.taskmanager.model.Task;

public class DoneTaskAdapter extends TaskAdapter {

    public DoneTaskAdapter(TaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_task, parent, false);
        TextView title = (TextView) v.findViewById(R.id.task_title);
        TextView content = (TextView) v.findViewById(R.id.task_content);
        TextView date = (TextView) v.findViewById(R.id.task_date);
        ImageView priority = (ImageView) v.findViewById(R.id.task_priority);
        ImageView icon = (ImageView) v.findViewById(R.id.task_icon);
        return new TaskViewHolder(v, title, content, date, priority, icon);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = this.items.get(position);
        if (item.isTask()) {
            holder.itemView.setEnabled(true);
            final Task task = (Task) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            final View itemView = taskViewHolder.itemView;
            final Resources resources = itemView.getResources();

            taskViewHolder.title.setText(task.getTitle());
            taskViewHolder.content.setText(task.getContent());
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(DateUtils.getTime(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);

            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_disabled_light));
            taskViewHolder.content.setTextColor(resources.getColor(R.color.secondary_text_disabled_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_disabled_light));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setEnabled(true);
            taskViewHolder.icon.setImageResource(R.drawable.baseline_done_white_24);

            itemView.setOnLongClickListener(v -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition()), 500);
                return true;
            });

            taskViewHolder.priority.setOnClickListener(v -> {
                taskViewHolder.priority.setEnabled(false);
                task.setStatus(Task.STATUS_CURRENT);
                getTaskFragment().activity.dbHelper.getUpdateManager().updateStatus(task.getTimeStamp(), Task.STATUS_CURRENT);

                taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_default_light));
                taskViewHolder.content.setTextColor(resources.getColor(R.color.secondary_text_default_light));
                taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_default_light));
                taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                ObjectAnimator animator = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", 180f, 0f);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (task.getStatus() != 2) {
                            taskViewHolder.icon.setImageResource(R.drawable.ic_notifications_white_24dp);
                            ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0.0f,
                                    (float) -itemView.getWidth());
                            ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView, "translationX",
                                    (float) -itemView.getWidth(), 0.0f);
                            translationX.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itemView.setVisibility(View.GONE);
                                    DoneTaskAdapter.this.getTaskFragment().moveTask(task);
                                    DoneTaskAdapter.this.removeItem(taskViewHolder.getLayoutPosition());;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });

                            AnimatorSet translationSet = new AnimatorSet();
                            translationSet.play(translationX).before(translationXBack);
                            translationSet.start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            });
        }
    }
}
