package net.evilblock.cubed.util.math;

import org.bukkit.util.Vector;

public class Rotation {
    double yaw, pitch, roll;

    public Rotation(){}
    
    public Rotation(Vector direction) {
        yaw = Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
        pitch = Math.toDegrees(Math.atan2(Math.sqrt(direction.getZ() * direction.getZ() + direction.getX() * direction.getX()), direction.getY())) - 90;
        ensure();
    }
    
    public Rotation(double yaw, double pitch, double roll){
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }
    
    public double getYaw() {
        return this.yaw;
    }
    
    public double getPitch() {
        return this.pitch;
    }
    
    public double getRoll() {
        return this.roll;
    }

    void ensure() {
        if (Double.isNaN(yaw) || Double.isInfinite(roll))
            yaw = 0;
        if (Double.isNaN(pitch) || Double.isInfinite(roll))
            pitch = 0;
        if (Double.isNaN(roll) || Double.isInfinite(roll))
            roll = 0;
    }
}
