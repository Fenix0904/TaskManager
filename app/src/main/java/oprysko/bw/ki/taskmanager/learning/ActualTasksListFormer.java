package oprysko.bw.ki.taskmanager.learning;


import java.util.ArrayList;
import java.util.List;

import oprysko.bw.ki.taskmanager.database.DBHelper;
import oprysko.bw.ki.taskmanager.model.Task;

public class ActualTasksListFormer {

    private List<Task> tasks;
    private DBHelper dbHelper;

    public List<Task> generateActualTasksList() {
        // TODO
        return new ArrayList<>();
    }

    private Task calculateContextConformity(Task task) {
        // TODO
        return task;
    }

    private double calculateContextInfluence(int contextConformity, int userPriority) {
        // TODO
        return contextConformity + userPriority;
    }

    private List<Task> sortTasksByDynamicPriority(List<Task> tasks) {
        // TODO
        return tasks;
    }
}
