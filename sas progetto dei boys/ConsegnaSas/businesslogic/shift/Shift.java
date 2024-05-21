package businesslogic.shift;

import businesslogic.event.ServiceInfo;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Shift {
    int id_shift;
    LocalDate expirationDate;
    String preparationPlace;
    Time startTime;
    Time endTime;
    boolean recurrence;
    int staffLimit;
    int currentStaff;
    LocalDate endDate;

    ArrayList<Cook> myCooks;

    public Shift(){
        this.id_shift = 0;
        this.expirationDate = LocalDate.of(2021, 6, 25);
        this.preparationPlace = "Giardini";
        this.startTime = Time.valueOf("10:00:00");
        this.endTime = Time.valueOf("13:00:00");
        this.recurrence = false;
        this.staffLimit = 50;
        this.currentStaff = 0;
        this.endDate = null;
        this.myCooks = new ArrayList<>();
    }
    
    public ArrayList<Cook> getMyCooks(){
        return myCooks;
    }
    public int getId(){return id_shift;}
    public void fakeCookLoader(){
        this.myCooks = Cook.loadCook();
        this.currentStaff = myCooks.size();
    }

    public void setId_shift(int id_shift) {
        this.id_shift = id_shift;
    }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    public void setPreparationPlace(String preparationPlace) {
        this.preparationPlace = preparationPlace;
    }
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
    public void setRecurrence(boolean recurrence) {
        this.recurrence = recurrence;
    }
    public void setStaffLimit(int staffLimit) {
        this.staffLimit = staffLimit;
    }
    public void setCurrentStaff(int currentStaff) {
        this.currentStaff = currentStaff;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public void setMyCooks(ArrayList<Cook> myCooks) {
        this.myCooks = myCooks;
    }

    @Override
    public boolean equals(Object obj) {
        Shift shift = (Shift) obj;
        return id_shift == shift.id_shift && recurrence == shift.recurrence && staffLimit == shift.staffLimit &&
                currentStaff == shift.currentStaff && expirationDate.equals(shift.expirationDate) &&
                preparationPlace.equals(shift.preparationPlace) && startTime.equals(shift.startTime) &&
                endTime.equals(shift.endTime) && myCooks.equals(shift.myCooks);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "\n\t\t\tTurno numero " + id_shift + "\n" +
                "\t\t\tscadenza per modificare disponibilità: " + expirationDate+"\n"+
                "\t\t\tluogo preparazione: " + preparationPlace+"\n"+
                "\t\t\tdurata: " + startTime+ " - "+endTime+"\n"+
                "\t\t\tmassimo numero di cuochi: " + staffLimit+"\n"+
                "\t\t\tnumero di cuochi attualmente disponibili: " + currentStaff+"\n"+
                "\t\t\tdata di fine ricorrenza: " + endDate+"\n"+
                "\t\t\tcuochi disponibili per il turno: " + myCooks+"\n";
    }
}
