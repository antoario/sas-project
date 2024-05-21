package main.businesslogic.shift;

import businesslogic.CatERing;
import businesslogic.ShiftException;
import businesslogic.TaskException;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.shift.KitchenShift;
import businesslogic.shift.Shift;
import businesslogic.shift.ShiftBoard;
import businesslogic.task.TaskEventReceiver;
import businesslogic.user.User;

import java.security.Provider;
import java.util.ArrayList;

public class ShiftManager {
    private ArrayList<TaskEventReceiver> eventReceivers;
    private Shift currentShift;

    public void fakeShiftLoader(int id_shift){
        this.currentShift = KitchenShift.loadShift(id_shift);
    }

    public Shift getCurrentShift() {
        return currentShift;
    }

    public ShiftBoard consultShiftBoard(ServiceInfo service, EventInfo event) throws UseCaseLogicException, ShiftException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef()) {
            throw new UseCaseLogicException();
        }
        if(!service.isAssociated(user)){
            throw new ShiftException();
        }

        return ShiftBoard.loadShiftBoard(service,event);
    }

}
