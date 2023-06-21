package com.example.mathlab2fx;

import com.example.mathlab2fx.simple_iteration.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextArea input;

    @FXML
    protected void onSubmit() {
        tableResults.getItems().clear();
        welcomeText.setText(input.getText());
        //System.out.println(result.getText());
        startComputation(input.getText());
    }

    @FXML
    private Button submit;

    private void startComputation(String input) {
        MatrixReader reader = new MatrixReader(input);
        try {

            EquationSystem equationSystem = reader.tryToRead();

            EquationSystemCalculator equation = new EquationSystemCalculator(equationSystem);
            equation.calculate();

            equationSystem.getMessages().forEach(System.out::println);
            System.out.println("Время расчёта: " + equationSystem.getCalculationTime() + " с.");

            initialize(equationSystem);

        } catch (WrongInputException | IncorrectCalculationException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        tableResults.getItems().clear();
        //initialize();
    }

    private final ObservableList<IterationResult> usersData = FXCollections.observableArrayList();

    @FXML
    private TableView<IterationResult> tableResults;

    @FXML
    private TableColumn<IterationResult, Integer> kColumn;

    @FXML
    private TableColumn<IterationResult, String> xColumn;

    @FXML
    private TableColumn<IterationResult, String> epsilonColumn;


    private void initialize(EquationSystem equationSystem) {
        initData(equationSystem);

        kColumn.setCellValueFactory(new PropertyValueFactory<IterationResult, Integer>("k"));
        xColumn.setCellValueFactory(new PropertyValueFactory<IterationResult, String>("x"));
        epsilonColumn.setCellValueFactory(new PropertyValueFactory<IterationResult, String>("epsilon"));

        tableResults.setItems(usersData);
    }

    private void initData(EquationSystem equationSystem) {
        ArrayList<ArrayList<Double>> table = equationSystem.getApproximateValuesTable();
        for (int i = 0; i < table.size(); i++) {
            ArrayList<Double> list = table.get(i);
            StringBuilder stringBuilder = new StringBuilder();
            for (Double decimal : list) {
                stringBuilder.append(decimal).append("  ");
            }
            String str = stringBuilder.toString();
            usersData.add(new IterationResult(i, str, equationSystem.getInaccuracyList().get(i).toString()));
        }
    }
}