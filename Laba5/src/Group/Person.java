package Group;

import java.util.Objects;

public class Person implements Comparable<Person> {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Double height; //Поле может быть null, Значение поля должно быть больше 0
    private long weight; //Значение поля должно быть больше 0
    private Location location; //Поле может быть null

    Person(String name, Double height, long weight, Location location) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.location = location;
    }

    public long getWeight() {
        return weight;
    }
    public Double getHeight() {
        return height;
    }
    public Location getLocation() {
        return location;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (location == null) return "GroupAdmin {" +
                "name: " + name + ", height: " + height + ", weight: " + weight + ", Location: " + null + "}";
        else return "Person {" +
                "name: " + name + ", height: " + height + ", weight: " + weight + ", " + location.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return name.equals(person.getName()) &&
                height.equals(person.getHeight()) &&
                weight == person.getWeight() &&
                location.equals(person.getLocation());
    }

    @Override
    public int compareTo(Person e) {
        if (height.compareTo(e.getHeight()) != 0) return (height.compareTo(e.getHeight()));
        if (weight - e.getWeight() != 0) return (int) (weight - e.getWeight());
        if (location != null && e.getLocation() == null) return 1;
        else if (location == null && e.getLocation() != null) return -1;
        else if (location != null && e.getLocation() != null && location.compareTo(e.getLocation()) != 0) return location.compareTo(e.getLocation());
        return name.compareTo(e.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, weight, location);
    }
}
