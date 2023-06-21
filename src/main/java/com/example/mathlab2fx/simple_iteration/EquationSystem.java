package com.example.mathlab2fx.simple_iteration;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EquationSystem {

    private Double[][] matrixAxB;
    private ArrayList<Double> check;
    private Double[][] matrixBegin;
    private boolean solved = false;
    private boolean read = false;
    private double accuracy;
    private ArrayList<ArrayList<Double>> approximateValuesTable = new ArrayList<>();
    private ArrayList<Double> inaccuracyList = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();
    private double calculationTime;

}
