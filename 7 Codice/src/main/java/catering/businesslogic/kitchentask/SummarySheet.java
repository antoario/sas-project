package catering.businesslogic.kitchentask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.user.User;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SummarySheet {
    private int id;
    private User owner;
    private ServiceInfo service;
    private ObservableList<KitchenTask> tasks;

    public SummarySheet(User user, ServiceInfo service) {
        this.id = 0;
        this.owner = user;
        this.service = service;
        this.tasks = FXCollections.observableArrayList();
    }

    public void addTask(KitchenTask t) {
        this.tasks.add(t);
    }

    public boolean isOwner(User ch) {
        return true;
    }

    public void moveTask(KitchenTask t, int pos) {
        tasks.remove(t);
        tasks.add(pos, t);
    }

    public void assignKitchenTask(KitchenTask t, Optional<Shift> s, Optional<User> c, Optional<Integer> time,
            Optional<String> qty) throws UseCaseLogicException {
        t.setToPrepare(true);
        t.setCompleted(false);

        if (time.isPresent())
            t.setEstimatedTime(time.get());

        if (qty.isPresent())
            t.setQuantity(qty.get());

        if (c.isPresent() && s.isPresent()) {
            if (c.get().isAvailable(s.get())) {
                t.setCook(c.get());
                t.setShift(s.get());
                c.get().addShift(s.get());
            } else
                throw new UseCaseLogicException("Error Cook not available in Shift");
        } else if (c.isPresent() && !s.isPresent()) {
            t.setCook(c.get());
        } else if (!c.isPresent() && s.isPresent()) {
            t.setShift(s.get());
        }

    }

    public void editTask(KitchenTask t, Optional<Integer> time, Optional<String> qty, Optional<Boolean> completed) {
        if (time.isPresent())
            t.setEstimatedTime(time.get());

        if (qty.isPresent())
            t.setQuantity(qty.get());

        if (completed.isPresent())
            t.setCompleted(completed.get());
    }

    public void deleteKitchenTask(KitchenTask task) {
        task.getCook().removeShift(task.getShift());
        this.tasks.remove(task);
    }

    public void cancelKitchenTask(KitchenTask task) {
        task.getCook().removeShift(task.getShift());
        task.setToPrepare(false);
    }

    public ObservableList<KitchenTask> getTasks() {
        return this.tasks;
    }

    public int getSummarySheetSize() {
        return this.tasks.size();
    }

    public User getOwner() {
        return this.owner;
    }

    public int getId() {
        return this.id;
    }

    public ServiceInfo getService() {
        return this.service;
    }

    public String testString() {
        String result = this.toString() + "\n";

        result += "Owner : " + this.owner.getUserName();

        result += "\n";

        for (KitchenTask t : tasks) {
            result += t.toString() + "\n";
        }

        return result;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static void saveNewSummarySheet(SummarySheet sm) {
        String summarySheetInsert = "INSERT INTO catering.SummarySheets (owner_id, service_id) VALUES (?, ?);";
        int[] result = PersistenceManager.executeBatchUpdate(summarySheetInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, sm.getOwner().getId());
                ps.setInt(2, sm.getService().getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if (count == 0) {
                    sm.id = rs.getInt(1);
                }
            }
        });
        if (result[0] > 0) {
            if (sm.tasks.size() > 0) {
                KitchenTask.saveAllNewTasks(sm.id, sm.tasks);
            }
        }
    }

    public static void saveTaskOrder(SummarySheet sm) {
        String upd = "UPDATE KitchenTasks SET position = ? WHERE id = ?";
        PersistenceManager.executeBatchUpdate(upd, sm.tasks.size(), new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, batchCount);
                ps.setInt(2, sm.tasks.get(batchCount).getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });
    }

    public static ObservableList<SummarySheet> loadAllSheets() {
        String query = "SELECT * FROM SummarySheets";
        ObservableList<SummarySheet> summarySheets = FXCollections.observableArrayList();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int ownerId = rs.getInt(2);
                int serviceId = rs.getInt(3);
                User owner = User.loadUserById(ownerId);
                ServiceInfo service = ServiceInfo.loadServiceById(serviceId);
                SummarySheet sm = new SummarySheet(owner, service);
                sm.id = rs.getInt(1);
                sm.tasks = KitchenTask.loadTasksOfSummarySheetById(sm.id);

                summarySheets.add(sm);
            }
        });
        return summarySheets;
    }

}
