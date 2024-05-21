package businesslogic.shift;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class KitchenShift extends Shift{
    private int id_kitchen;
    private LocalDate perfDate;
    private boolean kitchenFull;

    public KitchenShift(){
        super();
        this.id_kitchen = 0;
        this.perfDate = LocalDate.of(2021, 8, 1);
        this.kitchenFull = false;
    }

    public LocalDate getPerfDate() {
        return perfDate;
    }

    public void setId_kitchen(int id_kitchen) {
        this.id_kitchen = id_kitchen;
    }

    public void setPerfDate(LocalDate perfDate) {
        this.perfDate = perfDate;
    }

    public void setKitchenFull(boolean kitchenFull) {
        this.kitchenFull = kitchenFull;
    }

    public static KitchenShift loadShift(int id) {
        KitchenShift s = new KitchenShift();
        String kitchenShiftQuery = "SELECT * FROM catering.kitchenShifts WHERE id_kitchen_shift='"+id+"'";
        PersistenceManager.executeQuery(kitchenShiftQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                s.id_kitchen = rs.getInt("id_kitchen_shift");
                s.perfDate = rs.getTimestamp("perf_date").toLocalDateTime().toLocalDate();
                s.kitchenFull = rs.getBoolean("kitchen_full");
                s.id_shift = rs.getInt("id_shift");
            }
        });

        String shiftQuery = "SELECT * FROM Shifts WHERE id_shift='"+s.id_shift+"'";
        PersistenceManager.executeQuery(shiftQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                s.id_shift = rs.getInt("id_shift");
                s.expirationDate = rs.getTimestamp("expiration_date").toLocalDateTime().toLocalDate();
                s.preparationPlace = rs.getString("preparation_place");
                s.startTime = rs.getTime("start_time");
                s.endTime = rs.getTime("end_time");
                s.recurrence = rs.getBoolean("recurrence");
                s.staffLimit = rs.getInt("staff_limit");
                s.currentStaff = rs.getInt("current_staff");
                if(rs.getTimestamp("end_date")!=null)
                    s.endDate = rs.getTimestamp("end_date").toLocalDateTime().toLocalDate();
                else
                    s.endDate = null;
            }
        });
        return s;
    }

    @Override
    public String toString() {
        return "\t\tTurno numero " + id_shift + "\n" +
                "\t\t\tscadenza per modificare disponibilità: " + expirationDate+"\n"+
                "\t\t\tluogo preparazione: " + preparationPlace+"\n"+
                "\t\t\tdurata: " + startTime+ " - "+endTime+"\n"+
                "\t\t\tmassimo numero di cuochi: " + staffLimit+"\n"+
                "\t\t\tnumero di cuochi attualmente disponibili: " + currentStaff+"\n"+
                "\t\t\tdata di fine ricorrenza: " + endDate+"\n"+
                "\t\t\tcuochi disponibili per il turno: " + myCooks+"\n" +
                "\t\t\tdata svolgimento turno: " + perfDate+"\n" +
                "\t\t\tcucina al completo: " + (kitchenFull?"sì":"no");
    }
}
