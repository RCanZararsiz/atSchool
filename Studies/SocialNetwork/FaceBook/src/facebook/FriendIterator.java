package facebook;

import java.util.List;
import java.util.NoSuchElementException;

public class FriendIterator implements Iterator<User> {
    private List<User> friends;
    private int position;

    public FriendIterator(List<User> friends) {
        this.friends = friends;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < friends.size();
    }

    @Override
    public User next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return friends.get(position++);
    }
}
