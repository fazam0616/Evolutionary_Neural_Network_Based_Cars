package GUI;

import Car.*;
import Car.Point;
import Main.Main;
import Main.CarThread;

import javax.swing.*;

import NeuralNetworking.Brain;
import NeuralNetworking.BrainObject;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;

public class Panel extends JPanel {
    private Window parentWindow;
    double cycles = 0;
    double lastScore = 0;
    public Panel(Window p){
        this.parentWindow = p;
    }
    public void paintComponent(Graphics g){
        g.drawImage(parentWindow.getMap().getImage(),0,0,this);

        int rad = parentWindow.getMap().getTrackWidth();
        g.setColor(Color.GREEN);
        Point p = Main.respawnPoint.mathToScreen(parentWindow.getWidth(), parentWindow.getHeight());
        g.fillOval((int)p.getX()-rad/2,(int)p.getY()-rad/2,rad,rad);

        for (int i = 0; i < Main.map.getCheckpoints().size(); i++) {
            g.setColor(new Color((int)(255*(1-i/(double)Main.map.getCheckpoints().size())),(int)(255*(i/(double)Main.map.getCheckpoints().size())),0));
            p = Main.map.getCheckpoints().get(i).getPos().mathToScreen(parentWindow.getWidth(), parentWindow.getHeight());
            g.fillOval((int)p.getX()-2,(int)p.getY()-2,5,5);
        }

        if (Main.TEST) {
            //Main.car.update();
            Main.car.paint(g, parentWindow);
        }
        else {
            LinkedList<CarThread> threads = new LinkedList<CarThread>();
            LinkedList<BrainObject> cars = new LinkedList<>();

            for(BrainObject car:Main.cars){
                if (!car.isDead()) {
                    if (cars.size() < 30)
                        cars.add(car);
                    else {
                        threads.add(new CarThread(cars));
                        threads.getLast().start();
                        cars = new LinkedList<>();
                        cars.add(car);
                    }
                }
            }
            threads.add(new CarThread(cars));
            threads.getLast().start();

            for (CarThread t:threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LinkedList<Car> aliveCars = new LinkedList<>();
            for (BrainObject car:Main.cars){
                if (car.isDead())
                    ((Car)car).paint(g, parentWindow);
                else
                    aliveCars.add((Car)car);
            }

            for (Car car:aliveCars)
                car.paint(g, parentWindow);

            if (BrainObject.isDead(Main.cars)){
                lastScore = Main.cars.getLast().getFitness();
//                System.out.println(Brain.evolve2(Main.cars));
                System.out.println(Brain.evolve(Main.cars,(cycles < 5)? 0:cycles));
                if (Math.abs(Main.cars.getLast().getFitness() - lastScore) < 5 ){
                    cycles += 1;
                }
                else{
                    cycles = 0;
                }
                System.out.println("--------------------------------------------------");
            }
            try {
                if(((JSlider)this.getComponent(0)).getValue() > 0)
                    Thread.sleep(((JSlider)this.getComponent(0)).getValue());
            } catch (InterruptedException e) {}
            Main.window.repaint();
        }
    }

    public Window getParentWindow() {
        return parentWindow;
    }
}
