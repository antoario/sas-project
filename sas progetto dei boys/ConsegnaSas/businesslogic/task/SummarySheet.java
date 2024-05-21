package businesslogic.task;

import javafx.collections.ObservableList;
import main.businesslogic.CatERing;
import main.businesslogic.TaskException;
import main.businesslogic.event.EventInfo;
import main.businesslogic.event.EventManager;
import main.businesslogic.event.ServiceInfo;
import main.businesslogic.menu.MenuItem;
import main.businesslogic.menu.Section;
import main.businesslogic.recipe.Recipe;
import main.businesslogic.shift.Cook;
import main.businesslogic.shift.KitchenShift;
import main.businesslogic.shift.Shift;
import main.businesslogic.task.SummarySheet;
import main.businesslogic.task.Task;
import main.businesslogic.user.User;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;

public class SummarySheet {
    private int id;
    private ServiceInfo referredService = null;
    private EventInfo referredEvent = null;
    private ArrayList<Task> myTask = null;

    public SummarySheet(){
        id=0;
        myTask = new ArrayList<>();
    }
    public void setReferredService(ServiceInfo referredService) {
        this.referredService = referredService;
    }
    public void setReferredEvent(EventInfo referredEvent) {
        this.referredEvent = referredEvent;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ServiceInfo getReferredService() {
        return referredService;
    }
    public EventInfo getReferredEvent() {
        return referredEvent;
    }

    public ArrayList<Task> getMyTask() {
        return myTask;
    }

    public static void saveNewSummarySheet(SummarySheet sumSheet){
        String summarySheetsInsert = "INSERT INTO summarysheets(id_service,id_event) VALUES(?,?);";
        int[] result = PersistenceManager.executeBatchUpdate(summarySheetsInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1,sumSheet.getReferredService().getId());
                ps.setInt(2, sumSheet.getReferredEvent().getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    sumSheet.id = rs.getInt(1);
                }
            }
        });
    }

    @Override
    public String toString() {
        return "\tFoglio riepilogativo numero " + id + "\n" +
                "\t\triferito al servizio: " + referredService+ "\n" +
                "\t\tappartenente all'evento: " + referredEvent+ "\n" +
                "\t\tin cui sono registrarti i seguenti compiti: " + myTask+ "\n";
    }

    public Task addTask(Recipe kitchenTask,int same_task) {
        Task task = new Task(kitchenTask,id,same_task);
        task.setPortions(referredService.getParticipants());
        myTask.add(task);
        return task;
    }


    //Non sono presenti tutti gli overloading possibili
    //assignTask(kitchenTask, shift?, cook?,quantity?, time?, portions?)
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity, int time, int portions) throws TaskException {
        Task task = null;
        for(Task i_task: myTask){
            if(i_task.getKitchenTaskName().equalsIgnoreCase(kitchenTask.getName()) &&
                    i_task.getKitchenTaskId() == kitchenTask.getId())
                task = i_task;
        }
        if(task!=null)
            return task.assignTask(kitchenTask,shift,cook,quantity,time,portions);
        else
            throw new TaskException();
    }
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity, int time) throws TaskException {
        return assignTask(kitchenTask,shift,cook,quantity,time,-1);
    }
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity) throws TaskException {
        return assignTask(kitchenTask,shift,cook,quantity,-1,-1);
    }
    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook) throws TaskException {
        return assignTask(kitchenTask,shift,cook,null,-1,-1);
    }
    public Task assignTask(Recipe kitchenTask, Shift shift) throws TaskException {
        return assignTask(kitchenTask,shift,null,null,-1,-1);
    }

    //Non sono presenti tutti gli overloading possibili
    //modifyTask(kitchenTask, quantity?, time?, portions?)
    public Task modifyTask(Recipe kitchenTask, String quantity, int time, int portions) throws TaskException {
        Task task = null;
        for(Task i_task: myTask){
            if(i_task.getKitchenTaskName().equalsIgnoreCase(kitchenTask.getName()) &&
                    i_task.getKitchenTaskId() == kitchenTask.getId())
                task = i_task;
        }
        if(task!=null)
            return task.modifyTask(kitchenTask,quantity,time,portions);
        else
            throw new TaskException();
    }
    public Task modifyTask(Recipe kitchenTask, String quantity, int time) throws TaskException {
        return modifyTask(kitchenTask,quantity,time,-1);
    }
    public Task modifyTask(Recipe kitchenTask, String quantity) throws TaskException {
        return modifyTask(kitchenTask,quantity,-1,-1);
    }

    public Task regCompletedTask(Task task) {
        int myTaskIdx = myTask.indexOf(task);
        return myTask.get(myTaskIdx).regCompletedTask();
    }

    public Task deleteAssignment(Task task, Cook cook, Shift shift){
        int myTaskIdx = myTask.indexOf(task);
        return myTask.get(myTaskIdx).deleteAssignment(cook,shift);
    }

    public static SummarySheet loadSumSheet(ServiceInfo service, EventInfo event){

        SummarySheet sumSheet = new SummarySheet();
        String summarySheetGet = "SELECT * FROM summarysheets WHERE id_service="
                +service.getId()+" AND id_event="+event.getId()+";";


        PersistenceManager.executeQuery(summarySheetGet, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                sumSheet.setId(rs.getInt("id_summarysheets"));
                EventInfo eventToPass = null;
                ObservableList<EventInfo> e = CatERing.getInstance().getEventManager().getEventInfo();
                for(EventInfo myEvent: e){
                    if(myEvent.getId()==rs.getInt("id_event")){
                        eventToPass=myEvent;
                    }
                }
                ServiceInfo serviceToPass = null;
                ObservableList<ServiceInfo> serviceList = eventToPass.getServices();
                for(ServiceInfo myService: serviceList){
                    if(myService.getId()==rs.getInt("id_service")){
                        serviceToPass=myService;
                    }
                }
                sumSheet.setReferredEvent(eventToPass);
                sumSheet.setReferredService(serviceToPass);

                String myTasksId = "SELECT DISTINCT(same_task) FROM tasks";
                PersistenceManager.executeQuery(myTasksId, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        do{
                            String taskGet = "SELECT * FROM tasks WHERE same_task="+rs.getInt("same_task")+";";
                            PersistenceManager.executeQuery(taskGet, new ResultHandler() {
                                @Override
                                public void handle(ResultSet rs) throws SQLException {
                                    Task task = new Task();
                                    do{
                                        task.setPortions(rs.getInt("portions"));
                                        task.setQuantity(rs.getString("quantity"));
                                        task.setEstimatedTime(rs.getInt("estimated_time"));
                                        task.setCompleted(rs.getBoolean("completed"));
                                        task.setId_summary_sheet(rs.getInt("id_summary_sheet"));
                                        task.setSame_task(rs.getInt("same_task"));

                                        //set recipe
                                        String recipeGet = "SELECT * FROM recipes WHERE " +
                                                "id="+rs.getInt("id_recipe")+";";
                                        PersistenceManager.executeQuery(recipeGet, new ResultHandler() {
                                            @Override
                                            public void handle(ResultSet rs) throws SQLException {
                                                String name = rs.getString("name");
                                                Recipe recipe = new Recipe(name);
                                                recipe.setId(rs.getInt("id"));
                                                task.setKitchenTask(recipe);
                                            }
                                        });

                                        //set cook
                                        int id_cook = rs.getInt("id_cook");
                                        if(id_cook!=-1){
                                            String cookGet = "SELECT * FROM cooks WHERE " +
                                                    "id_cook="+id_cook+";";
                                            PersistenceManager.executeQuery(cookGet, new ResultHandler() {
                                                @Override
                                                public void handle(ResultSet rs) throws SQLException {
                                                    Cook cook = new Cook();
                                                    cook.setId_cook(rs.getInt("id_cook"));
                                                    cook.setName(rs.getString("name"));
                                                    cook.setBadge(rs.getInt("badge"));
                                                    if(!task.getInvolvedCooks().contains(cook))
                                                    task.getInvolvedCooks().add(cook);
                                                }
                                            });
                                        }

                                        //set shift
                                        int id_shift = rs.getInt("id_shift");
                                        if(id_shift!=-1){
                                            String shiftGet = "SELECT * FROM shifts WHERE " +
                                                    "id_shift="+id_shift+";";
                                            PersistenceManager.executeQuery(shiftGet, new ResultHandler() {
                                                @Override
                                                public void handle(ResultSet rs) throws SQLException {
                                                    Shift shift = new KitchenShift();
                                                    shift.setId_shift(rs.getInt("id_shift"));
                                                    shift.setExpirationDate(rs.getDate("expiration_date").toLocalDate());
                                                    shift.setPreparationPlace(rs.getString("preparation_place"));
                                                    shift.setStartTime(rs.getTime("start_time"));
                                                    shift.setEndTime(rs.getTime("end_time"));
                                                    shift.setRecurrence(rs.getBoolean("recurrence"));
                                                    shift.setStaffLimit(rs.getInt("staff_limit"));
                                                    //shift.setCurrentStaff(rs.getInt("current_staff"));
                                                    LocalDate end_date =null;
                                                    if(rs.getDate("end_date")==null){
                                                        shift.setEndDate(null);
                                                    }else{
                                                        shift.setEndDate(rs.getDate("end_date").toLocalDate());
                                                    }
                                                    shift.setMyCooks(Cook.loadCook());
                                                    shift.setCurrentStaff(shift.getMyCooks().size());

                                                    String kitchenShiftGet = "SELECT * FROM kitchenshifts WHERE id_shift=" + shift.getId()+";";
                                                    PersistenceManager.executeQuery(kitchenShiftGet, new ResultHandler() {
                                                        @Override
                                                        public void handle(ResultSet rs) throws SQLException {
                                                            ((KitchenShift)shift).setPerfDate(rs.getDate("perf_date").toLocalDate());
                                                            ((KitchenShift)shift).setKitchenFull(rs.getBoolean("kitchen_full"));
                                                            ((KitchenShift)shift).setId_kitchen(rs.getInt("id_shift"));
                                                        }
                                                    });
                                                    if(!task.getInvolvedShift().contains(shift))
                                                        task.getInvolvedShift().add(shift);
                                                }
                                            });
                                        }
                                    }while (rs.next());
                                    if(sumSheet.getMyTask()!=null && !sumSheet.getMyTask().contains(task))
                                        sumSheet.getMyTask().add(task);
                                }
                            });
                        }while (rs.next());
                    }
                });
            }
        });
        return sumSheet;
    }




}






