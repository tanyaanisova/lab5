package Group;

import java.util.*;


public class Commander {

    private CollectionManager manager;
    private String userCommand;
    private String[] finalUserCommand;
    private Scanner commandReader = new Scanner(System.in);
    private boolean wrong;
    protected static ArrayList<String> last_commands = new ArrayList<>();

    {
        userCommand = "";
        wrong = false;
        for (int i = 0; i < 8; i++) {
            last_commands.add("");
        }
    }

    public Commander(CollectionManager manager) {
        this.manager = manager;
    }

    public void interactiveMod() {
        try {
            while (!userCommand.equals("exit")) {
                System.out.print(">> ");
                userCommand = commandReader.nextLine();
                finalUserCommand = userCommand.trim().split(" ", 2);
                choseCommand();
            }
        } catch (NoSuchElementException ex){
            System.exit(1);
        }
    }
    public void choseCommand() {
        try {
            last_commands.add(finalUserCommand[0]);
            switch (finalUserCommand[0]) {
                case "":
                case "exit":
                    if (finalUserCommand.length > 1) {
                        if (!finalUserCommand[1].equals("")) wrong = true;
                        else System.exit(1);
                    } else System.exit(1);
                    break;
                case "help":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.help();
                        else wrong = true;
                    } else manager.help();
                    break;
                case "info":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) System.out.println(manager.toString());
                        else wrong = true;
                    } else System.out.println(manager.toString());
                    break;
                case "show":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.show();
                        else wrong = true;
                    } else manager.show();
                    break;
                case "add":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.add();
                        else wrong = true;
                    } else manager.add();
                    break;
                case "execute_script":
                    ArrayList<String> commands_of_script =new ArrayList<>();
                    manager.execute_script(finalUserCommand[1], commands_of_script);
                    if (commands_of_script.size() != 0) {
                        for (String command : commands_of_script) {
                            System.out.println(">> " + command);
                            finalUserCommand = command.trim().split(" ", 2);
                            choseCommand();
                        }
                        CollectionManager.scripts.remove(CollectionManager.scripts.size()-1);
                    }
                    break;
                case "update":
                    manager.update(finalUserCommand[1]);
                    break;
                case "remove_by_id":
                    manager.remove_by_id(finalUserCommand[1]);
                    break;
                case "clear":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.clear();
                        else wrong = true;
                    } else manager.clear();
                    break;
                case "save":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.save();
                        else wrong = true;
                    } else manager.save();
                    break;
                case "add_if_max":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.add_if_max();
                        else wrong = true;
                    } else manager.add_if_max();
                    break;
                case "add_if_min":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.add_if_min();
                        else wrong = true;
                    } else manager.add_if_min();
                    break;
                case "history":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) {
                            last_commands.remove(last_commands.size()-1);
                            for (String last_command : last_commands) {
                                if(!last_command.equals("")) System.out.println(last_command);
                            }
                            last_commands.add("history");
                        } else wrong = true;
                    } else {
                        last_commands.remove(last_commands.size()-1);
                        for (String last_command : last_commands) {
                            if (!last_command.equals("")) System.out.println(last_command);
                        }
                        last_commands.add("history");
                    }
                    break;
                case "average_of_students_count":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.average_of_students_count();
                        else wrong = true;
                    } else manager.average_of_students_count();
                    break;
                case "count_by_group_admin":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.count_by_group_admin();
                        else wrong = true;
                    } else manager.count_by_group_admin();
                    break;
                case "count_greater_than_group_admin":
                    if (finalUserCommand.length > 1) {
                        if (finalUserCommand[1].equals("")) manager.count_greater_than_group_admin();
                        else wrong = true;
                    } else manager.count_greater_than_group_admin();
                    break;
                default:
                    wrong = true;
            }
            last_commands.remove(0);
        if (wrong) {
            System.out.println("Неопознанная команда. Наберите 'help' для справки.");
            last_commands.remove(last_commands.size() - 1);
            wrong = false;
        }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Отсутствует аргумент");
            last_commands.remove(last_commands.size()-1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commander)) return false;
        Commander commander = (Commander) o;
        return manager.equals(commander.manager);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(manager, userCommand);
        result = 31 * result + Arrays.hashCode(finalUserCommand);
        return result;
    }
}