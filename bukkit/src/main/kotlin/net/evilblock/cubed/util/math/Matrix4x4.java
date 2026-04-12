package net.evilblock.cubed.util.math;

import javax.annotation.Nullable;

import org.bukkit.util.Vector;

public class Matrix4x4 {

    private static final Matrix4x4 RIGHT = new Matrix4x4().identity().translate(-1, 0, 0);
    private static final Matrix4x4 UP = new Matrix4x4().identity().translate(0, 1, 0);
    private static final Matrix4x4 FORWARD = new Matrix4x4().identity().translate(0, 0, 1);

    double m00 = 0, m10 = 0, m20 = 0, m30 = 0;
    double m01 = 0, m11 = 0, m21 = 0, m31 = 0;
    double m02 = 0, m12 = 0, m22 = 0, m32 = 0;
    double m03 = 0, m13 = 0, m23 = 0, m33 = 0;

    public Matrix4x4() {
    }

    public Matrix4x4(
            double m00, double m10, double m20, double m30,
            double m01, double m11, double m21, double m31,
            double m02, double m12, double m22, double m32,
            double m03, double m13, double m23, double m33) {

        this.m00 = m00;
        this.m10 = m10;
        this.m20 = m20;
        this.m30 = m30;

        this.m01 = m01;
        this.m11 = m11;
        this.m21 = m21;
        this.m31 = m31;

        this.m02 = m02;
        this.m12 = m12;
        this.m22 = m22;
        this.m32 = m32;

        this.m03 = m03;
        this.m13 = m13;
        this.m23 = m23;
        this.m33 = m33;

    }

    public Matrix4x4 mul(Matrix4x4 right, @Nullable Matrix4x4 target) {
        if (target == null)
            target = new Matrix4x4();
        double nm00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02 + m30 * right.m03;
        double nm01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02 + m31 * right.m03;
        double nm02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02 + m32 * right.m03;
        double nm03 = m03 * right.m00 + m13 * right.m01 + m23 * right.m02 + m33 * right.m03;
        double nm10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12 + m30 * right.m13;
        double nm11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12 + m31 * right.m13;
        double nm12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12 + m32 * right.m13;
        double nm13 = m03 * right.m10 + m13 * right.m11 + m23 * right.m12 + m33 * right.m13;
        double nm20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22 + m30 * right.m23;
        double nm21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22 + m31 * right.m23;
        double nm22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22 + m32 * right.m23;
        double nm23 = m03 * right.m20 + m13 * right.m21 + m23 * right.m22 + m33 * right.m23;
        double nm30 = m00 * right.m30 + m10 * right.m31 + m20 * right.m32 + m30 * right.m33;
        double nm31 = m01 * right.m30 + m11 * right.m31 + m21 * right.m32 + m31 * right.m33;
        double nm32 = m02 * right.m30 + m12 * right.m31 + m22 * right.m32 + m32 * right.m33;
        double nm33 = m03 * right.m30 + m13 * right.m31 + m23 * right.m32 + m33 * right.m33;

        target.m00 = nm00;
        target.m01 = nm01;
        target.m02 = nm02;
        target.m03 = nm03;

        target.m10 = nm10;
        target.m11 = nm11;
        target.m12 = nm12;
        target.m13 = nm13;

        target.m20 = nm20;
        target.m21 = nm21;
        target.m22 = nm22;
        target.m23 = nm23;

        target.m30 = nm30;
        target.m31 = nm31;
        target.m32 = nm32;
        target.m33 = nm33;

        return target;
    }

    public Matrix4x4 add(Matrix4x4 addition, @Nullable Matrix4x4 target) {
        if (target == null)
            target = new Matrix4x4();

        target.m00 = m00 + addition.m00;
        target.m10 = m10 + addition.m10;
        target.m20 = m20 + addition.m20;
        target.m30 = m30 + addition.m30;

        target.m01 = m01 + addition.m01;
        target.m11 = m11 + addition.m11;
        target.m21 = m21 + addition.m21;
        target.m31 = m31 + addition.m31;

        target.m02 = m02 + addition.m02;
        target.m12 = m12 + addition.m12;
        target.m22 = m22 + addition.m22;
        target.m32 = m32 + addition.m32;

        target.m03 = m03 + addition.m03;
        target.m13 = m13 + addition.m13;
        target.m23 = m23 + addition.m23;
        target.m33 = m33 + addition.m33;

        return target;
    }

