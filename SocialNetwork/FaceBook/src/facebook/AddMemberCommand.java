package facebook;

public class AddMemberCommand implements Command {
    private Group group;
    private User user;

    public AddMemberCommand(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    @Override
    public void execute() {
        group.addMember(user);
    }

    @Override
    public void undo() {
        group.removeMember(user);
    }
}

