package Group;



import jdk.nashorn.internal.objects.Global;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.management.modelmbean.XMLParseException;
import java.io.*;
import java.time.*;
import java.util.*;

public class CollectionManager {
    private TreeSet<StudyGroup> groups;
    protected static ArrayList<String> scripts = new ArrayList<>();
    private String collectionPath;
    private File xmlCollection;
    private Scanner reader = new Scanner(System.in);
    private Date initDate;
    private Integer globalId;
    private Integer nowId;
    private ZonedDateTime globalCreationDate;
    protected static HashMap<String, String> manual;

    {
        groups = new TreeSet<>();
        globalId = null;
        globalCreationDate = null;
        manual = new HashMap<>();
        manual.put("help","вывести справку по доступным командам");
        manual.put("info","вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        manual.put("show","вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        manual.put("add {element}","добавить новый элемент в коллекцию");
        manual.put("update id {element}","обновить значение элемента коллекции, id которого равен заданному");
        manual.put("remove_by_id id","удалить элемент из коллекции по его id");
        manual.put("clear","очистить коллекцию");
        manual.put("save","сохранить коллекцию в файл");
        manual.put("execute_script file_name","считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме");
        manual.put("exit","завершить программу (без сохранения в файл)");
        manual.put("add_if_max {element}","добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        manual.put("add_if_min {element}","добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
        manual.put("history","вывести последние 8 команд (без их аргументов)");
        manual.put("average_of_students_count","вывести среднее значение поля studentsCount для всех элементов коллекции");
        manual.put("count_by_group_admin groupAdmin","вывести количество элементов, значение поля groupAdmin которых равно заданному");
        manual.put("count_greater_than_group_admin groupAdmin","вывести количество элементов, значение поля groupAdmin которых больше заданного");
    }

    public CollectionManager(String collectionPath)  {
        File file = new File(collectionPath);
        if (file.exists()) {
            this.xmlCollection = file;
            this.collectionPath = collectionPath;
        } else {
            System.out.println("Файл по указанному пути не существует.");
            System.exit(1);
        }
        this.load();
        this.initDate = new Date();
    }


    /**
     * Выводит на экран список доступных для пользователя команд
     */
    public void help() {
        System.out.println("Доступные к использованию команды:");
        manual.keySet().forEach(p -> System.out.println(p + " - " + manual.get(p)));
    }

    /**
     * Выводит все элементы коллекции
     */
    public void show() {
        if (groups.size() != 0) {
            groups.forEach(System.out::println);
        }
        else System.out.println("В коллекции отсутствуют элементы. Выполнение команды невозможно.");
    }

    /**
     * Вспомогательные методы для получения полей элемента
     */

    private String readString(String name) {
        System.out.print("Введите " + name +": ");
        return reader.nextLine();
    }

    private String readStringNotNull(String name) {
        System.out.print("Введите " + name +": ");
        String n = reader.nextLine();
        if (n.equals("")) {
            System.out.println("Поле не может быть null или пустой строкой ");
            return readStringNotNull(name);
        } else return n;
    }

    private Number readNumber(String name,String format) {
        String n = readStringNotNull(name);
        try {
            switch (format) {
                case "Float":
                    return Float.parseFloat(n);
                case "Integer":
                    return Integer.parseInt(n);
                case "Long":
                    return Long.parseLong(n);
                case "Double":
                    return Double.parseDouble(n);
                default:
                    return 0;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Аргумент не является значением типа " + format);
            return readNumber(name,format);
        }
    }

    private Semester readSemester(String name) {
        String n = readStringNotNull(name);
        try {
            return Semester.valueOf(n);
        } catch (IllegalArgumentException ex) {
            System.out.println("Значение поля неверное");
            return readSemester(name);
        }
    }

    private Person newPerson() {
        System.out.println("Введите groupAdmin: ");
        String name = readStringNotNull("name");
        Double height = (Double) readNumber("height","Double");
        while (height <= 0) {
            System.out.println("Значение поля должно быть больше 0");
            height = (Double) readNumber("height","Double");
        }
        long weight = (long) readNumber("weight","Long");
        while (weight <= 0) {
            System.out.println("Значение поля должно быть больше 0");
            weight = (long) readNumber("weight","Long");
        }
        String n = readString("location (если хотите установить значение поля null - введите null, при любом другом аргументе будет продолжен ввод)");
        Location location;
        if (n.equals("null")) location = null;
        else location = newLocation();
        return new Person(name,height,weight,location);
    }

    private Location newLocation() {
        double x = (double) readNumber("x","Double");
        long y = (long) readNumber("y","Long");
        Integer z = (Integer) readNumber("z","Integer");
        String name = readStringNotNull("name");
        return new Location(x,y,z,name);
    }

    /**
     * Получает значения элемента в коллекции
     */
    public StudyGroup newGroup() {
        String name = readStringNotNull("name");
        System.out.println("Введите coordinates: ");
        float x = (Float) readNumber("x","Float");
        Long y = (Long) readNumber("y","Long");
        int studentsCount = (int) readNumber("studentsCount","Integer");
        while (studentsCount <= 0) {
            System.out.println("Значение поля должно быть больше 0");
            studentsCount = (int) readNumber("studentsCount","Integer");
        }
        long expelledStudents = (long) readNumber("expelledStudents","Long");
        while (expelledStudents <= 0) {
            System.out.println("Значение поля должно быть больше 0");
            expelledStudents = (long) readNumber("expelledStudents","Long");
        }
        Float averageMark = (Float) readNumber("averageMark","Float");
        while (averageMark <= 0) {
            System.out.println("Значение поля должно быть больше 0");
            averageMark = (Float) readNumber("averageMark","Float");
        }
        Semester semester = readSemester("Semester (SECOND, THIRD, FOURTH)");
        Person admin = newPerson();

        int id;
        if (globalId == null) id = ++nowId;
        else id = globalId;
        ZonedDateTime creationDate;
        if (globalCreationDate == null) creationDate = ZonedDateTime.now();
        else creationDate = globalCreationDate;
        System.out.println("Все значения элемента успешно получены");
        return new StudyGroup(id, name, new Coordinates(x, y), creationDate, studentsCount, expelledStudents, averageMark, semester,admin);
    }

    public void add() {
        groups.add(newGroup());
        System.out.println("Элемент успешно добавлен");
    }

    /**
     * Обновляет значение элемента коллекции, id которого равен заданному
     * @param n : Id элемента, который требуется заменить
     */
    public void update(String n){
        if (groups.size() != 0) {
            try {
                int id = Integer.parseInt(n);
                boolean b = false;
                globalId = id;
                for (StudyGroup group : groups){
                    if (group.getId() == id) {
                        globalCreationDate = group.getCreationDate();
                        groups.remove(group);
                        groups.add(newGroup());
                        System.out.println("Элемент коллекции успешно обновлен.");
                        b = true;
                        break;
                    }
                }
                globalId = null;
                globalCreationDate = null;
                if (!b) System.out.println("В коллекции не найдено элемента с указанным id.");
            } catch (NumberFormatException ex) {
                System.out.println("Аргумент не является значением типа int");
            }
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");

    }

    /**
     * Удаляет элемент из коллекции по его id
     * @param n : id соответствующего элемента, который требуется удалить
     */
    public void remove_by_id(String n){
        if (groups.size() != 0) {
            try {
                boolean b = false;
                int id = Integer.parseInt(n);
                for (StudyGroup group : groups) {
                    if (group.getId() == id) {
                        groups.remove(group);
                        System.out.println("Элемент коллекции успешно удален.");
                        b = true;
                        break;
                    }
                }
                if (!b) System.out.println("В коллекции не найдено элемента с указанным ключом.");
            } catch (NumberFormatException e) {
                System.out.println("Аргумент не является значением типа int");
            }
        }
        else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Удаляет все элементы коллекции.
     */
    public void clear() {
        groups.clear();
        System.out.println("Коллекция очищена.");
    }

    /**
     * Сериализует коллекцию в файл json.
     */
    public void save() {
        try  {
            Document doc = new Document();
            // создаем корневой элемент с пространством имен
            doc.setRootElement(new Element("Groups"));
            // формируем JDOM документ из объектов Student
            for (StudyGroup group : groups) {
                Element element = new Element("StudyGroup");
                element.addContent(new Element("id").setText( String.valueOf(group.getId())));
                element.addContent(new Element("name").setText(group.getName()));
                Element element_c = new Element("Coordinates");
                element_c.addContent(new Element("x").setText(String.valueOf(group.getCoordinates().getX())));
                element_c.addContent(new Element("y").setText(String.valueOf(group.getCoordinates().getY())));
                element.addContent(element_c);
                element.addContent(new Element("creationDate").setText(String.valueOf(group.getCreationDate())));
                element.addContent(new Element("studentsCount").setText(String.valueOf(group.getStudentsCount())));
                element.addContent(new Element("expelledStudents").setText(String.valueOf(group.getExpelledStudents())));
                element.addContent(new Element("averageMark").setText(String.valueOf(group.getAverageMark())));
                element.addContent(new Element("Semester").setText(String.valueOf(group.getSemesterEnum())));
                Element element_d = new Element("groupAdmin");
                element_d.addContent(new Element("name").setText(group.getGroupAdmin().getName()));
                element_d.addContent(new Element("height").setText(String.valueOf(group.getGroupAdmin().getHeight())));
                element_d.addContent(new Element("weight").setText(String.valueOf(group.getGroupAdmin().getWeight())));
                if (group.getGroupAdmin().getLocation() == null) {
                    element_d.addContent(new Element("location").setText(String.valueOf(group.getGroupAdmin().getLocation())));
                } else {
                    Element element_l = new Element("location");
                    element_l.addContent(new Element("x").setText(String.valueOf(group.getGroupAdmin().getLocation().getX())));
                    element_l.addContent(new Element("y").setText(String.valueOf(group.getGroupAdmin().getLocation().getY())));
                    element_l.addContent(new Element("z").setText(String.valueOf(group.getGroupAdmin().getLocation().getZ())));
                    element_l.addContent(new Element("name").setText(group.getGroupAdmin().getLocation().getName()));
                    element_d.addContent(element_l);
                }
                element.addContent(element_d);
                doc.getRootElement().addContent(element);
            }
            if (!xmlCollection.canWrite())
                System.out.println("Файл защищён от записи. Невозможно сохранить коллекцию.");
            else{
                // Документ JDOM сформирован и готов к записи в файл
                XMLOutputter xmlWriter = new XMLOutputter(Format.getPrettyFormat());
                // сохнаряем в файл
                xmlWriter.output(doc, new FileOutputStream(xmlCollection));
                System.out.println("Коллекция успешно сохранена в файл.");
            }
        } catch (IOException ex) {
            System.out.println("Коллекция не может быть записана в файл");
        }
    }

    /**
     * Считывает и исполняет скрипт из указанного файла.
     * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме
     */
    public void execute_script(String file, ArrayList<String> commands_of_script) {
        if (scripts.contains(file)) {
            Commander.last_commands.remove(Commander.last_commands.size() - 1);
            System.out.println("Могло произойти зацикливание при исполнении скрипта: " + file + "\nКоманда не будет выполнена. Переход к следующей команде");
        } else {
            File file1 = new File(file);
            if (!file1.exists())
                System.out.println("Файла с таким названием не существует.");
            else if (!file1.canRead())
                System.out.println("Файл защищён от чтения. Невозможно выполнить скрипт.");
            else {
                scripts.add(file);
                try (InputStreamReader commandReader = new InputStreamReader(new FileInputStream(file1))) {
                    StringBuilder s = new StringBuilder();
                    while (commandReader.ready()) s.append((char) commandReader.read());
                    String[] s1 = s.toString().split("\n");
                    commands_of_script.addAll(Arrays.asList(s1));
                } catch (IOException ex) {
                    System.out.println("Невозможно считать скрипт");
                    scripts.remove(scripts.size()-1);
                }
            }
        }
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
     */
    public void add_if_max() {
        if (groups.size() != 0) {
            StudyGroup sg = newGroup();
            if (sg.compareTo(groups.last()) > 0) {
                groups.add(sg);
                System.out.println("Элемент успешно добавлен");
            } else System.out.println("Элемент не превышает значение наибольшего элемента в коллеции.");
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     */
    public void add_if_min() {
        if (groups.size() != 0) {
            StudyGroup sg = newGroup();
            if (sg.compareTo(groups.first()) < 0) {
                groups.add(sg);
                System.out.println("Элемент успешно добавлен");
            } else System.out.println("Элемент не меньше наименьшего элемента в коллеции.");
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Выводит среднее значение поля studentsСount для всех элементов коллекции
     */
    public void average_of_students_count() {
        if (groups.size() != 0) {
            float sum_students_count = 0;
            for (StudyGroup group: groups) {
                sum_students_count += group.getStudentsCount();
            }
            System.out.println("Cреднее значение поля studentsСount для всех элементов коллекции: " + sum_students_count/groups.size());
        }
        else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Выводит количество элементов, значение поля groupAdmin которых равно заданному
     */
    public void count_by_group_admin(){
        if (groups.size() != 0) {
            Person admin = newPerson();
            int count_group_admin = 0;
            for (StudyGroup group: groups) {
                if (group.getGroupAdmin().compareTo(admin) == 0) count_group_admin++;
            }
            System.out.println("Количество элементов, значение поля groupAdmin которых равно " + admin.toString() + ": " + count_group_admin);
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     * Выводит количество элементов, значение поля groupAdmin которых больше заданного
     */
    public void count_greater_than_group_admin(){
        if (groups.size() != 0) {
            Person admin = newPerson();
            int count_group_admin = 0;
            for (StudyGroup group: groups) {
                if (group.getGroupAdmin().compareTo(admin) > 0) count_group_admin ++;
            }
            System.out.println("Количество элементов, значение поля groupAdmin которых больше " + admin.toString() + ": " + count_group_admin);
        } else System.out.println("В коллекции отсутствуют элементы. Выполнение команды не возможно.");
    }

    /**
     *  Десериализует коллекцию из файла json.
     */
    public void load() {
        int beginSize = groups.size();
        if (!xmlCollection.exists()) {
            System.out.println("Файла по указанному пути не существует.");
            System.exit(1);
        } else if (!xmlCollection.canRead() || !xmlCollection.canWrite()) {
            System.out.println("Файл защищён от чтения и/или записи. Для работы программы нужны оба разрешения.");
            System.exit(1);
        } else {
            if (xmlCollection.length() == 0) {
                System.out.println("Файл пуст.");
                System.exit(1);
            }
            System.out.println("Идёт загрузка коллекции " + xmlCollection.getAbsolutePath());
            // мы можем создать экземпляр JDOM Document из классов DOM, SAX и STAX Builder
            try {
                org.jdom2.Document jdomDocument = createJDOMusingSAXParser(collectionPath);
                Element root = jdomDocument.getRootElement();
                // получаем список всех элементов
                List<Element> groupListElements = root.getChildren("StudyGroup");
                // список объектов Student, в которых будем хранить
                // считанные данные по каждому элементу

                int maxId = 0;
                for (Element group : groupListElements) {
                    int id = Integer.parseInt(group.getChildText("id"));
                    if (id > maxId) maxId = id;
                    String name = group.getChildText("name");
                    List<Element> lab_c = group.getChildren("Coordinates");
                    Float x = Float.parseFloat(lab_c.get(0).getChildText("x"));
                    Long y = Long.parseLong(lab_c.get(0).getChildText("y"));
                    ZonedDateTime creationDate = ZonedDateTime.parse(group.getChildText("creationDate"));
                    int studentsCount = Integer.parseInt(group.getChildText("studentsCount"));
                    long expelledStudents = Long.parseLong(group.getChildText("expelledStudents"));
                    Float averageMark = Float.parseFloat(group.getChildText("averageMark"));
                    Semester semester = Semester.valueOf(group.getChildText("Semester"));
                    List<Element> lab_d = group.getChildren("groupAdmin");
                    String nameAdmin = lab_d.get(0).getChildText("name");
                    Double height = Double.parseDouble(lab_d.get(0).getChildText("height"));
                    long weight = Long.parseLong(lab_d.get(0).getChildText("weight"));
                    Location location;
                    if (lab_d.get(0).getChildText("location").equals("null")) location = null;
                    else {
                        List<Element> lab_l = lab_d.get(0).getChildren("location");
                        double xl = Double.parseDouble(lab_l.get(0).getChildText("x"));
                        long yl = Long.parseLong(lab_l.get(0).getChildText("y"));
                        Integer zl =Integer.parseInt(lab_l.get(0).getChildText("z"));
                        String namel = lab_l.get(0).getChildText("name");
                        location = new Location(xl,yl,zl,namel);
                    }
                    Person admin = new Person(nameAdmin,height,weight,location);
                    groups.add(new StudyGroup(id, name, new Coordinates(x, y), creationDate, studentsCount, expelledStudents, averageMark, semester,admin));
                }
                System.out.println("Коллекция успешно загружена. Добавлено " + (groups.size() - beginSize) + " элементов.");
                nowId = maxId;
            } catch (IOException | JDOMException ex) {
                System.out.println("Коллекция не может быть загружена. Файл некорректен");
                System.exit(1);
            }
        }
    }
    private static org.jdom2.Document createJDOMusingSAXParser(String fileName)
            throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new File(fileName));
    }

    /**
     * Выводит информацию о коллекции.
     */
    @Override
    public String toString() {
        return "Тип коллекции: " + groups.getClass() +
                "\nДата инициализации: " + initDate +
                "\nКоличество элементов: " + groups.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionManager)) return false;
        CollectionManager manager = (CollectionManager) o;
        return groups.equals(manager.groups) &&
                xmlCollection.equals(manager.xmlCollection) &&
                initDate.equals(manager.initDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groups, initDate);
    }
}
