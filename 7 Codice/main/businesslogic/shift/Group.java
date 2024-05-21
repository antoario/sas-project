package main.businesslogic.shift;

import java.util.ArrayList;

import businesslogic.shift.KitchenShift;

public class Group {
    private ArrayList<KitchenShift> myShiftGroup;
    public Group(){
        myShiftGroup = new ArrayList<>();
    }

    public ArrayList<KitchenShift> getMyShiftGroup() {
        return myShiftGroup;
    }

    public void setMyShiftGroup(ArrayList<KitchenShift> myShiftGroup) {
        this.myShiftGroup = myShiftGroup;
    }
}
