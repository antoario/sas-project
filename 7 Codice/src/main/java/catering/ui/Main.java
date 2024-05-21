package catering.ui;

import catering.businesslogic.CatERing;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import catering.ui.menu.MenuManagement;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main {

    @FXML
    private AnchorPane paneContainer;

    @FXML
    private FlowPane startPane;

    @FXML
    private Start startPaneController;

    private BorderPane menuManagementPane;
    private MenuManagement menuManagementPaneController;

    @FXML
    public void initialize() {
        startPaneController.setParent(this);

        URL fxmlUrl = getClass().getResource("/ui/menu/menu-management.fxml");

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(fxmlUrl));
        try {
            menuManagementPane = loader.load();
            menuManagementPaneController = loader.getController();
            menuManagementPaneController.setMainPaneController(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void startMenuManagement() {
        CatERing.getInstance().getUserManager().fakeLogin("Lidia");

        menuManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(menuManagementPane);
        AnchorPane.setTopAnchor(menuManagementPane, 0.0);
        AnchorPane.setBottomAnchor(menuManagementPane, 0.0);
        AnchorPane.setLeftAnchor(menuManagementPane, 0.0);
        AnchorPane.setRightAnchor(menuManagementPane, 0.0);
    }

    public void showStartPane() {
        startPaneController.initialize();
        paneContainer.getChildren().remove(menuManagementPane);
        paneContainer.getChildren().add(startPane);
    }
}
