package businesslogic;

import main.businesslogic.CatERing;
import main.businesslogic.event.EventManager;
import main.businesslogic.menu.Menu;
import main.businesslogic.menu.MenuManager;
import main.businesslogic.recipe.RecipeManager;
import main.businesslogic.shift.Shift;
import main.businesslogic.shift.ShiftManager;
import main.businesslogic.task.Task;
import main.businesslogic.task.TaskManager;
import main.businesslogic.user.UserManager;
import persistence.MenuPersistence;
import persistence.PersistenceManager;
import persistence.TaskPersistence;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private TaskManager taskMgr;
    private ShiftManager shiftMgr;

    private MenuPersistence menuPersistence;
    private TaskPersistence taskPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        taskMgr = new TaskManager();
        shiftMgr = new ShiftManager();
        menuPersistence = new MenuPersistence();
        menuMgr.addEventReceiver(menuPersistence);
        taskPersistence = new TaskPersistence();
        taskMgr.addEventReceiver(taskPersistence);
    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }

    public TaskManager getTaskManager(){ return taskMgr; }

    public ShiftManager getShiftManager(){ return shiftMgr;}
}
