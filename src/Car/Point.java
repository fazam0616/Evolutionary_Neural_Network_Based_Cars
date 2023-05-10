package Car;

import java.io.Serializable;

public class Point implements Serializable {
    public static final Point O = new Point(0,0);
    private double x,y;
    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point clone(){
        return new Point(this.x, this.y);
    }

    public Point mathToScreen(double length, double height){
        double newX = this.getX() + length/2;
        double newY = -this.getY() + height/2;

        return new Point(Math.round(newX), Math.round(newY));
    }

    public static double getDistOptimized(Point a, Point b){
        return Math.pow(a.getX()-b.getX(),2) + Math.pow(a.getY()-b.getY(),2);
    }

    public static double getDist(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2) + Math.pow(a.getY()-b.getY(),2));
    }

    public static Point combine(Point p1, Point p2){
        return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    public String toString(){
        return this.x+", "+this.y;
    }
}
