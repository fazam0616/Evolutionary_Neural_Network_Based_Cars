package GUI;

import Car.Map;
import Main.Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame implements KeyListener{
    private Map map;
    public double dtheta = 0;
    public double dvel = 0;
    public boolean print = false;
    public Window(Map map){
        super();

        Panel p =new Panel(this);
        this.setSize(map.getMap().length, map.getMap()[0].length+8);
        this.setTitle("Evolutionary Neural Network");
        this.setContentPane(p);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.map = map;
        JSlider slider = new JSlider(0,100,0);
        if (!Main.TEST){
            p.add(slider);
            pack();
        }
        this.setSize(map.getMap().length, map.getMap()[0].length);
        this.setVisible(true);
        addKeyListener(this);
    }

    public Map getMap() {
        return map;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w': dvel = 0.5;break;
            case 's': dvel = -0.5;break;
            case 'd': dtheta = 0.1;break;
            case 'a': dtheta = -0.1;break;
            case 'p': print = true;break;
        }
        System.out.println(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w': case 's': dvel = 0;break;
            case 'd': case 'a': dtheta = 0;break;
        }
    }
}
