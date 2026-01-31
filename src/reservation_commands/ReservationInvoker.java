package reservation_commands;

import java.util.Stack;

public class ReservationInvoker {
    private Stack<Command> commandHistory = new Stack<>();
    
    public boolean executeCommand(Command command) {
        boolean success = command.execute();
        if (success) {
            commandHistory.push(command);
        }
        return success;
    }
    
    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command command = commandHistory.pop();
            command.undo();
        }
    }
    
    public void undoAllCommands() {
        while (!commandHistory.isEmpty()) {
            undoLastCommand();
        }
    }
} 