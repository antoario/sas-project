package catering.businesslogic.kitchentask;

public interface KitchenTaskReceiver {
    
    public void updateSheetGenerated(SummarySheet sheet);
    public void updateKitchenTaskAdded(SummarySheet sheet, KitchenTask task);
    public void updateTasksRearranged(SummarySheet sheet);
    public void updateKitchenTaskAssigned(SummarySheet currentSummarySheet, KitchenTask task);
    public void updateKitchenTaskEdited(SummarySheet currentSummarySheet, KitchenTask task);
    public void updateTaskDeleted(KitchenTask task);
    public void updateTaskCanceled(KitchenTask task);
}