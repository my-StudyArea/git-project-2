package lk.ijse.dep10.project2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class DashboardController {

    @FXML
    private Button btnManageCustormers;

    @FXML
    private Button btnManageEmployee;

    @FXML
    private Button btnManageStudent;

    @FXML
    private Button btnManageTeacers;

    @FXML
    void btnManageCustormersOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CustomerView.fxml"))));
        stage.centerOnScreen();
        stage.show();
        stage.setTitle("Manage Customer");
        //stage.setMaximized(true);
    }

    @FXML
    void btnManageEmployee(ActionEvent event) {

    }

    @FXML
    void btnManageStudentOnAction(ActionEvent event) throws IOException {
        URL mainViewUrl = getClass().getResource("/view/Student.fxml");
        Scene mainViewScene = new Scene(FXMLLoader.load(mainViewUrl));
        Stage stage = (Stage) btnManageTeacers.getScene().getWindow();
        stage.setTitle("Student");
        stage.setScene(mainViewScene);
        stage.sizeToScene();
        stage.centerOnScreen();

    }

    @FXML
    void btnManageTeachersOnAction(ActionEvent event) {

    }

}
