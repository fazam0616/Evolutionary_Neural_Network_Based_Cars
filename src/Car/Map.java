package Car;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.LinkedList;

public class Map implements Serializable {
    private int trackWidth;
    private int length;
    private LinkedList<Checkpoint> Checkpoints;
    private Point spawn;
    private BufferedImage image;
    private int[][] map;

    public Map(BufferedImage image, Point spawn){
        this.image = image;
        this.spawn = spawn;
        this.Checkpoints = new LinkedList<Checkpoint>();
        this.map = new int[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (((this.image.getRGB(x,y) & 0xff0000) >> 16) == 0)
                    map[x][y] = 1;
                else
                    map[x][y] = 0;
            }
        }
        try{
            createCheckPoints();
            System.out.println("Created "+ Checkpoints.size()+" Checkpoints for a length of "+length+"px");
        } catch (Exception e){
            System.out.println("FAILED TO CREATE CHECKPOINTS");
        }
        Car.maxSpeed = this.trackWidth*2;
    }

    public void createCheckPoints(){
        double l = 10;
        Point currentPos = this.spawn.clone();
        Point wall = this.getClosestWall(currentPos);
        Point wallToCent = new Point(currentPos.getX() - wall.getX(),currentPos.getY() - wall.getY());
        Point trackVec = new Point(-wallToCent.getY(), wallToCent.getX());
        double d = Point.getDist(Point.O,trackVec);
        trackWidth = (int)Math.round(2*Point.getDist(Point.O,wallToCent));
        trackVec.setX(l*trackVec.getX()/d);
        trackVec.setY(l*trackVec.getY()/d);

        int count = 0;
        boolean done = false;
        Point other;
        double wallCenAngle = Math.atan2(wallToCent.getY(),wallToCent.getX());
        Checkpoints.clear();

        currentPos = Point.combine(currentPos, trackVec);
        Point change = new Point(Math.cos(wallCenAngle), Math.sin(wallCenAngle));
        other = currentPos.clone();

        while (this.getVal(other) == 0){
            other = Point.combine(other, change);
        }

        currentPos = new Point((other.getX()+wall.getX())/2,(other.getY()+wall.getY())/2);
        l = Point.getDist(currentPos,this.getClosestWall(currentPos))/2;
        Checkpoints.add(new Checkpoint(currentPos, count++));
        System.out.println(count + ", " + currentPos+", "+wall+", "+(wallCenAngle+Math.PI/2));

        while (!done){

            if (this.getVal(currentPos) != 0) {
                System.out.println("Error: checkpoint in walls");
                System.out.println(currentPos);
                done = true;
//                throw new RuntimeException("Checkpoint in walls");
            }
            if (!done) {
                trackVec = new Point(-wallToCent.getY(), wallToCent.getX());
                d = Point.getDist(Point.O,trackVec);
                trackVec.setX(l*trackVec.getX()/d);
                trackVec.setY(l*trackVec.getY()/d);
                currentPos = Point.combine(currentPos, trackVec);

                wallToCent = new Point(currentPos.getX() - wall.getX(), currentPos.getY() - wall.getY());
                wallCenAngle = Math.atan2(wallToCent.getY(),wallToCent.getX());

                wall = this.getClosestWall(currentPos,wallCenAngle+5*Math.PI/4,wallCenAngle+3*Math.PI/4);
                other = this.getClosestWall(currentPos,wallCenAngle-Math.PI/4,wallCenAngle+Math.PI/4);
//                d = Point.getDist(Point.O,wallToCent);
//                change = new Point(wallToCent.getX()/d,wallToCent.getY()/d);
//                other = currentPos.clone();
//
//                while (this.getVal(other) == 0){
//                    other = Point.combine(other, change);
//                }
//                double temp = Math.atan2(wallToCent.getY(),wallToCent.getX());
//                if (    (Math.signum(temp) == Math.signum(wallCenAngle)) && Math.abs(temp - wallCenAngle) >= Math.PI/4 ||
//                        (Math.signum(temp) != Math.signum(wallCenAngle) && 2*Math.PI-Math.abs(temp - wallCenAngle) >= Math.PI/4)){
//                    //                System.out.println(wallCenAngle+" vs "+ temp+": "+(Math.signum(temp) == Math.signum(angle)));
//                    Point t = wall.clone();
//                    wall = other.clone();
//                    other = t;
//
//                    wallToCent = new Point(currentPos.getX() - wall.getX(), currentPos.getY() - wall.getY());
//                    //                System.out.println("Flip");
//                }

                currentPos = new Point((other.getX()+wall.getX())/2,(other.getY()+wall.getY())/2);

                if (Point.getDistOptimized(currentPos, Checkpoints.getFirst().getPos()) < l*l && count > 50) {
                    done = true;
                } else {
                    Checkpoints.add(new Checkpoint(currentPos, count++));
                    System.out.println(count + ", " + currentPos+", "+wall+", "+(wallCenAngle+Math.PI/2));
                }
            }
        }
        Checkpoint p = this.Checkpoints.get(0);
        for (Checkpoint c:this.Checkpoints) {
            length += Point.getDist(p.getPos(),c.getPos());
            p = c;
        }
    }
    public int getVal(Point pos) {
        Point screenPoint = pos.mathToScreen(map.length,map[0].length);
        if (screenPoint.getX() > 0 && screenPoint.getX() < map.length)
            if (screenPoint.getY() > 0 && screenPoint.getY() < map[0].length)
                return map[(int)screenPoint.getX()][(int)screenPoint.getY()];
        return 1;
    }

    public Point getClosestWall(Point pos){
        return getClosestWall(pos,0,2*Math.PI);
    }

    public Point getClosestWall(Point pos, double a, double b){
        int r = 0;
        double t = 0;
        boolean found = false;
        double baseX = pos.getX();
        double baseY = pos.getY();
        for (r = 0; r < map.length && !found; r++) {
            for (t = Math.min(a,b); t < Math.max(a,b) && !found; t += Math.PI/100) {
                try{
                    found = this.getVal(new Point(baseX + r*Math.cos(t), baseY + r*Math.sin(t))) == 1;
                } catch (RuntimeException e){
                    System.out.println("t: "+t+" r: "+r);
                    throw e;
                }

            }
        }

        if (found)
            return new Point(baseX + r*Math.cos(t), baseY + r*Math.sin(t));
        else
            throw new RuntimeException("No wall found");
    }

    public int[][] getMap(){
        return this.map;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Point getSpawn() {
        return spawn;
    }

    public void setSpawn(Point spawn) {
        this.spawn = spawn;
    }

    public LinkedList<Checkpoint> getCheckpoints() {
        return Checkpoints;
    }

    public int getTrackWidth() {
        return trackWidth;
    }

    public int getLength() {
        return length;
    }
}