    public Matrix4x4 sub(Matrix4x4 subtraction, @Nullable Matrix4x4 target) {
        if (target == null)
            target = new Matrix4x4();

        target.m00 = m00 - subtraction.m00;
        target.m10 = m10 - subtraction.m10;
        target.m20 = m20 - subtraction.m20;
        target.m30 = m30 - subtraction.m30;

        target.m01 = m01 - subtraction.m01;
        target.m11 = m11 - subtraction.m11;
        target.m21 = m21 - subtraction.m21;
        target.m31 = m31 - subtraction.m31;

        target.m02 = m02 - subtraction.m02;
        target.m12 = m12 - subtraction.m12;
        target.m22 = m22 - subtraction.m22;
        target.m32 = m32 - subtraction.m32;

        target.m03 = m03 - subtraction.m03;
        target.m13 = m13 - subtraction.m13;
        target.m23 = m23 - subtraction.m23;
        target.m33 = m33 - subtraction.m33;

        return target;
    }

    public double determinant() {
        return (m00 * m11 - m01 * m10) * (m22 * m33 - m23 * m32)
             + (m02 * m10 - m00 * m12) * (m21 * m33 - m23 * m31)
             + (m00 * m13 - m03 * m10) * (m21 * m32 - m22 * m31)
             + (m01 * m12 - m02 * m11) * (m20 * m33 - m23 * m30)
             + (m03 * m11 - m01 * m13) * (m20 * m32 - m22 * m30)
             + (m02 * m13 - m03 * m12) * (m20 * m31 - m21 * m30);
    }

    public Matrix4x4 invert(@Nullable Matrix4x4 target) {
        if (target == null)
            target = new Matrix4x4();

        double a = m00 * m11 - m01 * m10;
        double b = m00 * m12 - m02 * m10;
        double c = m00 * m13 - m03 * m10;
        double d = m01 * m12 - m02 * m11;
        double e = m01 * m13 - m03 * m11;
        double f = m02 * m13 - m03 * m12;
        double g = m20 * m31 - m21 * m30;
        double h = m20 * m32 - m22 * m30;
        double i = m20 * m33 - m23 * m30;
        double j = m21 * m32 - m22 * m31;
        double k = m21 * m33 - m23 * m31;
        double l = m22 * m33 - m23 * m32;
        double det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0f / det;
        double nm00 = ( m11 * l - m12 * k + m13 * j) * det;
        double nm01 = (-m01 * l + m02 * k - m03 * j) * det;
        double nm02 = ( m31 * f - m32 * e + m33 * d) * det;
        double nm03 = (-m21 * f + m22 * e - m23 * d) * det;
        double nm10 = (-m10 * l + m12 * i - m13 * h) * det;
        double nm11 = ( m00 * l - m02 * i + m03 * h) * det;
        double nm12 = (-m30 * f + m32 * c - m33 * b) * det;
        double nm13 = ( m20 * f - m22 * c + m23 * b) * det;
        double nm20 = ( m10 * k - m11 * i + m13 * g) * det;
        double nm21 = (-m00 * k + m01 * i - m03 * g) * det;
        double nm22 = ( m30 * e - m31 * c + m33 * a) * det;
        double nm23 = (-m20 * e + m21 * c - m23 * a) * det;
        double nm30 = (-m10 * j + m11 * h - m12 * g) * det;
        double nm31 = ( m00 * j - m01 * h + m02 * g) * det;
        double nm32 = (-m30 * d + m31 * b - m32 * a) * det;
        double nm33 = ( m20 * d - m21 * b + m22 * a) * det;

        target.m00 = nm00;
        target.m01 = nm01;
        target.m02 = nm02;
        target.m03 = nm03;

        target.m10 = nm10;
        target.m11 = nm11;
        target.m12 = nm12;
        target.m13 = nm13;

        target.m20 = nm20;
        target.m21 = nm21;
        target.m22 = nm22;
        target.m23 = nm23;

        target.m30 = nm30;
        target.m31 = nm31;
        target.m32 = nm32;
        target.m33 = nm33;

        return target;
    }

    public Matrix4x4 translate(Point point) {
        return translate(point.x, point.y, point.z);
    }

    public Matrix4x4 translate(double x, double y, double z) {
        m30 = m00 * x + m10 * y + m20 * z + m30;
        m31 = m01 * x + m11 * y + m21 * z + m31;
        m32 = m02 * x + m12 * y + m22 * z + m32;
        m33 = m03 * x + m13 * y + m23 * z + m33;
        return this;
    }

    public Matrix4x4 rotate(Rotation rotation) {

        
        return rotateYXZ(-Math.toRadians(rotation.yaw), Math.toRadians(rotation.pitch), Math.toRadians(rotation.roll));
    }

