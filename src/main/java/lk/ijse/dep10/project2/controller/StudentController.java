package lk.ijse.dep10.project2.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep10.project2.db.DBConnection;
import lk.ijse.dep10.project2.model.Student;

import java.sql.*;
import java.time.LocalDate;

public class StudentController {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewStudent;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Student> tblCustomer;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;
    public void initialize(){
        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        loadAllStudents();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((ov, previous, current) -> {
            btnDelete.setDisable(current == null);
            if (current == null) return;

            txtId.setText(current.getId() + "");
            txtName.setText(current.getName());
            txtAddress.setText(current.getAddress());
        });

    }
    private void loadAllStudents() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
            ObservableList<Student> studentList = tblCustomer.getItems();

            while (rst.next()) {
                int id = rst.getInt("id");
                String name = rst.getString("name_customer");
                String address = rst.getString("address_customer");
                studentList.add(new Student(id, name,  address));
            }

            Platform.runLater(btnNewStudent::fire);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed load student details, try again").showAndWait();
            Platform.exit();
        }
    }
    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            String sql = "DELETE FROM Customer WHERE id=%d";
            sql = String.format(sql, tblCustomer.getSelectionModel().getSelectedItem().getId());
            stm.executeUpdate(sql);

            tblCustomer.getItems().remove(tblCustomer.getSelectionModel().getSelectedItem());
            if (tblCustomer.getItems().isEmpty()) btnNewStudent.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the student, try again!").show();
        }
    }

    @FXML
    void btnNewStudentOnAction(ActionEvent event) {
        ObservableList<Student> studentList = tblCustomer.getItems();
        int newId = (studentList.isEmpty() ? 1 : studentList.get(studentList.size() - 1).getId() + 1);
        txtId.setText(newId + "");
        txtName.clear();
        txtAddress.clear();

        txtName.requestFocus();

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if(!isDataValid())return;
        try {
            Student student = new Student(Integer.parseInt(txtId.getText()), txtName.getText(), txtAddress.getText());

            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm=connection.prepareStatement("INSERT INTO Customer VALUES (?,?, ?)",Statement.RETURN_GENERATED_KEYS);
            Student selectedStudent = tblCustomer.getSelectionModel().getSelectedItem();

            if (selectedStudent == null) {
                stm.setInt(1, Integer.parseInt(txtId.getText()));
                stm.setString(2,txtName.getText());
                stm.setString(3,txtAddress.getText());

                stm.executeUpdate();

                tblCustomer.getItems().add(student);
            } else {
                Statement stm1=connection.createStatement();
                String sql = "UPDATE Customer SET name_customer='%s', address_customer='%s'" + " WHERE id=%d";
                sql = String.format(sql, student.getName(), student.getAddress(),student.getId());
                stm1.executeUpdate(sql);

                ObservableList<Student> studentList = tblCustomer.getItems();
                int selectedStudentIndex = studentList.indexOf(selectedStudent);
                studentList.set(selectedStudentIndex, student);
                tblCustomer.refresh();
            }

            btnNewStudent.fire();


        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the student, try again!").show();
        }


    }

    @FXML
    void tblCustomerOnKeyReleased(KeyEvent event) {

    }
    private boolean isDataValid() {
        boolean isDataValid = true;


        String name = txtName.getText();
        String address = txtAddress.getText();



        if (address.strip().length() < 3) {
            isDataValid = false;
            txtAddress.requestFocus();
            txtAddress.selectAll();
            txtAddress.getStyleClass().add("invalid");
        }


        if (!name.matches("[A-Za-z ]+")) {
            isDataValid = false;
            txtName.requestFocus();
            txtName.selectAll();
            txtName.getStyleClass().add("invalid");
        }

        return isDataValid;
    }

}
