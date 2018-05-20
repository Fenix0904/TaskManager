package oprysko.bw.ki.taskmanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import java.util.Calendar;

import oprysko.bw.ki.taskmanager.R;
import oprysko.bw.ki.taskmanager.Utils;
import oprysko.bw.ki.taskmanager.alarm.AlarmHelper;
import oprysko.bw.ki.taskmanager.model.Task;

public class AddingTaskDialogFragment extends DialogFragment {

    private TextInputLayout tilHeader;
    private EditText etHeader;
    private TextInputLayout tilContent;
    private EditText etContent;
    private TextInputLayout tilDate;
    private EditText etDate;
    private TextInputLayout tilTime;
    private EditText etTime;

    private AddingTaskListener addingTaskListener;

    public interface AddingTaskListener {
        void onTaskAdded(Task task);

        void onTaskAddedCancel();
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

        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);

        tilHeader.setHint(getResources().getString(R.string.hint_task_title));
        tilContent.setHint(getResources().getString(R.string.hint_task_content));
        tilDate.setHint(getResources().getString(R.string.hint_task_date));
        tilTime.setHint(getResources().getString(R.string.hint_task_time));

        builder.setView(container);

        final Task task = new Task();

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Task.PRIORITY_LEVELS);
        spPriority.setAdapter(priorityAdapter);

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

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length() == 0) {
                    etDate.setText(" ");
                }
                DatePickerDialog DatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker.show();
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

        builder.setPositiveButton(R.string.dialog_ok, null);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addingTaskListener.onTaskAddedCancel();
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if (etHeader.length() == 0) {
                    positiveButton.setEnabled(false);
                    tilHeader.setError(getResources().getString(R.string.dialog_error_empty_title));
                }

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validateDate(calendar)) {
                            task.setTitle(etHeader.getText().toString());
                            if (etContent.length() != 0) {
                                task.setContent(etContent.getText().toString());
                            }
                            task.setDate(calendar.getTimeInMillis());
                            task.setStatus(Task.STATUS_CURRENT);
                            addingTaskListener.onTaskAdded(task);
                            AlarmHelper alarmHelper = AlarmHelper.getInstance();
                            alarmHelper.setAlarm(task);
                            dialog.dismiss();
                        }
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
            }
        });

        return alertDialog;
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
