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
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.Utils;
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
        TextView title = (TextView) v.findViewById(R.id.tvTaskHeader);
        TextView date = (TextView) v.findViewById(R.id.tvTaskDate);
        CircleImageView priority = (CircleImageView) v.findViewById(R.id.cvTaskPriority);
        return new TaskViewHolder(v, title, date, priority);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = (Item) this.items.get(position);
        if (item.isTask()) {
            holder.itemView.setEnabled(true);
            final Task task = (Task) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            final View itemView = taskViewHolder.itemView;
            final Resources resources = itemView.getResources();

            taskViewHolder.title.setText(task.getTitle());
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);
            itemView.setBackgroundColor(resources.getColor(R.color.gray_50));
            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_disabled_material_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_disabled_material_light));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setImageResource(R.drawable.ic_access_time_white_24dp);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                        }
                    }, 1000);

                    return true;
                }
            });

            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.setStatus(Task.STASUS_CURRENT);
                    getTaskFragment().activity.dbHelper.getUpdateManager().updateStatus(task.getTimeStamp(), Task.STASUS_CURRENT);


                    itemView.setBackgroundColor(resources.getColor(R.color.gray_200));
                    taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_default_material_light));
                    taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_default_material_light));
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                    ObjectAnimator animator = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", 180f, 0f);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (task.getStatus() != 2) {
                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0.0f, (float) -itemView.getWidth());
                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView, "translationX", (float) -itemView.getWidth(), 0.0f);
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
                }
            });
        }
    }
}
