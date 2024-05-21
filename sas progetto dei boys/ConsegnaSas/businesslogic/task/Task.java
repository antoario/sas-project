package businesslogic.task;

import businesslogic.TaskException;
import businesslogic.menu.Menu;
import businesslogic.shift.KitchenShift;
import businesslogic.shift.Shift;
import businesslogic.shift.Cook;
import businesslogic.recipe.Recipe;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private int id_task;
    private int same_task;
    private int id_summary_sheet;
    private int portions;
    private boolean insert=false;
    private String quantity;
    private int estimatedTime;
    private boolean completed;
    private Recipe kitchenTask;
    private ArrayList<Shift> involvedShift;
    private ArrayList<Cook> involvedCooks;

    public Task(){
        this.id_task = 0;
        this.same_task = -1;
        this.id_summary_sheet = -1;
        this.kitchenTask = null;
        this.completed = false;
        this.involvedShift = new ArrayList<>();
        this.involvedCooks = new ArrayList<>();
        this.portions=0;
        this.quantity="";
        this.estimatedTime=0;
    }
    public Task(Recipe kitchenTask,int id_summary_sheet,int same_task){
        this.id_task = 0;
        this.same_task = same_task;
        this.id_summary_sheet = id_summary_sheet;
        this.kitchenTask = kitchenTask;
        this.completed = false;
        this.involvedShift = new ArrayList<>();
        this.involvedCooks = new ArrayList<>();
        this.portions=0;
        this.quantity=""; //es. 10 teglie
        this.estimatedTime=0;
    }

    @Override
    public boolean equals(Object obj) {
        Task task = (Task) obj;
        return id_task == task.id_task && same_task == task.same_task && id_summary_sheet == task.id_summary_sheet &&
                portions == task.portions && insert == task.insert && estimatedTime == task.estimatedTime &&
                completed == task.completed && Objects.equals(quantity, task.quantity) && Objects.equals(kitchenTask, task.kitchenTask) &&
                Objects.equals(involvedShift, task.involvedShift) && Objects.equals(involvedCooks, task.involvedCooks);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static void saveNewTask(Task task){
        String taskInsert = "INSERT INTO catering.tasks(portions,quantity,estimated_time,completed,id_cook,id_shift,id_recipe," +
                "id_summary_sheet,same_task) VALUES(?,?,?,?,?,?,?,?,?);";

        int[] result = PersistenceManager.executeBatchUpdate(taskInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1,task.getPortions());
                ps.setString(2, task.getQuantity());
                ps.setInt(3,task.getEstimatedTime());
                ps.setBoolean(4,task.isCompleted());
                ps.setInt(5,-1);
                ps.setInt(6,-1);
                ps.setInt(7,task.getKitchenTask().getId());
                ps.setInt(8,task.getSummarySheetId());
                ps.setInt(9,task.getSame_task());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // should be only one
                if (count == 0) {
                    task.id_task = rs.getInt(1);
                }
            }
        });
    }
    public static void updateAssignTask(Task task) {
        String getUnitializedTask = "SELECT * FROM catering.tasks WHERE id_cook=-1 AND id_shift=-1 AND id_task=" + task.getIdTask()+";";
        String taskUpdate = "UPDATE catering.tasks SET portions = ?, quantity = ?, estimated_time = ?,"+
                "completed=?,id_cook=?,id_shift=?,id_recipe=?,id_summary_sheet=? WHERE id_task=?;";
        String insertTask = "INSERT INTO catering.tasks(portions,quantity,estimated_time,completed,id_cook,id_shift,id_recipe,id_summary_sheet,same_task)" +
                "VALUES(?,?,?,?,?,?,?,?,?);";

        if(!task.getInsert()){
            PersistenceManager.executeQuery(getUnitializedTask, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                        int[] result = PersistenceManager.executeBatchUpdate(taskUpdate, 1, new BatchUpdateHandler() {
                            @Override
                            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                                ps.setInt(1,task.getPortions());
                                ps.setString(2, task.getQuantity());
                                ps.setInt(3,task.getEstimatedTime());
                                ps.setBoolean(4,task.isCompleted());
                                if(task.getInvolvedCooks().size()>0){
                                    ps.setInt(5,task.getInvolvedCooks().get(0).getId()); //cook
                                }else{
                                    ps.setInt(5,-1);
                                }
                                if(task.getInvolvedShift().size()>0){
                                    ps.setInt(6,task.getInvolvedShift().get(0).getId()); //shift
                                }else{
                                    ps.setInt(6,-1);
                                }
                                ps.setInt(7,task.getKitchenTask().getId());
                                ps.setInt(8,task.getSummarySheetId());
                                ps.setInt(9,task.getIdTask());
                            }
                            @Override
                            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

                            }
                        });
                }
            });
        }else{
            int[] result = PersistenceManager.executeBatchUpdate(insertTask, 1, new BatchUpdateHandler() {
                @Override
                public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {

                    ps.setInt(1,task.getPortions());
                    ps.setString(2, task.getQuantity());
                    ps.setInt(3,task.getEstimatedTime());
                    ps.setBoolean(4,task.isCompleted());
                    int involvedCooksSize = task.getInvolvedCooks().size();
                    int involvedShiftSize = task.getInvolvedShift().size();
                    if(involvedCooksSize>0){
                        ps.setInt(5,task.getInvolvedCooks().get(involvedCooksSize-1).getId()); //cook
                    }else{
                        ps.setInt(5,-1);
                    }
                    if(involvedShiftSize>0){
                        ps.setInt(6,task.getInvolvedShift().get(involvedShiftSize-1).getId()); //shift
                    }else{
                        ps.setInt(6,-1);
                    }
                    ps.setInt(7,task.getKitchenTask().getId());
                    ps.setInt(8,task.getSummarySheetId());
                    ps.setInt(9,task.getSame_task());
                }
                @Override
                public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

                }
            });
        }

    }
    public static void updateModifyTask(Task task) {
        String taskUpdate = "UPDATE catering.tasks SET portions = ?, quantity = ?, estimated_time = ?,"+
                "completed=?,id_recipe=?,id_summary_sheet=? WHERE same_task=?;";

        int[] result = PersistenceManager.executeBatchUpdate(taskUpdate, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1,task.getPortions());
                ps.setString(2, task.getQuantity());
                ps.setInt(3,task.getEstimatedTime());
                ps.setBoolean(4,task.isCompleted());
                ps.setInt(5,task.getKitchenTask().getId());
                ps.setInt(6,task.getSummarySheetId());
                ps.setInt(7,task.getSame_task());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {

            }
        });
    }
    public static void updateRegCompletedTask(Task task){
        String taskUpdate = "UPDATE catering.tasks SET completed = true WHERE same_task=?;";
        int[] result = PersistenceManager.executeBatchUpdate(taskUpdate, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1,task.getSame_task());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });

    }

    public static void updateDeletedAssignment(Task task,Cook cook, Shift shift){
        if(cook!=null && shift!=null){
            String taskUpdateBoth = "UPDATE catering.tasks SET id_cook = -1,id_shift=-1 " +
                    "WHERE id_cook="+ cook.getId() + " AND id_shift="+ shift.getId()+ " AND same_task="+task.getSame_task()+";";
            PersistenceManager.executeUpdate(taskUpdateBoth);
        }else{
            if(cook!=null){
                String taskUpdateCook = "UPDATE catering.tasks SET id_cook = -1 WHERE " +
                        "id_cook="+ cook.getId() +" AND same_task="+task.getSame_task()+";";
                PersistenceManager.executeUpdate(taskUpdateCook);
            }
            if(shift!=null){
                String taskUpdateShift = "UPDATE catering.tasks SET id_shift=-1 WHERE " +
                        "id_shift="+shift.getId()+" AND same_task="+task.getSame_task()+";";
                PersistenceManager.executeUpdate(taskUpdateShift);
            }
        }
    }



    public String getKitchenTaskName(){ return kitchenTask.getName();}
    public int getSummarySheetId(){ return id_summary_sheet;}
    public int getKitchenTaskId(){ return kitchenTask.getId();}
    public int getPortions() {
        return portions;
    }
    public String getQuantity() {
        return quantity;
    }
    public int getEstimatedTime() {
        return estimatedTime;
    }
    public int getSame_task() {
        return same_task;
    }
    public boolean getInsert(){
        return insert;
    }
    public boolean isInsert() {
        return insert;
    }
    public void setInsert(boolean insert) {
        this.insert = insert;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public boolean isCompleted() {
        return completed;
    }
    public Recipe getKitchenTask() {
        return kitchenTask;
    }
    public String getInvolvedCookNames(){
        String ret="";
        for(Cook cook:involvedCooks)
            ret+= cook.getName() + " ";
        return ret;
    }
    public int getIdTask(){ return  id_task;}
    public ArrayList<Shift> getInvolvedShift() {
        return involvedShift;
    }
    public ArrayList<Cook> getInvolvedCooks() {
        return involvedCooks;
    }
    public void setPortions(int portions) {
        this.portions = portions;
    }
    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public void setId_summary_sheet(int id_summary_sheet) {
        this.id_summary_sheet = id_summary_sheet;
    }
    public void setSame_task(int same_task) {
        this.same_task = same_task;
    }
    public void setKitchenTask(Recipe kitchenTask) {
        this.kitchenTask = kitchenTask;
    }


    public Task assignTask(Recipe kitchenTask, Shift shift, Cook cook, String quantity, int time, int portions) throws TaskException {
        this.kitchenTask = kitchenTask;
        if(shift!=null && (shift instanceof KitchenShift) &&
                ((KitchenShift) shift).getPerfDate().isAfter(LocalDate.now())){
            if(involvedShift != null && !involvedShift.contains(shift)){
                this.involvedShift.add(shift);
                if(involvedShift.size()>1)
                    insert=true;
            }

        }
        else{
            throw new TaskException();
        }


        if(cook!=null && shift.getMyCooks().contains(cook)){
            this.involvedCooks.add(cook);
            if(involvedCooks.size()>1)
                insert=true;
        }
        else{
            throw new TaskException();
        }

        if(quantity!=null)
            this.quantity=quantity;
        if(time!=-1)
            this.estimatedTime = time;
        if(portions!=-1)
            this.portions = portions;

        return this;
    }
    public Task modifyTask(Recipe kitchenTask, String quantity, int time, int portions) throws TaskException {
        this.kitchenTask = kitchenTask;
        if(quantity!=null)
            this.quantity=quantity;
        if(time!=-1)
            this.estimatedTime = time;
        if(portions!=-1)
            this.portions = portions;

        return this;
    }
    public Task regCompletedTask(){
        this.completed=true;
        return this;
    }
    public Task deleteAssignment(Cook cook, Shift shift){
        int counter=0;
        if (cook!=null){
            this.involvedCooks.remove(cook);
        }
        if (shift != null) {
            this.involvedShift.remove(shift);
        }
        return this;
    }

    @Override
    public String toString() {
        String ret="";
        ret+= "\tCompito numero "+ same_task + ":\n"+
                "\t\tporzioni da preparare: " + portions + "\n"+
                "\t\tquantità da preparare: " + quantity + "\n"+
                "\t\ttempo stimato: " + estimatedTime + " ore\n"+
                "\t\tcompletato: " + (completed?"si":"no") + "\n"+
                "\t\tportata: " + kitchenTask.getName() + "\n"+
                "\t\tpreparato nel turno: " + involvedShift + "\n"+
                "\t\tdai cuochi: " + getInvolvedCookNames() + "\n" +
                "\t\tdettagli nel foglio riepilogativo: " + id_summary_sheet + "\n";
        return ret;
    }


    @Override
    public int compareTo(Task task) {
        return this.portions-task.getPortions();
    }
}
