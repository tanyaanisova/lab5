package Group;

import java.util.Objects;

public class Coordinates implements Comparable<Coordinates> {
    private Float x; //Поле не может быть null
    private Long y; //Поле не может быть null

    Coordinates(Float x, Long y){
        this.x = x;
        this.y = y;
    }

    public Float getX() {
        return x;
    }
    public Long getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordibates {" +
                "x: " + x + ", y: " + y +  "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates coordinates = (Coordinates) o;
        return x.equals(coordinates.getX()) &&
                y.equals(coordinates.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

    @Override
    public int compareTo(Coordinates o) {
        return (int) (x*x + y*y - o.getX()*o.getX() - o.getY()*o.getY());
    }
}
