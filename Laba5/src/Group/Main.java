package Group;

import java.io.IOException;
/**
 * @autors Tatyana Anisova
 * @year 2020
 */
public class Main {
    public static void main(String[] args) throws IOException{
        try {
            String s = args[0];
            Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Работа программы завершена!")));
            Commander commander = new Commander(new CollectionManager(s));
            commander.interactiveMod();
        } catch(ArrayIndexOutOfBoundsException ex) {
            System.out.println("Путь до файла xml нужно передать через аргумент командной строки.");
            System.exit(1);
        }
    }
}

