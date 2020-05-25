package it.polimi.ingsw.client.gui.utils;

import javafx.geometry.Point3D;

public class STLImportInfo {
    private final double x;
    private final double y;
    private final double z;
    private final double scale;

    public STLImportInfo(double x, double y, double z, double scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
    }
    public STLImportInfo(Point3D point, double scale){
        this(point.getX(),point.getY(),point.getZ(),scale);
    }
    public STLImportInfo(double x, double y, double z){
        this(x,y,z,1);
    }
    public STLImportInfo(Point3D point){
        this(point,1);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getScale() {
        return scale;
    }
}
