package org.firstinspires.ftc.teamcode;

public class GyroAnalysis {
    public int size;
    public int total;
    public int index;
    public double data[];


    public GyroAnalysis(int size, double initSample){ // construct with args
        this.size = size;
        data = new double[size];
        for (int i = 0; i < size; i++){
            data[i] = initSample;
        }

    }
    public void add(double x){
        total -= data[index];
        data[index] = x;
        total += x;
        if (++index == size)index = 0;
    }

    public double getError() {
        return total/size;
    }
    public void initialize(){

    }
}