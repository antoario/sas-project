package catering;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.kitchentask.KitchenTask;
import catering.businesslogic.kitchentask.SummarySheet;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import javafx.collections.ObservableList;

import java.util.Optional;

public class TestKitchenTask {
    public static void main(String[] args) {
        try {

            PersistenceManager.resetDb();

            System.out.println("TEST FAKE LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            System.out.println("\nTEST GENERATE SUMMARY SHEET");
            CatERing.getInstance().getMenuManager().getAllMenus();
            User cook = User.loadUserById(4);
            ObservableList<EventInfo> events = CatERing.getInstance().getEventManager().getEventInfo();
            EventInfo event = events.get(0);
            ServiceInfo serviceInfo = event.getServices().get(0);

            SummarySheet sm = CatERing.getInstance().getKitchenTaskManager().generateSummarySheet(event, serviceInfo);
            System.out.println(sm.testString());

            System.out.println("\nTEST INSERT TASK");
            ObservableList<Recipe> recipes = CatERing.getInstance().getRecipeManager().getRecipes();
            CatERing.getInstance().getKitchenTaskManager().insertTask(recipes.get(0));
            System.out.println(sm.testString());

            System.out.println("\nTEST MOVE TASK");
            KitchenTask task0 = CatERing.getInstance().getKitchenTaskManager().getSummarySheet(sm).getTasks().get(0);
            CatERing.getInstance().getKitchenTaskManager().moveTask(task0, 5);
            System.out.println(sm.testString());

            System.out.println("\nTEST GET SHIFT BOARD");
            ObservableList<Shift> shiftsBoard = CatERing.getInstance().getKitchenTaskManager()
                    .getShiftBoard(serviceInfo.getId());
            System.out.println(shiftsBoard);

            System.out.println("\nTEST ASSIGN KITCHEN TASK");
            CatERing.getInstance().getKitchenTaskManager().assignKitchenTask(task0,
                    Optional.ofNullable(shiftsBoard.get(1)), Optional.ofNullable(cook), Optional.ofNullable(20),
                    Optional.of("1kg"));
            KitchenTask task1 = CatERing.getInstance().getKitchenTaskManager().getSummarySheet(sm).getTasks().get(1);
            CatERing.getInstance().getKitchenTaskManager().assignKitchenTask(task1,
                    Optional.ofNullable(shiftsBoard.get(2)), Optional.ofNullable(cook), Optional.ofNullable(30),
                    Optional.of("500g"));
            KitchenTask task2 = CatERing.getInstance().getKitchenTaskManager().getSummarySheet(sm).getTasks().get(3);
            CatERing.getInstance().getKitchenTaskManager().assignKitchenTask(task2,
                    Optional.ofNullable(null), Optional.ofNullable(cook), Optional.ofNullable(50),
                    Optional.of("1kg"));

            System.out.println(sm.testString());

        } catch (UseCaseLogicException e) {
            System.out.println(e.getMessage());
        }
    }
}