    public Matrix4x4 rotateYXZ(double angleY, double angleX, double angleZ) {

        double sinX = Math.sin(angleX);
        double cosX = cosFromSinInternal(sinX, angleX);
        double sinY = Math.sin(angleY);
        double cosY = cosFromSinInternal(sinY, angleY);
        double sinZ = Math.sin(angleZ);
        double cosZ = cosFromSinInternal(sinZ, angleZ);
        double m_sinY = -sinY;
        double m_sinX = -sinX;
        double m_sinZ = -sinZ;

        double nm20 = m00 * sinY + m20 * cosY;
        double nm21 = m01 * sinY + m21 * cosY;
        double nm22 = m02 * sinY + m22 * cosY;
        double nm23 = m03 * sinY + m23 * cosY;
        double nm00 = m00 * cosY + m20 * m_sinY;
        double nm01 = m01 * cosY + m21 * m_sinY;
        double nm02 = m02 * cosY + m22 * m_sinY;
        double nm03 = m03 * cosY + m23 * m_sinY;

        double nm10 = m10 * cosX + nm20 * sinX;
        double nm11 = m11 * cosX + nm21 * sinX;
        double nm12 = m12 * cosX + nm22 * sinX;
        double nm13 = m13 * cosX + nm23 * sinX;

        this.m20 = m10 * m_sinX + nm20 * cosX;
        this.m21 = m11 * m_sinX + nm21 * cosX;
        this.m22 = m12 * m_sinX + nm22 * cosX;
        this.m23 = m13 * m_sinX + nm23 * cosX;

        this.m00 = nm00 * cosZ + nm10 * sinZ;
        this.m01 = nm01 * cosZ + nm11 * sinZ;
        this.m02 = nm02 * cosZ + nm12 * sinZ;
        this.m03 = nm03 * cosZ + nm13 * sinZ;
        this.m10 = nm00 * m_sinZ + nm10 * cosZ;
        this.m11 = nm01 * m_sinZ + nm11 * cosZ;
        this.m12 = nm02 * m_sinZ + nm12 * cosZ;
        this.m13 = nm03 * m_sinZ + nm13 * cosZ;

        
        return this;
    }

    public Matrix4x4 rotateZYX(double angleZ, double angleY, double angleX) {

        double sinX = Math.sin(angleX);
        double cosX = cosFromSinInternal(sinX, angleX);
        double sinY = Math.sin(angleY);
        double cosY = cosFromSinInternal(sinY, angleY);
        double sinZ = Math.sin(angleZ);
        double cosZ = cosFromSinInternal(sinZ, angleZ);
        double m_sinZ = -sinZ;
        double m_sinY = -sinY;
        double m_sinX = -sinX;

        double nm00 = m00 * cosZ + m10 * sinZ;
        double nm01 = m01 * cosZ + m11 * sinZ;
        double nm02 = m02 * cosZ + m12 * sinZ;
        double nm03 = m03 * cosZ + m13 * sinZ;
        double nm10 = m00 * m_sinZ + m10 * cosZ;
        double nm11 = m01 * m_sinZ + m11 * cosZ;
        double nm12 = m02 * m_sinZ + m12 * cosZ;
        double nm13 = m03 * m_sinZ + m13 * cosZ;

        double nm20 = nm00 * sinY + m20 * cosY;
        double nm21 = nm01 * sinY + m21 * cosY;
        double nm22 = nm02 * sinY + m22 * cosY;
        double nm23 = nm03 * sinY + m23 * cosY;
        m00 = nm00 * cosY + m20 * m_sinY;
        m01 = nm01 * cosY + m21 * m_sinY;
        m02 = nm02 * cosY + m22 * m_sinY;
        m03 = nm03 * cosY + m23 * m_sinY;

        m10 = nm10 * cosX + nm20 * sinX;
        m11 = nm11 * cosX + nm21 * sinX;
        m12 = nm12 * cosX + nm22 * sinX;
        m13 = nm13 * cosX + nm23 * sinX;
        m20 = nm10 * m_sinX + nm20 * cosX;
        m21 = nm11 * m_sinX + nm21 * cosX;
        m22 = nm12 * m_sinX + nm22 * cosX;
        m23 = nm13 * m_sinX + nm23 * cosX;

        

        return this;
    }

    public Matrix4x4 rotate(double yaw, double pitch, double roll) {

        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);

        

        double c1 = Math.cos(yaw);
        double c2 = Math.cos(pitch);
        double c3 = Math.cos(roll);

        double s1 = Math.sin(yaw);
        double s2 = Math.sin(pitch);
        double s3 = Math.sin(roll);

        Matrix4x4 rotationMatrix = new Matrix4x4(
                c1*c2, c1*s2*s3 - s1*c3, c1*s2*c3 + s1*s3, 0,
                s1*c2, s1*s2*s3 + c1*c3, s1*s2*c3 - c1*s3, 0,
                -s2, c2*s3, c2*c3, 0,
                0,0,0,1
                );

