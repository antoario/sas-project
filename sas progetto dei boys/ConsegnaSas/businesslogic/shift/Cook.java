package businesslogic.shift;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import main.businesslogic.shift.Cook;

public class Cook {
    private int id_cook;
    private String name;
    private int badge;

    public Cook(String name, int badge) {
        this.name = name;
        this.badge = badge;
    }
    public Cook(){
    }

    @Override
    public boolean equals(Object obj){
        Cook cook = (Cook) obj;
        return this.id_cook == cook.id_cook &&
                this.name.equalsIgnoreCase(cook.name) &&
                this.badge == cook.badge;
    }

    public String getName() {
        return name;
    }

    public int getBadge() {
        return badge;
    }

    public int getId(){ return id_cook;}

    public void setId_cook(int id_cook) {
        this.id_cook = id_cook;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public static ArrayList<Cook> loadCook() {
        ArrayList<Cook> cooks = new ArrayList<Cook>();
        String cookQuery = "SELECT * FROM Cooks";
        PersistenceManager.executeQuery(cookQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Cook c = new Cook();
                c.id_cook = rs.getInt("id_cook");
                c.name = rs.getString("name");
                c.badge = rs.getInt("badge");
                cooks.add(c);
            }
        });
        return cooks;
    }

    @Override
    public String toString() {
        return "\n\t\t\t\tnome: " + name + " badge: " + badge;
    }
}
