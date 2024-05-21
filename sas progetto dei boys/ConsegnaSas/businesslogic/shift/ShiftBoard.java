package businesslogic.shift;

import javafx.collections.ObservableList;
import jdk.jfr.Event;
import main.businesslogic.CatERing;
import main.businesslogic.event.EventInfo;
import main.businesslogic.event.ServiceInfo;
import main.businesslogic.recipe.Recipe;
import main.businesslogic.shift.Group;
import main.businesslogic.shift.Shift;
import main.businesslogic.shift.ShiftBoard;
import main.businesslogic.task.Task;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.security.Provider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ShiftBoard {
    private ServiceInfo service;
    private EventInfo event;
    private ArrayList<Shift> shifts;
    private ArrayList<Group> groups = new ArrayList<>(); //Non implementato

    public ShiftBoard(ServiceInfo service, EventInfo event){
        this.service = service;
        this.event = event;
        this.shifts = new ArrayList<>();
    }

    public ServiceInfo getService() {
        return service;
    }
    public EventInfo getEvent() {
        return event;
    }
    public ArrayList<Shift> getShifts() {
        return shifts;
    }
    public void setService(ServiceInfo service) {
        this.service = service;
    }
    public void setEvent(EventInfo event) {
        this.event = event;
    }
    public void setShifts(ArrayList<Shift> shifts) {
        this.shifts = shifts;
    }



    public static ShiftBoard loadShiftBoard(ServiceInfo service, EventInfo event){
        ShiftBoard myShiftBoard = new ShiftBoard(service,event);

        String getShiftBoard = "SELECT * FROM  shiftboards WHERE id_service="+service.getId()+" AND id_event="+event.getId()+";";
        //System.out.println("Service "+service.getId()+" event "+event.getId());
        PersistenceManager.executeQuery(getShiftBoard, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                do{
                    int id_shift = rs.getInt("id_shift");
                    //System.out.println(id_shift);

                    String getShifts = "SELECT * FROM  shifts WHERE id_shift="+id_shift+";";

                    PersistenceManager.executeQuery(getShifts, new ResultHandler() {
                        @Override
                        public void handle(ResultSet rs) throws SQLException {
                            do{
                                Shift myShift = new Shift();
                                myShift.setId_shift(rs.getInt("id_shift"));
                                myShift.setExpirationDate(rs.getDate("expiration_date").toLocalDate());
                                myShift.setPreparationPlace(rs.getString("preparation_place"));
                                myShift.setStartTime(rs.getTime("start_time"));
                                myShift.setEndTime(rs.getTime("end_time"));
                                myShift.setRecurrence(rs.getBoolean("recurrence"));
                                myShift.setStaffLimit(rs.getInt("staff_limit"));
                                //Il current staff è fittizio
                                myShift.fakeCookLoader();
                                myShift.setCurrentStaff(myShift.getMyCooks().size());

                                if(rs.getTimestamp("end_date")!=null)
                                    myShift.setEndDate(rs.getTimestamp("end_date").toLocalDateTime().toLocalDate());
                                else
                                    myShift.setEndDate(null);

                                myShiftBoard.getShifts().add(myShift);
                            }while(rs.next());
                        }
                    });

                }while(rs.next());
            }
        });
        return myShiftBoard;
    }

    @Override
    public String toString() {
        return "ShiftBoard{" +
                "service=" + service +
                ", event=" + event +
                ", shifts=" + shifts +
                ", groups=" + groups +
                '}';
    }
}
