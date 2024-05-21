package businesslogic.event;

import javafx.collections.ObservableList;
import main.businesslogic.event.EventInfo;

import java.util.Random;

public class EventManager {
    private ObservableList<EventInfo> events;

    public EventManager(){
        this.events = getEventInfo();
    }
    public ObservableList<EventInfo> getEventInfo() {
        return EventInfo.loadAllEventInfo();
    }
    public EventInfo getFakeEvent(){
        Random rand = new Random();
        return events.get(rand.nextInt(events.size()));
    }
}
