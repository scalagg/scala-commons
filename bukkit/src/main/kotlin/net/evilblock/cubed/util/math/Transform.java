package net.evilblock.cubed.util.math;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;

import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class Transform {
    
    /*
     * BASE
     * 1 0 0 0
     * 0 1 0 0
     * 0 0 1 0
     * 0 0 0 1
     * 
     * Translation: 
     *      x = 3,0
     *      y = 3,1
     *      z = 3,2
     *      
     * Scaling    
     *      sx = 0,0
     *      sy = 1,1
     *      sz = 2,2
     *      
     * Rotation x (roll)
     *      1 0 0 0
     *      0 cos -sin 0
     *      0 sin cos 0
     *      0 0 0 1
     *      
     * Rotation y (yaw)
     *      cos 0 sin 0
     *      0 1 0 0
     *      -sin 0 cos 0
     *      0 0 0 1
     *      
     * Rotation z (pitch)
     *      cos -sin 0 0
     *      sin cos 0 0
     *      0 0 1 0
     *      0 0 0 1
     * 
     *       
     */
    
    
    private Matrix4x4 local = new Matrix4x4();
    
    private Point localPoint = new Point();
    private Rotation localRotation = new Rotation();
    
    private Matrix4x4 global = new Matrix4x4();
    
    private AtomicReference<Transform> parent = new AtomicReference<>();
    private final List<Transform> children = Lists.newArrayList();
    private final ReentrantLock lock = new ReentrantLock();
    
    public Transform() {
    }
    
    public Transform(Point point) {
        this.localPoint.x = point.x;
        this.localPoint.y = point.y;
        this.localPoint.z = point.z;
        this.updateLocalMatrix();
    }
    
    public Transform(Point point, Rotation rotation) {
        this.localPoint.x = point.x;
        this.localPoint.y = point.y;
        this.localPoint.z = point.z;
        this.localRotation.yaw = rotation.yaw;
        this.localRotation.pitch = rotation.pitch;
        this.localRotation.roll = rotation.roll;
        this.updateLocalMatrix();
    }
    
    public Transform addChildren(Transform... transforms) {
        try {
            lock.lock();
            for (Transform trans : transforms) {
                if (trans.parent.get() != null) {
                    trans.parent.get().removeChild(trans);
                }
                trans.parent.set(this);
                this.children.add(trans);
                trans.updateGlobal(this.global);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }
    
    public Transform removeChild(Transform transforms) {
        this.children.remove(transforms);
        return this;
    }
    
    final void updateLocalMatrix() {
        this.local.identity().translate(localPoint).rotate(localRotation);
        updateGlobal(this.parent.get() != null ? parent.get().global : null);
    }
    
    public Transform setPosition(Point point) {
        this.localPoint.x = point.x;
        this.localPoint.y = point.y;
        this.localPoint.z = point.z;
        this.updateLocalMatrix();
        return this;
    }
    
    public Transform setRotation(Rotation rotation) {
        this.localRotation.yaw = rotation.yaw;
        this.localRotation.pitch = rotation.pitch;
        this.localRotation.roll = rotation.roll;
        this.updateLocalMatrix();
        return this;
    }
    
    public Transform setPositionAndRotation(Point point, Rotation rotation) {
        this.localPoint.x = point.x;
        this.localPoint.y = point.y;
        this.localPoint.z = point.z;
        this.localRotation.yaw = rotation.yaw;
        this.localRotation.pitch = rotation.pitch;
        this.localRotation.roll = rotation.roll;
        this.updateLocalMatrix();
        return this;
    }
    
    public Vector getRight() {
        return this.global.getRight(null);
    }
    
    public Vector getUp() {
        return this.global.getUp(null);
    }

    public Vector getForward() {
        return this.global.getForward(null);
    }


    public Vector getRight(@Nullable Vector target) {
        return this.global.getRight(target);
    }
    
    public Vector getUp(@Nullable Vector target) {
        return this.global.getUp(target);
    }

    public Vector getForward(@Nullable Vector target) {
        return this.global.getForward(target);
    }

    
    public Point getPosition(@Nullable Point point) {
        if (this.parent == null)
            return this.local.getTranslation(point);
        return this.global.getTranslation(point);
    }
    /*
    public Rotation getRotation(@Nullable Rotation rotation) {
        if (this.parent == null)
            return this.local.getRotation(rotation);
        return this.global.getRotation(rotation);
    }*/
    
    final void updateGlobal(@Nullable Matrix4x4 parent) {
        if (parent != null)
            parent.mul(this.local, this.global);
        else {
            this.global.set(this.local);
        }
        try {
            lock.lock();
            this.children.forEach(t -> t.updateGlobal(this.global));
        } finally {
            lock.unlock();
        }
    }
    
    public Transform getParent() {
        return this.parent.get();
    }
    
    public Transform[] getChildren() {
        try {
            lock.lock();
            return this.children.toArray(new Transform[0]);
        } finally {
            lock.unlock();
        }
    }
    
}
