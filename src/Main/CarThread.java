package Main;

import Car.Car;
import NeuralNetworking.BrainObject;

import java.util.LinkedList;

public class CarThread extends Thread {
    public LinkedList<BrainObject> objects;

    public CarThread(LinkedList<BrainObject> object) {
        this.objects = object;
    }

    @Override
    public void run(){
        for(BrainObject o:objects){
            o.tick();
        }
    }
}
