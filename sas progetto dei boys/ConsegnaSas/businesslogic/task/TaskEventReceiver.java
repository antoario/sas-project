package businesslogic.task;

import main.businesslogic.shift.Cook;
import main.businesslogic.shift.Shift;
import main.businesslogic.task.SummarySheet;
import main.businesslogic.task.Task;

public interface TaskEventReceiver {
    public void updateSummarySheetCreated(SummarySheet sumSheet);
    public void updateAddedTask(Task task);
    public void updateModifyTask(Task task);
    public void updateRegCompletedTask(Task task);
    public void updateDeletedAssignment(Task task, Cook cook, Shift shift);
    public void updateAssignTask(Task task);
}