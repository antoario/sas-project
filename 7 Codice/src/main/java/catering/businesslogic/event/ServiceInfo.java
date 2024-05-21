package catering.businesslogic.event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import catering.businesslogic.menu.Menu;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceInfo implements EventItemInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu menu;

    public ServiceInfo(String name) {
        this.name = name;
    }

    public Menu getMenu(){
        return this.menu;
    }

    public void setMenu(Menu m){
        this.menu = m;
    }

    public int getId(){
        return this.id;
    }

    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp.";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, approved_menu_id, service_date, time_start, time_end, expected_participants " +
                "FROM Services WHERE event_id = " + event_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String s = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(s);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                serv.menu = rs.getInt("approved_menu_id") == 0 ? null : Menu.getMenuById(rs.getInt("approved_menu_id"));
                result.add(serv);
            }
        });
        return result;
    }

    public static ServiceInfo loadServiceById(int serviceId){
        String query = "SELECT name, approved_menu_id, service_date, time_start, time_end, expected_participants " +
                "FROM Services WHERE id = " + serviceId;

        AtomicReference<ServiceInfo> servRef = new AtomicReference<>();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String sName = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(sName);
                serv.id = serviceId;
                serv.date = rs.getDate("service_date");
                serv.timeStart = rs.getTime("time_start");
                serv.timeEnd = rs.getTime("time_end");
                serv.participants = rs.getInt("expected_participants");
                serv.menu = rs.getInt("approved_menu_id") == 0 ? null : Menu.getMenuById(rs.getInt("approved_menu_id"));
                servRef.set(serv);
            }
        });       
        return servRef.get();
    }
}
