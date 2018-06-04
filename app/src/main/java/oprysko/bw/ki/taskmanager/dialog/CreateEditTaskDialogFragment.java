package oprysko.bw.ki.taskmanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.DateUtils;
import oprysko.bw.ki.taskmanager.alarm.AlarmHelper;
import oprysko.bw.ki.taskmanager.model.Tag;
import oprysko.bw.ki.taskmanager.model.Task;

public class CreateEditTaskDialogFragment extends DialogFragment {

    private TextInputLayout tilHeader;
    private EditText etHeader;
    private TextInputLayout tilContent;
    private EditText etContent;
    private TextInputLayout tilDate;
    private EditText etDate;
    private TextInputLayout tilTime;
    private EditText etTime;
    private Spinner spPriority;
    private Spinner spTag;
    private Task task;
    private boolean isEditing;

    private AddingTaskListener addingTaskListener;

    public interface AddingTaskListener {
        void onTaskAdded(Task task);

        void onTaskAddedCancel();

        void onTaskEdited(Task task);
    }

    public static CreateEditTaskDialogFragment newInstance(Task task) {
        CreateEditTaskDialogFragment editTaskDialogFragment = new CreateEditTaskDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", task.getTitle());
        bundle.putString("content", task.getContent());
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
            addingTaskListener = (AddingTaskListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement AddingTaskListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.dialog_title);

        View container = inflater.inflate(R.layout.dialog_task, null);
        tilHeader = (TextInputLayout) container.findViewById(R.id.tilTaskHeader);
        etHeader = tilHeader.getEditText();
        tilContent = (TextInputLayout) container.findViewById(R.id.tilTaskContent);
        etContent = tilContent.getEditText();
        tilDate = (TextInputLayout) container.findViewById(R.id.tilTaskDate);
        etDate = tilDate.getEditText();
        tilTime = (TextInputLayout) container.findViewById(R.id.tilTaskTime);
        etTime = tilTime.getEditText();
        spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);
        spTag = (Spinner) container.findViewById(R.id.spDialogTaskTags);

        tilHeader.setHint(getResources().getString(R.string.hint_task_title));
        tilContent.setHint(getResources().getString(R.string.hint_task_content));
        tilDate.setHint(getResources().getString(R.string.hint_task_date));
        tilTime.setHint(getResources().getString(R.string.hint_task_time));

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Task.PRIORITY_LEVELS);
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Tag.TAGS);
        spPriority.setAdapter(priorityAdapter);
        spTag.setAdapter(tagAdapter);

        builder.setView(container);
        task = initValues();
        fillDialogWithValues(task);


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
        if (task.getDate() != 0) {
            calendar.setTimeInMillis(task.getDate());
        }
        //TODO remove and write correct functionality
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));

        etDate.setOnClickListener(v -> {
            if (etDate.length() == 0) {
                etDate.setText(" ");
            }
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), (DatePicker, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etDate.setText(DateUtils.getDate(calendar.getTimeInMillis()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        etTime.setOnClickListener(v -> {
            if (etTime.length() == 0) {
                etTime.setText(" ");
            }
            TimePickerDialog timePicker = new TimePickerDialog(getActivity(), (TimePicker, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                etTime.setText(DateUtils.getTime(task.getDate()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity().getApplicationContext()));
            timePicker.show();
        });

        builder.setPositiveButton(R.string.dialog_ok, null);
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            addingTaskListener.onTaskAddedCancel();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
            if (etHeader.length() == 0) {
                positiveButton.setEnabled(false);
                tilHeader.setError(getResources().getString(R.string.dialog_error_empty_title));
            }

            positiveButton.setOnClickListener(v -> {
                if (validateDate(calendar)) {
                    task.setTitle(etHeader.getText().toString());
                    if (etContent.length() != 0) {
                        task.setContent(etContent.getText().toString());
                    }
                    task.setDate(calendar.getTimeInMillis());
                    task.setStatus(Task.STATUS_CURRENT);
                    if (isEditing) {
                        addingTaskListener.onTaskEdited(task);
                    } else {
                        addingTaskListener.onTaskAdded(task);
                    }
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                    dialog.dismiss();
                }
            });

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
        });

        return alertDialog;
    }

    private void fillDialogWithValues(Task task) {
        etHeader.setText(task.getTitle());
        etContent.setText(task.getContent());
        if (task.getDate() == 0) {
            etDate.setText(DateUtils.getDate(new Date().getTime()));
            etTime.setText(null);
        } else {
            etDate.setText(DateUtils.getDate(task.getDate()));
            etTime.setText(DateUtils.getTime(task.getDate()));
        }
        spPriority.setSelection(task.getPriority());
    }

    private Task initValues() {
        Bundle args = getArguments();
        if (args != null) {
            isEditing = true;
            String title = args.getString("title");
            String content = args.getString("content");
            long date = args.getLong("date", 0);
            long timeStamp = args.getLong("time_stamp", 0);
            int priority = args.getInt("priority", 0);
            int status = args.getInt("status", 0);

            final Task task = new Task();
            task.setTitle(title);
            task.setContent(content);
            task.setPriority(priority);
            task.setDate(date);
            task.setTimeStamp(timeStamp);
            task.setStatus(status);

            return task;
        } else
            return new Task();
    }

    private boolean validateDate(Calendar calendar) {
        boolean isError = false;
        if (etDate.length() == 0) {
            isError = true;
            Toast.makeText(getActivity(), R.string.snackbar_empty_date_error, Toast.LENGTH_LONG).show();
            etDate.setError(null);
        }
        int instanceDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (calendar.get(Calendar.DAY_OF_YEAR) < instanceDay) {
            isError = true;
            Toast.makeText(getActivity(), R.string.snackbar_date_error, Toast.LENGTH_LONG).show();
            etDate.setError(null);
        }
        return !isError;
    }
}
