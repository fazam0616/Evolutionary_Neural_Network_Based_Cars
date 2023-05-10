package Car;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;
import GUI.Window;
import Main.Main;
import NeuralNetworking.Brain;
import NeuralNetworking.BrainObject;
import NeuralNetworking.Nodes.Input;
import NeuralNetworking.Nodes.Output;

public class Car extends BrainObject implements Serializable {
    public static double maxSpeed = 25;
    public final double height = 10;
    public final double width = 5;
    private Color color;
    private double rot;
    private LinkedList<Point> points=  new LinkedList<>();
    private double speed = 0;
    private Point velocity = Point.O;
    private boolean passedConstraint = true;
    private double averageSpeed = 0;
    private Point lastPoint;
    private Checkpoint lastCheckpoint;
    private int laps = 0;
    private static int lapTime = 0;
    private int stall;

    public Car(Color color, Point pos, double rot){
        this.lastPoint = pos;
        stall = 0;
        this.lastCheckpoint = Main.map.getCheckpoints().get(0);
        this.color = color;
        setPoints(pos,rot);
        Car self = this;
        Input[] inputs = new Input[]{
                new Input(this::getSpeed),
                new Input(() -> (this.rot)),
                new Input(() -> this.getClosestPoint(1.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(2.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(3.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(4.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(5.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(6.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(7.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(8.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(9.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(10.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(11.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(12.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(13.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(14.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(15.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(16.0/8*Math.PI,Main.map))
        };
        Output[] outputs = new Output[]{
                new Output(self::changeSpeed),
                new Output(self::changeRot)
        };

        this.setBrain(new Brain(inputs,1,6,outputs));
    }

    public Car(Color color, Point pos, double rot, String weights){
        this.lastPoint = pos;
        this.stall = 0;
        this.lastCheckpoint = Main.map.getCheckpoints().get(0);
        this.color = color;
        setPoints(pos,rot);
        Car self = this;
        Input[] inputs = new Input[]{
                new Input(this::getSpeed),
                new Input(() -> (this.rot)),
                new Input(() -> this.getClosestPoint(1.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(2.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(3.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(4.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(5.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(6.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(7.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(8.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(9.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(10.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(11.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(12.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(13.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(14.0/8*Math.PI,Main.map)),
//                new Input(() -> this.getClosestPoint(15.0/8*Math.PI,Main.map)),
                new Input(() -> this.getClosestPoint(16.0/8*Math.PI,Main.map))
        };
        Output[] outputs = new Output[]{
                new Output(self::changeSpeed),
                new Output(self::changeRot)
        };
        this.setBrain(new Brain(inputs,outputs,weights));
    }

    public void reset(){
        this.setPoints(Main.respawnPoint,Main.respawnRot);
        this.averageSpeed = 0;
        this.speed = 0;
        this.velocity = new Point(0,0);
        this.passedConstraint = true;
        this.setTick(0);
        this.lastPoint = Main.respawnPoint;
        this.stall = 0;
        this.lastCheckpoint = Main.map.getCheckpoints().get(0);
        this.laps = 0;
        this.isBest = false;
    }

    public int getClosestPoint(double angle, Map map){
        boolean collided = false;
        Point pos = this.getCentre();
        double dX = Math.cos(angle+Math.toRadians(this.rot));
        double dY = Math.sin(angle+Math.toRadians(this.rot));
        int r = 0;
        for (; !collided; r++) {
            pos = new Point(pos.getX()+dX,pos.getY()+dY);
            collided = map.getVal(pos) == 1;
        }


        return r;
    }

    public void setPoints(Point pos, double rot){
        double[] angles = new double[4];
        double d = Math.sqrt((height*height/4)+(width*width/4));
        this.rot = rot;

        angles[0] = Math.toDegrees(Math.atan2(height/2, width/2));
        angles[1] = Math.toDegrees(Math.atan2(-height/2, width/2));
        angles[2] = Math.toDegrees(Math.atan2(-height/2, -width/2));
        angles[3] = Math.toDegrees(Math.atan2(height/2, -width/2));

        points.clear();
        for(double angle:angles){
            double x = Math.cos(Math.toRadians(rot+angle))*d+pos.getX();
            double y = Math.sin(Math.toRadians(rot+angle))*d+pos.getY();
            points.add(new Point(x,y));
        }
    }

    public Point getCentre(){
        double x = 0;
        double y = 0;

        for (Point p:this.points){
            x += p.getX();
            y += p.getY();
        }
        x /= 4;
        y /= 4;

        return new Point(x,y);
    }

    private double f2(Point velocity, double angle){
        double r = Point.getDist(Point.O,velocity);
        velocity.setX(velocity.getX()/r);
        velocity.setY(velocity.getY()/r);
        double rx = Math.cos(Math.toRadians(angle));
        double ry = Math.sin(Math.toRadians(angle));

        return Math.abs((rx*velocity.getX()+ry*velocity.getY())*0.5)+0.5;
    }
    @Override
    public void update(){
        Point pos = getCentre();
        if (speed <= 0.01){
            velocity = new Point(0,0);
            speed = 0;
        }
        pos.setX(pos.getX() + velocity.getX());
        pos.setY(pos.getY() + velocity.getY());
        setPoints(pos,rot);
        double angle = Math.toDegrees(Math.atan2(velocity.getY(), velocity.getX()));
        if (speed > 1){
            velocity.setX(velocity.getX()*f2(velocity,rot+90));
            velocity.setY(velocity.getY()*f2(velocity,rot+90));
        }else{
            velocity.setX(velocity.getX()*0.9);
            velocity.setY(velocity.getY()*0.9);
        }

        this.speed = Point.getDist(Point.O,this.velocity);
        Point next = Main.map.getCheckpoints().get((this.lastCheckpoint.getNum()<Main.map.getCheckpoints().size()-1)? this.lastCheckpoint.getNum()+1:0).getPos();
        next.setX(next.getX()-pos.getX());
        next.setY(next.getY()-pos.getY());
        double l = Point.getDist(Point.O,next);
        if (l>0){
            next.setX(next.getX()/l);
            next.setY(next.getY()/l);
        }else{
            next = Point.O.clone();
        }

        Point nV = this.velocity.clone();
        if(speed > 0){
            nV.setX(nV.getX()/speed);
            nV.setY(nV.getY()/speed);
        }else{
            nV = Point.O.clone();
        }
        this.averageSpeed += this.getSpeed();//((nV.getX()*next.getX() + nV.getY()*next.getY())/5+0.8);
        //Super important for optimizing behaviour
        LinkedList<Checkpoint> c = Main.map.getCheckpoints();
        if ((this.getTick() > 7000*(this.laps+1) || laps > 3 || (this.getTick() % 1000 == 0 && Math.abs(this.lastCheckpoint.getNum()-this.stall) < 10))){
            passedConstraint = false;
            if (this.lastPoint == Main.respawnPoint){
                this.averageSpeed = 0;
            }
        }else{
            this.lastPoint = pos;
            this.lastCheckpoint = getClosestCheckpoint();
            if ((this.getTick() % 1000 == 0))
                this.stall = this.lastCheckpoint.getNum();
        }
    }

    public Checkpoint getClosestCheckpoint(){
        Checkpoint closest = Main.map.getCheckpoints().getFirst();
        double low_d = Point.getDistOptimized(this.lastPoint,closest.getPos());
        double d;
        for(Checkpoint c:Main.map.getCheckpoints()){
            d = Point.getDistOptimized(this.lastPoint,c.getPos());
            if (d<low_d){
                low_d = d;
                closest = c;
            }
        }
        if (Math.abs(closest.getNum() - this.lastCheckpoint.getNum()) <= 10 || this.lastCheckpoint.getNum() == Main.map.getCheckpoints().getLast().getNum()){
            if (closest.getNum() == Main.map.getCheckpoints().get(0).getNum() && lastCheckpoint.getNum() >= Main.map.getCheckpoints().size()/2){
                laps++;
            }

            this.lastCheckpoint = closest;
            return closest;
        } else
            return this.lastCheckpoint;
    }

    public void paint(Graphics g, Window frame){
        Color ogColor = g.getColor();
        Polygon poly = new Polygon();

        for(Point p:this.points){
            Point screenP = p.mathToScreen(frame.getWidth(),frame.getHeight());
            poly.addPoint((int)screenP.getX(), (int)screenP.getY());
        }

        g.setColor(Color.BLUE);
        if (this.isBest){
            Point p = this.getCentre().mathToScreen(frame.getWidth(),frame.getHeight());
            Point p2 = this.lastCheckpoint.getPos().mathToScreen(frame.getWidth(),frame.getHeight());
            g.drawLine((int)p.getX(),(int)p.getY(),(int)p2.getX(),(int)p2.getY());
        }

        if (this.isBest)
            g.setColor(Color.YELLOW);
        else if (this.isDead())
            g.setColor(Color.RED);
        else{
            g.setColor(this.color);
        }

        g.fillPolygon(poly);
        g.setColor(Color.BLACK);
        g.drawPolygon(poly);



        if(isBest){
            g.setColor(Color.blue);
            for (double theta = Math.PI / 4; theta <= 2 * Math.PI; theta += Math.PI / 4) {
                int length = getClosestPoint(theta, frame.getMap());
                Point screenP = getCentre().mathToScreen(
                        frame.getMap().getMap().length,
                        frame.getMap().getMap()[0].length
                );
                Point distanceP = new Point(
                        getCentre().getX() + length * Math.cos(theta + Math.toRadians(this.rot)),
                        getCentre().getY() + length * Math.sin(theta + Math.toRadians(this.rot))).mathToScreen(
                        frame.getMap().getMap().length,
                        frame.getMap().getMap()[0].length);

                g.drawLine((int) screenP.getX(), (int) screenP.getY(), (int) distanceP.getX(), (int) distanceP.getY());
            }
        }

        g.setColor(ogColor);
    }

    public void paint(Graphics g, Window frame, Color color){
        Color old = this.color;
        this.color = color;
        paint(g,frame);
        this.color = old;
    }

    private static double f(double x){
        return 1/(1+Math.pow(Math.E,-5*x))-0.5;
    }

    public void changeSpeed(double vOffset){
        velocity.setX(velocity.getX() + Math.cos(Math.toRadians(rot+90))*f(vOffset));
        velocity.setY(velocity.getY() + Math.sin(Math.toRadians(rot+90))*f(vOffset));
        speed = Point.getDist(Point.O, velocity);

        if (speed >= maxSpeed){
            double s = maxSpeed/speed;
            velocity.setX(velocity.getX()*s);
            velocity.setY(velocity.getY()*s);
            speed = Point.getDist(Point.O, velocity);
        }

    }

    public void changeRot(double rotOffset){
        rot += f(rotOffset)*20;
        rot = rot % 360;
        //this.speed *=-Math.pow(rotOffset,4)/+1;
    }

    public double getSpeed() {
        speed = Point.getDist(Point.O, velocity);
        return speed;
    }

    public double getRot() {
        return rot;
    }

    @Override
    public double getFitness() {
        double f;
        double checkpointNum = this.lastCheckpoint.getNum();
        f = 5*((Double.isNaN(this.averageSpeed/this.getTick()) || this.getTick() == 0)?0:this.averageSpeed/(this.getTick()*this.getTick())) + (checkpointNum+this.laps*Main.map.getCheckpoints().size());
        return f;
    }

    public String fitnessReport(){
        return "V:"+1000*this.averageSpeed/this.getTick()+"  Checkpoint:"+this.lastCheckpoint.getNum()+"  Laps:"+this.laps+"  Score:"+this.getFitness();
    }

    @Override
    public boolean isDead() {
        boolean pointInWall = false;
        if (passedConstraint)
            for (int i = 0; i < 4 && !pointInWall; i++) {
                pointInWall = Main.map.getVal(this.points.get(i)) == 1;
            }
        else
            return true;
        return pointInWall;
    }
}
