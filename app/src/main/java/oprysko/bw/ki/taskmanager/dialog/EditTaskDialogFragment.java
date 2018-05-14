package oprysko.bw.ki.taskmanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.Utils;
import oprysko.bw.ki.taskmanager.alarm.AlarmHelper;
import oprysko.bw.ki.taskmanager.model.Task;

public class EditTaskDialogFragment extends DialogFragment {

    public interface EditingTaskListener {
        void onTaskEdited(Task task);
    }

    private EditingTaskListener editingTaskListener;

    public static EditTaskDialogFragment newInstance(Task task) {

        EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", task.getTitle());
        bundle.putLong("date", task.getDate());
        bundle.putInt("priority", task.getPriority());
        bundle.putLong("time_stamp", task.getTimeStamp());
        bundle.putInt("status", task.getStatus());

        editTaskDialogFragment.setArguments(bundle);
        return editTaskDialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editingTaskListener = (EditingTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement EditingTaskListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        String title = args.getString("title");
        long date = args.getLong("date", 0);
        long timeStamp = args.getLong("time_stamp", 0);
        int priority = args.getInt("priority", 0);
        int status = args.getInt("status", 0);

        final Task task = new Task();
        task.setTitle(title);
        task.setPriority(priority);
        task.setDate(date);
        task.setTimeStamp(timeStamp);
        task.setStatus(status);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.dialog_editing_title);

        View container = inflater.inflate(R.layout.dialog_task, null);
        final TextInputLayout tilHeader = (TextInputLayout) container.findViewById(R.id.tilTaskHeader);
        final EditText etHeader = tilHeader.getEditText();
        final TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.tilTaskDate);
        final EditText etDate = tilDate.getEditText();
        final TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.tilTaskTime);
        final EditText etTime = tilTime.getEditText();

        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);

        tilHeader.setHint(getResources().getString(R.string.hint_task_title));
        tilDate.setHint(getResources().getString(R.string.hint_task_date));
        tilTime.setHint(getResources().getString(R.string.hint_task_time));

        etHeader.setText(task.getTitle());
        etHeader.setSelection(etHeader.length());
        etDate.setText(Utils.getDate(task.getDate()));
        etTime.setText(Utils.getTime(task.getDate()));

        builder.setView(container);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Task.PRIORITY_LEVELS);
        spPriority.setAdapter(priorityAdapter);

        spPriority.setSelection(task.getPriority());

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Calendar calendar = Calendar.getInstance();
        //TODO remove and write correct functionality
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        if (etDate.length() != 0 || etTime.length() != 0) {
            calendar.setTimeInMillis(task.getDate());
        }

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length() == 0) {
                    etDate.setText(" ");
                }

                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etDate.setText(null);
                    }
                };
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTime.length() == 0) {
                    etTime.setText(" ");
                }

                DialogFragment timePickerFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        etTime.setText(null);
                    }
                };
                timePickerFragment.show(getFragmentManager(), "TImePickerFragment");
            }
        });

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etHeader.getText().toString());
                task.setDate(calendar.getTimeInMillis());
                task.setStatus(Task.STATUS_CURRENT);
                editingTaskListener.onTaskEdited(task);
                AlarmHelper alarmHelper = AlarmHelper.getInstance();
                alarmHelper.setAlarm(task);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if (etHeader.length() == 0) {
                    positiveButton.setEnabled(false);
                    tilHeader.setError(getResources().getString(R.string.dialog_error_empty_title));
                }

                etHeader.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                            tilHeader.setError(getResources().getString(R.string.dialog_error_empty_title));
                        } else {
                            positiveButton.setEnabled(true);
                            tilHeader.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });

        return alertDialog;
    }
}
