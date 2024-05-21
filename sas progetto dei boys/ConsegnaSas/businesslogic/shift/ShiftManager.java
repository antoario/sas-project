package businesslogic.shift;

import java.security.Provider;
import java.util.ArrayList;

import main.businesslogic.CatERing;
import main.businesslogic.ShiftException;
import main.businesslogic.TaskException;
import main.businesslogic.UseCaseLogicException;
import main.businesslogic.event.EventInfo;
import main.businesslogic.event.ServiceInfo;
import main.businesslogic.shift.KitchenShift;
import main.businesslogic.shift.Shift;
import main.businesslogic.shift.ShiftBoard;
import main.businesslogic.task.TaskEventReceiver;
import main.businesslogic.user.User;

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
