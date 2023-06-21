package com.example.mathlab2fx.simple_iteration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class EquationSystemCalculator {

    private final EquationSystem equationSystem;

    public EquationSystemCalculator(EquationSystem equationSystem) {
        this.equationSystem = equationSystem;
    }

    @ToString
    @Setter
    @Getter
    private static class IndexPair {
        private Integer n;
        private Integer m;
        public IndexPair(Integer n, Integer m) {
            this.n = n;
            this.m = m;
        }
    }

    public void calculate() throws IncorrectCalculationException {
        long startTime = System.nanoTime();


        this.equationSystem.setMatrixBegin(equationSystem.getMatrixAxB().clone()); //////
        System.out.println(Arrays.deepToString(this.equationSystem.getMatrixBegin())); ////////


        iterateForDiagonalDominance(this.equationSystem.getMatrixAxB());
        expressUnknowns(this.equationSystem.getMatrixAxB());
        checkMatrixNorm(this.equationSystem.getMatrixAxB());
        getCloserToResult(this.equationSystem.getMatrixAxB());
        long endTime = System.nanoTime();
        this.equationSystem.setCalculationTime((endTime - startTime)/1000000000d);


        checkResults();
    }

    private void checkResults() {
        ArrayList<Double> res = equationSystem.getApproximateValuesTable()
                .get(equationSystem.getApproximateValuesTable().size() -1 );
        ArrayList<Double> check = new ArrayList<>();
        for (Double[] arr : this.equationSystem.getMatrixBegin()) {
            BigDecimal sum = BigDecimal.valueOf(0);
            for (int i = 0; i < arr.length - 1; i++) {
                sum = sum.add(BigDecimal.valueOf(arr[i]).multiply(BigDecimal.valueOf(res.get(i)))
                        .setScale(5, RoundingMode.HALF_UP));
            }
            check.add(sum.doubleValue());
        }
        equationSystem.setCheck(check);
    }

    private void iterateForDiagonalDominance(Double[][] arr) throws IncorrectCalculationException{
        ArrayList<IndexPair> indexList = new ArrayList<>();
        Double[][] newArray = arr.clone();
        for (int n = 0; n < arr.length; n++) {
            double arrSum = Arrays.stream(arr[n])
                    .map(Math::abs).mapToDouble(Double::doubleValue).sum() - Math.abs(arr[n][arr.length]);
            for (int m = 0; m < arr.length; m++ ) {
                if (Math.abs(arr[n][m]) >= arrSum - Math.abs(arr[n][m])) {
                    if (arr[n][m] == 0)
                        throw new IncorrectCalculationException("Наибольший элемент одной из строк = 0, это приведёт к делению на 0");
                    indexList.add(new IndexPair(n,m));
                }
            }
        }
        if (indexList.size() < arr.length)
            throw new IncorrectCalculationException("Условие диагонального преобладания не выполняется.");

        for (int i = 0; i < arr.length; i++) {
            IndexPair pair;
            try {
                int finalI = i;
                pair = indexList.stream().filter(a -> a.getM() == finalI).findFirst().orElseThrow();
                ArrayList<IndexPair> toRemove = indexList.stream()
                        .filter(a -> Objects.equals(a.getN(), pair.getN())).collect(Collectors.toCollection(ArrayList::new));
                indexList.removeAll(toRemove);
            } catch (NoSuchElementException e) {
                throw new IncorrectCalculationException("Условие диагонального преобладания не выполняется..");
            }
            newArray[pair.getM()] = arr[pair.getN()];
        }
        equationSystem.setMatrixAxB(newArray);
        System.out.println(Arrays.deepToString(equationSystem.getMatrixAxB()));///////////////
    }

    private void expressUnknowns(Double[][] arr) {
        Double[][] newArray = arr.clone();
        for (int i = 0; i < arr.length; i++) {
            BigDecimal iiDivider = BigDecimal.valueOf(arr[i][i]);
            ArrayList<Double> lineList = new ArrayList<>(Arrays.asList(arr[i].clone()));

            Collections.reverse(lineList);
            Collections.rotate(lineList.subList(lineList.size() - i -1 , lineList.size()), -1);
            Collections.reverse(lineList);

            lineList = lineList.stream().map(a -> BigDecimal.valueOf(a).divide(iiDivider, 5, RoundingMode.HALF_UP))
                    .map(BigDecimal::doubleValue).collect(Collectors.toCollection(ArrayList::new));

            Double[] lineArray = lineList.toArray(new Double[0]);
            for (int m = 1; m < lineArray.length - 1; m++) {
                lineArray[m] *= -1d;
            }
            newArray[i] = lineArray;
        }
        equationSystem.setMatrixAxB(newArray);
    }

    private void checkMatrixNorm(Double[][] arr) throws IncorrectCalculationException {
        double norm = 0d;
        for (Double[] doubles : arr) {
            double absoluteSum = 0d;
            for (int m = 1; m < arr.length; m++) {
                absoluteSum += Math.abs(doubles[m]);
            }
            if (absoluteSum > norm) norm = absoluteSum;
        }
        if (norm < 1) this.equationSystem.getMessages().add("Норма матрицы = " + norm + ". Итерационный метод сходится.");
        else throw new IncorrectCalculationException("Норма матрицы = " + norm +" >= 1. НЕ СХОДИТСЯ \n" +
                "Возможная причина: в одной из строк изначальной матрицы более одного элемента >= сумме по модулю " +
                "остальных элементов этой строки.");
    }

    private void getCloserToResult(Double[][] arr) {
        ArrayList<Double> x0 = new ArrayList<>();
        for (Double[] doubles : arr) x0.add(doubles[arr.length]);

        /*
        x0.add(100d);
        x0.add(100d);
        x0.add(100d);
        x0.add(100d);
        x0.add(100d);
        x0.add(100d);
        x0.add(100d);
         */


        this.equationSystem.getApproximateValuesTable().add(x0);
        this.equationSystem.getInaccuracyList().add(0d);

        int iterationIndex = 0;
        while (!this.equationSystem.isSolved()) {

            ArrayList<Double> listToAdd = new ArrayList<>();

            for (int n = 0; n < arr.length; n++) {
                ArrayList<Double> l = new ArrayList<>(this.equationSystem.getApproximateValuesTable().get(iterationIndex));
                l.remove(n);
                BigDecimal xk = BigDecimal.valueOf(arr[n][arr.length]);
                for (int m = 1; m < arr.length; m++) {
                    xk = xk.add((BigDecimal.valueOf(arr[n][m]))
                            .multiply(BigDecimal.valueOf(l.get(m - 1)))).setScale(5, RoundingMode.HALF_UP);
                }
                listToAdd.add(xk.doubleValue());
            }

            this.equationSystem.getApproximateValuesTable().add(listToAdd);
            iterationIndex ++;

            double deviation = 0;
            for (int i = 0; i < arr.length; i++) {
                BigDecimal difference = BigDecimal.valueOf
                        (this.equationSystem.getApproximateValuesTable().get(iterationIndex).get(i))
                        .subtract(BigDecimal.valueOf
                                (this.equationSystem.getApproximateValuesTable().get(iterationIndex - 1).get(i)));
                difference = difference.abs();
                if (difference.doubleValue() > deviation) deviation = difference.doubleValue();
            }

            this.equationSystem.getInaccuracyList().add(deviation);

            if (deviation < this.equationSystem.getAccuracy()) this.equationSystem.setSolved(true);
        }
    }

}
