package com.example.mathlab2fx;

public class IterationResult {

    private int k;
    private String x;
    private String epsilon;

    public IterationResult(int id, String login, String password) {
        this.k = id;
        this.x = login;
        this.epsilon = password;
    }

    public IterationResult() {
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(String epsilon) {
        this.epsilon = epsilon;
    }
}