package lk.ijse.dep10.project2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep10.project2.db.DBConnection;
import lk.ijse.dep10.project2.utils.Customer;

import java.sql.*;

public class CustomerViewController {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewCustomer;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Customer> tblCustomer;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    public void initialize() {
        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        loadCustomer();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, current) -> {
            btnDelete.setDisable(current == null);
            if (!(current == null)) {
               txtId.setText(String.valueOf(current.getId()));
               txtName.setText(current.getName());
               txtAddress.setText(current.getAddress());
            }
        });

    }

    private void loadCustomer() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");

            while (rst.next()) {
                int id = rst.getInt(1);
                String name = rst.getString(2);
                String address = rst.getString(3);

                Customer customer = new Customer(id, name, address);
                tblCustomer.getItems().add(customer);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer. Try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Customer selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement stmStudent = connection.prepareStatement("DELETE FROM Customer WHERE id = ?");
            stmStudent.setInt(1, selectedCustomer.getId());
            stmStudent.executeUpdate();

            connection.commit();
            tblCustomer.getItems().remove(selectedCustomer);
            if (tblCustomer.getItems().isEmpty()) btnNewCustomer.fire();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to delete student").show();
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void btnNewCustomerOnAction(ActionEvent event) {
        txtId.setText("Generated Id");
        txtName.clear();
        txtAddress.clear();
        txtName.requestFocus();

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!isValidate()) return;

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Customer(name_customer, address_customer) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1,txtName.getText());
            stm.setString(2,txtAddress.getText());
            stm.executeUpdate();

            ResultSet generatedKey = stm.getGeneratedKeys();
            generatedKey.next();
            int id = generatedKey.getInt(1);
            Customer customer = new Customer(id, txtName.getText(), txtAddress.getText());
            tblCustomer.getItems().add(customer);

            btnNewCustomer.fire();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void tblCustomerOnKeyReleased(KeyEvent event) {

    }
    private boolean isValidate() {
        boolean validData = true;

        if (txtAddress.getText().length() < 3) {
            txtAddress.requestFocus();
            txtAddress.selectAll();
            //txtAddress.getStyleClass().add("invalid");
            validData = false;
        }
        if (!txtName.getText().matches("[A-Za-z ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            //txtName.getStyleClass().add("invalid");
            validData = false;
        }

        return validData;
    }
}
