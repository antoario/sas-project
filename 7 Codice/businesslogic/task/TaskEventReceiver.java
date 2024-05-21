package businesslogic.task;

import businesslogic.shift.Cook;
import businesslogic.shift.Shift;

public interface TaskEventReceiver {
    public void updateSummarySheetCreated(SummarySheet sumSheet);
    public void updateAddedTask(Task task);
    public void updateModifyTask(Task task);
    public void updateRegCompletedTask(Task task);
    public void updateDeletedAssignment(Task task, Cook cook, Shift shift);
    public void updateAssignTask(Task task);
}