        return this.mul(rotationMatrix, this);
    }

    public Point getTranslation(@Nullable Point target) {
        if (target == null)
            target = new Point();

        target.x = m30;
        target.y = m31;
        target.z = m32;

        return target;
    }

    public Rotation getRotation(@Nullable Rotation target) {
        if (target == null)
            target = new Rotation();

        double x = 0, y = 0, z = 0, w = 0;

        double t;
        double tr = m00 + m11 + m22;
        if (tr >= 0.0) {
            t = Math.sqrt(tr + 1.0);
            w = t * 0.5;
            t = 0.5 / t;
            x = (m12 - m21) * t;
            y = (m20 - m02) * t;
            z = (m01 - m10) * t;
        } else {
            if (m00 >= m11 && m00 >= m22) {
                t = Math.sqrt(m00 - (m11 + m22) + 1.0);
                x = t * 0.5;
                t = 0.5 / t;
                y = (m10 + m01) * t;
                z = (m02 + m20) * t;
                w = (m12 - m21) * t;
            } else if (m11 > m22) {
                t = Math.sqrt(m11 - (m22 + m00) + 1.0);
                y = t * 0.5;
                t = 0.5 / t;
                z = (m21 + m12) * t;
                x = (m10 + m01) * t;
                w = (m20 - m02) * t;
            } else {
                t = Math.sqrt(m22 - (m00 + m11) + 1.0);
                z = t * 0.5;
                t = 0.5 / t;
                x = (m02 + m20) * t;
                y = (m21 + m12) * t;
                w = (m01 - m10) * t;
            }
        }

        target.roll = Math.toDegrees(Math.atan2(2.0 * (x*w - y*z), 1.0 - 2.0 * (x*x + y*y)));
        target.yaw = Math.toDegrees(Math.asin(2.0 * (x*z + y*w)));
        target.pitch = Math.toDegrees(Math.atan2(2.0 * (z*w - x*y), 1.0 - 2.0 * (y*y + z*z)));

        target.ensure();

        return target;
    }

    final Matrix4x4 identity() {
        m00 = 1;
        m01 = 0;
        m02 = 0;
        m03 = 0;

        m10 = 0;
        m11 = 1;
        m12 = 0;
        m13 = 0;

        m20 = 0;
        m21 = 0;
        m22 = 1;
        m23 = 0;

        m30 = 0;
        m31 = 0;
        m32 = 0;
        m33 = 1;

        return this;
    }

    public static final double PI = Math.PI;
    static final double PI2 = PI * 2.0;
    static final float PI_f = (float) Math.PI;
    static final float PI2_f = PI_f * 2.0f;
    static final double PIHalf = PI * 0.5;
    static final float PIHalf_f = (float) (PI * 0.5);
    static final double PI_4 = PI * 0.25;
    static final double PI_INV = 1.0 / PI;

    private static double cosFromSinInternal(double sin, double angle) {

        double cos = Math.sqrt(1.0f - sin * sin);
        double a = angle + PIHalf_f;
        double b = a - (int)(a / PI2_f) * PI2_f;
        if (b < 0.0)
            b = PI2_f + b;
        if (b >= PI_f)
            return -cos;
        return cos;
    }

    void set(Matrix4x4 local) {
        this.m00 = local.m00;
        this.m01 = local.m01;
        this.m02 = local.m02;
        this.m03 = local.m03;

        this.m10 = local.m10;
        this.m11 = local.m11;
        this.m12 = local.m12;
        this.m13 = local.m13;

        this.m20 = local.m20;
        this.m21 = local.m21;
        this.m22 = local.m22;
        this.m23 = local.m23;

        this.m30 = local.m30;
        this.m31 = local.m31;
        this.m32 = local.m32;
        this.m33 = local.m33;
    }

    Vector getForward(@Nullable Vector target) {
        if (target == null)
            target = new Vector();

        Matrix4x4 tag = this.mul(FORWARD, null);

        target.setX(tag.m30 - this.m30).setY(tag.m31 - this.m31).setZ(tag.m32 - this.m32);

        return target;
    }

    Vector getRight(@Nullable Vector target) {
        if (target == null)
            target = new Vector();

        Matrix4x4 tag = this.mul(RIGHT, null);

        target.setX(tag.m30 - this.m30).setY(tag.m31 - this.m31).setZ(tag.m32 - this.m32);

        return target;
    }

    Vector getUp(@Nullable Vector target) {
        if (target == null)
            target = new Vector();

        Matrix4x4 tag = this.mul(UP, null);

        target.setX(tag.m30 - this.m30).setY(tag.m31 - this.m31).setZ(tag.m32 - this.m32);

        return target;
    }

}
