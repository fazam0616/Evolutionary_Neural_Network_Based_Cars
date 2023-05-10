package Car;

import java.io.Serializable;

public class Checkpoint implements Serializable {

    private Point pos;
    private int num;

    public Checkpoint(Point pos, int num) {
        this.pos = pos;
        this.num = num;
    }

    public Point getPos() {
        return pos.clone();
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
