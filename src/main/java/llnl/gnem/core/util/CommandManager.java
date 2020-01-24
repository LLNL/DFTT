package llnl.gnem.core.util;

import javax.swing.*;
import java.util.Stack;

/**
 * Created by dodge1 Date: Mar 24, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class CommandManager {
    private final Stack<Command> history;
    private final Stack<Command> redoList;
    private Action undoAction;
    private Action redoAction;

    private enum RunDirection {
        runForward, runBackward
    }

    public CommandManager() {
        history = new Stack<Command>();
        redoList = new Stack<Command>();
    }

    public void registerUndoAction(Action action) {
        undoAction = action;
        updateActions();
    }

    public void registerRedoAction(Action action) {
        redoAction = action;
        updateActions();
    }

    public void clear() {
        history.clear();
        redoList.clear();
        updateActions();
    }

    public void updateActions() {
        if (redoAction != null) {
            redoAction.setEnabled(!redoList.isEmpty());
        }
        if (undoAction != null) {
            undoAction.setEnabled(!history.isEmpty());
        }

    }

    public void invokeCommand(Command command) {
        runCommand(command);
        pushCommand(command);
    }

    public void pushCommand(Command command) {
        if (command.isReversible()) {
            history.push(command);
        }
        redoList.clear();
        updateActions();
    }

    public void undo() {
        if (!canUndo()) {
            return;
        }

        Command command = history.pop();
        if (command != null) {
            redoList.push(command);
            if (command.isRunInNewThread()) {
                runInThread(command, RunDirection.runBackward);
            } else {
                command.unexecute();
            }
        }
        updateActions();
    }

    public void redo() {
        if (!canRedo()) {
            return;
        }

        Command command = redoList.pop();
        if (command != null) {
            history.push(command);
            command.execute();
        }
        updateActions();
    }

    public void undoAll() {
        while (!history.isEmpty()) {
            undo();
        }
    }

    public void redoAll() {
        while (!redoList.isEmpty()) {
            redo();
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public boolean canRedo() {
        return !redoList.isEmpty();
    }

    public static void runCommand(Command command) {
        if (command.isRunInNewThread()) {
            runInThread(command, RunDirection.runForward);
        } else {
            command.execute();
        }
    }

    private static void runInThread(Command cmd, RunDirection direction) {
        if (direction == RunDirection.runForward) {
            Thread t = new Thread(new CommandRunner(cmd));
            t.start();
        } else {
            Thread t = new Thread(new CommandUnRunner(cmd));
            t.start();
        }
    }

    private static class CommandRunner implements Runnable {
        private final Command cmd;

        private CommandRunner(Command cmd) {
            this.cmd = cmd;
        }

        public void run() {
            cmd.execute();
        }
    }

    private static class CommandUnRunner implements Runnable {
        private final Command cmd;

        private CommandUnRunner(Command cmd) {
            this.cmd = cmd;
        }

        public void run() {
            cmd.unexecute();
        }
    }
}