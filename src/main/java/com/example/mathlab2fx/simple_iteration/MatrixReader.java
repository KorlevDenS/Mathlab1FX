package com.example.mathlab2fx.simple_iteration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class MatrixReader {

    private final Scanner stdinReader;
    private Scanner fileReader;
    private EquationSystem equationSystem;

    private final String input;
    public MatrixReader(String input) {
        this.input = input;
        this.stdinReader = new Scanner(this.input);
    }

    public EquationSystem tryToRead() throws WrongInputException {
        if (this.input.equals("")) throw new WrongInputException("Строка пуста");
        String firstLine = this.stdinReader.nextLine().trim();

        if (firstLine.matches(".*\\.(txt|doc|pdf)$")) {
            try {
                this.fileReader = new Scanner(new FileReader(firstLine));
            } catch (FileNotFoundException e) {
                throw new WrongInputException("Файл не существует или имеет недопустимое имя.");
            }
            if (!fileReader.hasNext()) throw new WrongInputException("Файл пуст.");
            readFromFile();
        } else if (firstLine.matches("exit")) {
            System.exit(0);
        } else {
            readFromStdin(firstLine);
        }
        return this.equationSystem;
    }

    private void readFromFile() throws WrongInputException {
        this.equationSystem = new EquationSystem();
        ArrayList<Double> lineList = new ArrayList<>();
        String firstLine = this.fileReader.nextLine();
        Scanner lineReader = new Scanner(firstLine);

        while (lineReader.hasNext()) {
            try {
                lineList.add(lineReader.nextDouble());
            } catch (Exception e) {
                throw new WrongInputException("Матрица в файле содержит ошибки.");
            }
        }
        if (lineList.size() < 2) throw new WrongInputException("Матрица системы уравнений не может быть короче двух");
        Double[][] array = new Double[lineList.size() - 1][lineList.size()];

        array[0] = lineList.toArray(new Double[0]);

        try {
            for (int n = 1; n < lineList.size() - 1; n++) {
                String line = this.fileReader.nextLine();
                Scanner sc = new Scanner(line);
                for (int m = 0; m < lineList.size(); m++) {
                    array[n][m] = sc.nextDouble();
                }
            }
            this.equationSystem.setAccuracy(this.fileReader.nextDouble());
        } catch (Exception e) {
            throw new WrongInputException("Матрица в файле содержит ошибки. Проверьте размерности и наличие иных символов");
        }
        this.equationSystem.setMatrixAxB(array);
        this.equationSystem.setRead(true);
    }

    private void readFromStdin(String firstLine) throws WrongInputException{
        this.equationSystem = new EquationSystem();
        ArrayList<Double> lineList = new ArrayList<>();

        Scanner lineReader = new Scanner(firstLine);

        while (lineReader.hasNext()) {
            try {
                lineList.add(lineReader.nextDouble());
            } catch (Exception e) {
                throw new WrongInputException("Первая строка матрицы содержит ошибки.");
            }
        }
        if (lineList.size() < 2) throw new WrongInputException("Длина матрицы системы уравнений не может быть < 2");
        Double[][] array = new Double[lineList.size() - 1][lineList.size()];

        array[0] = lineList.toArray(new Double[0]);

        try {
            for (int n = 1; n < lineList.size() - 1; n++) {
                String line = this.stdinReader.nextLine();
                Scanner sc = new Scanner(line);
                for (int m = 0; m < lineList.size(); m++) {
                    array[n][m] = sc.nextDouble();
                }
            }
            this.equationSystem.setAccuracy(this.stdinReader.nextDouble());
        } catch (Exception e) {
            throw new WrongInputException("Строка введена с ошибкой. Проверьте размерности и наличие иных символов");
        }
        this.equationSystem.setMatrixAxB(array);
        this.equationSystem.setRead(true);
    }
}
