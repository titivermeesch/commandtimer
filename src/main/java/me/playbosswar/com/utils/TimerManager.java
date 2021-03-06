package me.playbosswar.com.utils;

import me.playbosswar.com.Tools;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Timer;

public class TimerManager {
    private static ArrayList<CommandTimer> timers = new ArrayList<>();

    public static void addCommandTimer(CommandTimer t) {
        timers.add(t);
    }

    public static void removeCommandTimer(CommandTimer t) {
        timers.remove(t);
    }

    /**
     * Find timer by name
     *
     * @param name
     * @return
     */
    public static CommandTimer getCommandTimer(String name) {
        for (CommandTimer t : timers) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public static ArrayList<CommandTimer> getAllTimers() {
        return timers;
    }

    public static void cancelAllTimers() {
        for (Timer t : Tools.timerList) {
            t.cancel();
        }
        timers.clear();
    }

    /**
     * Create a new Timer (file and instance)
     *
     * @param p
     * @param name
     */
    public static void createNewCommandTimer(Player p, String name) {
        try {
            Files.createNewCommandTimerDataFile(name);
            CommandTimer t = new CommandTimer(name);
            TimerManager.addCommandTimer(t);
            Messages.sendMessageToPlayer(p, "A new timer has been created");
        } catch (FileAlreadyExistsException e) {
            Messages.sendMessageToPlayer(p, "&cThis name is already used");
        } catch (IOException e) {
            e.printStackTrace();
            Messages.sendMessageToPlayer(p, "&cCould not save a file to your server disk");
        }
    }

    public static void addCommandToTimer(Player p, CommandTimer timer, String command) {
        try {
            timer.addCommand(command);
            Files.changeDataInFile(timer.getName(), "commands", timer.getCommands());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeCommandFromTimer(Player p, CommandTimer timer, int commandIndex) {
        try {
            ArrayList<String> commands = timer.getCommands();
            commands.remove(commandIndex);
            timer.setCommands(commands);
            Files.changeDataInFile(timer.getName(), "commands", timer.getCommands());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void addTimeToTimer(Player p, CommandTimer timer, String time) {
        try {
            timer.addTime(time);
            Files.changeDataInFile(timer.getName(), "times", timer.getTimes());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeTimeFromTimer(Player p, CommandTimer timer, int timeIndex) {
        try {
            timer.removeTime(timeIndex);
            Files.changeDataInFile(timer.getName(), "times", timer.getTimes());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeWorldFromTimer(Player p, CommandTimer timer, int worldIndex) {
        try {
            ArrayList<String> worlds = timer.getWorlds();
            worlds.remove(worldIndex);
            timer.setWorlds(worlds);
            Files.changeDataInFile(timer.getName(), "worlds", timer.getWorlds());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void addWorldToTimer(Player p, CommandTimer timer, String world) {
        try {
            timer.addWorld(world);
            Files.changeDataInFile(timer.getName(), "worlds", timer.getWorlds());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete existing Timer (file and instance)
     *
     * @param p
     * @param name
     */
    public static void deleteCommandtimer(Player p, String name) {
        try {
            CommandTimer timer = getCommandTimer(name);
            Files.removeExistingCommandTimer(name);
            removeCommandTimer(timer);
            Messages.sendMessageToPlayer(p, "Timer has been deleted");
        } catch (FileNotFoundException e) {
            Messages.sendMessageToPlayer(p, "&cCould not find this Timer");
        } catch (IOException e) {
            e.printStackTrace();
            Messages.sendMessageToPlayer(p, "&cWe could not delete this file from your server disk");
        }
    }

    public static void changeCommandtimerData(Player p, String timerName, String param, String value) {
        CommandTimer timer = TimerManager.getCommandTimer(timerName);

        if (timer == null) {
            Messages.sendMessageToPlayer(p, "&cCould not find timer");
            return;
        }

        if (param.equalsIgnoreCase("gender")) {
            Gender gender = Gender.valueOf(value.toUpperCase());

            if (gender == null) {
                Messages.sendMessageToPlayer(p, "&cThis is not a valid gender, please use one of the following : operator/user/console");
                return;
            }

            timer.setGender(gender);
            try {
                Files.changeDataInFile(timerName, "gender", gender.toString());
                Messages.sendMessageToPlayer(p, "Gender has been updated");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }
            return;
        }

        if (param.equalsIgnoreCase("seconds")) {
            int seconds = Integer.parseInt(value);

            if (seconds < 1) {
                Messages.sendMessageToPlayer(p, "&cPlease use a value greater than 0");
                return;
            }

            timer.setSeconds(seconds);

            try {
                Files.changeDataInFile(timerName, "seconds", seconds);
                Messages.sendMessageToPlayer(p, "Seconds has been updated");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }
            return;
        }

        if (param.equalsIgnoreCase("useMinecraftTime")) {
            if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                Messages.sendMessageToPlayer(p, "&cPlease use one of the following : true/false");
                return;
            }

            timer.setUseMinecraftTime(value.equalsIgnoreCase("true"));

            try {
                Files.changeDataInFile(timerName, "useMinecraftTime", value.equalsIgnoreCase("true"));
                Messages.sendMessageToPlayer(p, "UseMinecraftTime has been updated");
            } catch (ParseException | IOException e) {
                Messages.sendFailedIO(p);
                e.printStackTrace();
            }
            return;
        }

        if (param.equalsIgnoreCase("selectRandomCommand")) {
            if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                Messages.sendMessageToPlayer(p, "&cPlease use one of the following : true/false");
                return;
            }

            timer.setSelectRandomCommand(value.equalsIgnoreCase("true"));

            try {
                Files.changeDataInFile(timerName, "selectRandomCommand", value.equalsIgnoreCase("true"));
                Messages.sendMessageToPlayer(p, "SelectRandomCommand has been updated");
            } catch (ParseException | IOException e) {
                Messages.sendFailedIO(p);
                e.printStackTrace();
            }
            return;
        }

        if (param.equalsIgnoreCase("random")) {
            double randomValue = Double.parseDouble(value);

            if (randomValue < 0 || randomValue > 1) {
                Messages.sendMessageToPlayer(p, "&cPlease use a value between 0 and 1 included. 0 means never executed and 1 means always executed. You can pick a value between those as well.");
                return;
            }

            timer.setRandom(randomValue);

            try {
                Files.changeDataInFile(timerName, "random", randomValue);
                Messages.sendMessageToPlayer(p, "Random has been updated");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }

            return;
        }

        if (param.equalsIgnoreCase("executePerUser")) {
            if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                Messages.sendMessageToPlayer(p, "&cPlease use one of the following : true/false");
                return;
            }

            timer.setExecutePerUser(value.equalsIgnoreCase("true"));

            try {
                Files.changeDataInFile(timerName, "executePerUser", value.equalsIgnoreCase("true"));
                Messages.sendMessageToPlayer(p, "ExecutePerUser has been updated");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }
            return;
        }

        if (param.equalsIgnoreCase("executionLimit")) {
            int limit = Integer.parseInt(value);

            if (limit < -1) {
                Messages.sendMessageToPlayer(p, "&cPlease use -1 to disable limit or any other value above it");
                return;
            }

            timer.setExecutionLimit(limit);

            try {
                Files.changeDataInFile(timerName, "executionLimit", limit);
                Messages.sendMessageToPlayer(p, "ExecutionLimit has been updated");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }
        }

        if (param.equalsIgnoreCase("minPlayers")) {
            int limit = Integer.parseInt(value);

            if (limit < -1) {
                Messages.sendMessageToPlayer(p, "&cPlease use -1 to disable limit or any other value above it");
                return;
            }

            timer.setMinPlayers(limit);

            try {
                Files.changeDataInFile(timerName, "minPlayers", limit);
                Messages.sendMessageToPlayer(p, "MinPlayers has been updated");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }
        }

        if (param.equalsIgnoreCase("maxPlayers")) {
            int limit = Integer.parseInt(value);

            if (limit < -1) {
                Messages.sendMessageToPlayer(p, "&cPlease use -1 to disable limit or any other value above it");
                return;
            }

            timer.setMaxPlayers(limit);

            try {
                Files.changeDataInFile(timerName, "maxPlayers", limit);
                Messages.sendMessageToPlayer(p, "MaxPlayers has been updated");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                Messages.sendFailedIO(p);
            }
        }
    }
}
