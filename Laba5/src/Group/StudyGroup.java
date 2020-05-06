package Group;

import java.time.ZonedDateTime;
import java.util.*;

public class StudyGroup implements Comparable<StudyGroup> {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int studentsCount; //Значение поля должно быть больше 0
    private long expelledStudents; //Значение поля должно быть больше 0
    private Float averageMark; //Значение поля должно быть больше 0, Поле не может быть null
    private Semester semesterEnum; //Поле не может быть null
    private Person groupAdmin; //Поле не может быть null

    public StudyGroup(int id, String name, Coordinates coordinates, ZonedDateTime creationDate, int studentsCount, long expelledStudents, Float averageMark, Semester semesterEnum, Person groupAdmin){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.studentsCount = studentsCount;
        this.expelledStudents = expelledStudents;
        this.averageMark = averageMark;
        this.semesterEnum = semesterEnum;
        this.groupAdmin = groupAdmin;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates(){
        return coordinates;
    }
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }
    public int getStudentsCount() {
        return studentsCount;
    }
    public long getExpelledStudents() {
        return expelledStudents;
    }
    public Float getAverageMark() {
        return averageMark;
    }
    public Semester getSemesterEnum() {
        return semesterEnum;
    }
    public Person getGroupAdmin() {
        return groupAdmin;
    }

    @Override
    public int compareTo(StudyGroup e) {
        if (semesterEnum.compareTo(e.getSemesterEnum()) != 0) return semesterEnum.compareTo(e.getSemesterEnum());
        if (averageMark.compareTo(e.getAverageMark()) != 0) return (averageMark.compareTo(e.getAverageMark()));
        if (studentsCount - e.getStudentsCount() != 0) return studentsCount - e.getStudentsCount();
        if (e.getExpelledStudents() - expelledStudents != 0) return (int) (e.getExpelledStudents() - expelledStudents);
        if (coordinates.compareTo(e.getCoordinates()) != 0) return coordinates.compareTo(e.getCoordinates());
        if (name.compareTo(e.getName()) != 0) return name.compareTo(e.getName());
        return groupAdmin.compareTo(e.getGroupAdmin());
    }

    @Override
    public String toString() {
        return "StudyGroup {" +
                "id: " + id + ", name: " + name + ", " + coordinates.toString() + ", creation date: " + creationDate.toString() + ", students count: "
                + studentsCount + ", expelled students: " + expelledStudents + ", average mark: " + averageMark +
                ", Semester: " + semesterEnum.toString() + ", " + groupAdmin.toString() + "}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyGroup)) return false;
        StudyGroup group = (StudyGroup) o;
        return id == group.getId() &&
                studentsCount == group.getStudentsCount() &&
                averageMark.equals(group.getAverageMark()) &&
                name.equals(group.getName()) &&
                coordinates.equals(group.getCoordinates()) &&
                creationDate.equals(group.getCreationDate()) &&
                expelledStudents == group.getExpelledStudents() &&
                semesterEnum.equals(group.getSemesterEnum()) &&
                groupAdmin.equals(group.getGroupAdmin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, studentsCount, expelledStudents, averageMark, semesterEnum, groupAdmin);
    }
}