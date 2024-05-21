package catering.businesslogic.shift;

import javafx.collections.ObservableList;

public class ShiftManager {
    ObservableList<Shift> shiftBoard;

    public ObservableList<Shift> getShiftBoard(int serviceId){
        return Shift.loadShiftByServiceId(serviceId);
    }

}
