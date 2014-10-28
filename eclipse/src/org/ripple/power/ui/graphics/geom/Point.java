package org.ripple.power.ui.graphics.geom;

import java.io.Serializable;

public class Point implements Comparable<Point>, Serializable {
private static final long serialVersionUID = 1L;
/**
* The x coordinate of this point.
*/
public final double x;
/**
* The y coordinate of this point.
*/
public final double y;
/**
* @param x
* the x coordinate of this point.
* @param y
* the y coordinate of this point.
*/
public Point(double x, double y) {
this.x = x;
this.y = y;
}
@Override
public int compareTo(Point point) {
if (this.x > point.x) {
return 1;
} else if (this.x < point.x) {
return -1;
} else if (this.y > point.y) {
return 1;
} else if (this.y < point.y) {
return -1;
}
return 0;
}
/**
* @return the euclidian distance from this point to the given point.
*/
public double distance(Point point) {
return Math.hypot(this.x - point.x, this.y - point.y);
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
} else if (!(obj instanceof Point)) {
return false;
}
Point other = (Point) obj;
if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
return false;
} else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
return false;
}
return true;
}
@Override
public int hashCode() {
final int prime = 31;
int result = 1;
long temp;
temp = Double.doubleToLongBits(this.x);
result = prime * result + (int) (temp ^ (temp >>> 32));
temp = Double.doubleToLongBits(this.y);
result = prime * result + (int) (temp ^ (temp >>> 32));
return result;
}
@Override
public String toString() {
StringBuilder stringBuilder = new StringBuilder();
stringBuilder.append("x=");
stringBuilder.append(this.x);
stringBuilder.append(", y=");
stringBuilder.append(this.y);
return stringBuilder.toString();
}
}