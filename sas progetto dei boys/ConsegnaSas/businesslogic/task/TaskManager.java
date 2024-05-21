package businesslogic.task;

import java.util.ArrayList;

import main.businesslogic.CatERing;
import main.businesslogic.TaskException;
import main.businesslogic.UseCaseLogicException;
import main.businesslogic.event.EventInfo;
import main.businesslogic.event.ServiceInfo;
import main.businesslogic.menu.Menu;
import main.businesslogic.menu.MenuEventReceiver;
import main.businesslogic.recipe.Recipe;
import main.businesslogic.shift.Cook;
import main.businesslogic.shift.Shift;
import main.businesslogic.task.SummarySheet;
import main.businesslogic.task.Task;
import main.businesslogic.task.TaskEventReceiver;
import main.businesslogic.user.User;

public class TaskManager {
    private ArrayList<TaskEventReceiver> eventReceivers;
    SummarySheet currentSumSheet = null;

    public TaskManager(){
        eventReceivers = new ArrayList<>();
    }
    public void addEventReceiver(TaskEventReceiver ter) {
        this.eventReceivers.add(ter);
    }

    public void setCurrentSumSheet(SummarySheet currentSumSheet) {
        this.currentSumSheet = currentSumSheet;
    }

    public SummarySheet getCurrentSumSheet(){
        return this.currentSumSheet;
    }

    public SummarySheet generateSummarySheet(ServiceInfo service, EventInfo event) throws UseCaseLogicException,TaskException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }

        if(!service.isAssociated(user)){
            throw new TaskException();
        }

        SummarySheet summarySheet = new SummarySheet();
        this.setCurrentSumSheet(summarySheet);
        currentSumSheet.setReferredService(service);
        currentSumSheet.setReferredEvent(event);

        notifySummarySheetCreated(summarySheet);

        return summarySheet;
    }


    public SummarySheet openSummarySheet(ServiceInfo service, EventInfo event) throws UseCaseLogicException, TaskException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(!service.isAssociated(user)){
            throw new TaskException();
        }

        currentSumSheet = SummarySheet.loadSumSheet(service, event);
        return currentSumSheet;

    }

    public Task addTask(Recipe kitchenTask) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();

        if(!user.isChef()){
            throw new UseCaseLogicException();
        }

       Task task = currentSumSheet.addTask(kitchenTask,currentSumSheet.getMyTask().size()+1);

        notifyAddedTask(task);
        return task;
    }

    //Non sono presenti tutti gli overloading possibili
    //assignTask(kitchenTask, shift?, cook?,quantity?, time?, portions?)
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity, int time, int portions) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        Task task = null;
        try {
            task = currentSumSheet.assignTask(kitchenTask,shift,cook,quantity,time,portions);
        } catch (TaskException e) {
            e.printStackTrace();
        }

        notifyAssignTask(task);
        return task;
    }
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity, int time) throws UseCaseLogicException {
        return assignTask(kitchenTask,shift,cook,quantity,time,-1);
    }
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity) throws UseCaseLogicException {
        return assignTask(kitchenTask,shift,cook,quantity,-1,-1);
    }
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook) throws UseCaseLogicException {
        return assignTask(kitchenTask,shift,cook,null,-1,-1);
    }
    public Task assignTask(Recipe kitchenTask, Shift shift) throws UseCaseLogicException {
        return assignTask(kitchenTask,shift,null,null,-1,-1);
    }

    public Task modifyTask(Recipe kitchenTask, String quantity, int time, int portions) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        Task task = null;
        try {
            task = currentSumSheet.modifyTask(kitchenTask,quantity,time,portions);
        } catch (TaskException e) {
            e.printStackTrace();
        }

        notifyModifyTask(task);
        return task;
    }
    public Task modifyTask(Recipe kitchenTask, String quantity, int time) throws UseCaseLogicException {
        return modifyTask(kitchenTask,quantity,time,-1);
    }
    public Task modifyTask(Recipe kitchenTask, String quantity) throws UseCaseLogicException {
        return modifyTask(kitchenTask,quantity,-1,-1);
    }

    public Task regCompletedTask(Task task) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        notifyRegCompletedTask(task);
        return currentSumSheet.regCompletedTask(task);
    }


    public Task deleteAssignment(Task task, Cook cook, Shift shift) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        Task newTask = currentSumSheet.deleteAssignment(task,cook,shift);
        notifyDeletedAssignment(newTask,cook,shift);
        return newTask;
    }
    public Task deleteAssignment(Task task, Cook cook) throws UseCaseLogicException {
        return deleteAssignment(task,cook,null);
    }
    public Task deleteAssignment(Task task, Shift shift) throws UseCaseLogicException {
        return deleteAssignment(task,null,shift);
    }


    private void notifySummarySheetCreated(SummarySheet sumSheet) {
        for (TaskEventReceiver er: this.eventReceivers) {
            er.updateSummarySheetCreated(sumSheet);
        }
    }
    private void notifyAddedTask(Task task) {
       for (TaskEventReceiver er: this.eventReceivers) {
            er.updateAddedTask(task);
        }
    }
    private void notifyAssignTask(Task task){
        for (TaskEventReceiver er: this.eventReceivers) {
            er.updateAssignTask(task);
        }
    }
    private void notifyModifyTask(Task task){
        for (TaskEventReceiver er: this.eventReceivers) {
            er.updateModifyTask(task);
        }
    }
    private void notifyRegCompletedTask(Task task) {
        for (TaskEventReceiver er: this.eventReceivers) {
            er.updateRegCompletedTask(task);
        }
    }
    private void notifyDeletedAssignment(Task task,Cook cook,Shift shift){
        for (TaskEventReceiver er: this.eventReceivers) {
            er.updateDeletedAssignment(task,cook,shift);
        }
    }





}
