package Group;

import java.util.Objects;

public class Location implements Comparable<Location> {
        private double x;
        private long y;
        private Integer z; //Поле не может быть null
        private String name; //Строка не может быть пустой, Поле не может быть null

        Location(double x, long y, Integer z, String name) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.name = name;
        }

        public String getName() {
                return name;
        }
        public double getX() {
                return x;
        }
        public long getY() {
                return y;
        }
        public Integer getZ() {
                return z;
        }

        @Override
        public String toString() {
                return "Location { " +
                        "x: " + x + ", y: " + y + ", z: " + z + ", name: " + name + "}";
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Location)) return false;
                Location location = (Location) o;
                return x == location.getX() &&
                        y == location.getY() &&
                        z.equals(location.getZ()) &&
                        name.equals(location.getName());
        }

        @Override
        public int compareTo(Location e) {
                if (x*x + y*y + z*z - e.getX()*e.getX() - e.getY()*e.getY() - e.getZ()*e.getZ() != 0) return (int) (x*x + y*y + z*z - e.getX()*e.getX() - e.getY()*e.getY() - e.getZ()*e.getZ());
                return name.compareTo(e.getName());
        }

        @Override
        public int hashCode() {
                return Objects.hash(x, y, z, name);
        }
}