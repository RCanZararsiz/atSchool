package facebook;

public class RemoveMemberCommand implements Command {
    private Group group;
    private User user;

    public RemoveMemberCommand(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    @Override
    public void execute() {
        group.removeMember(user);
    }

    @Override
    public void undo() {
        group.addMember(user);
    }
}


