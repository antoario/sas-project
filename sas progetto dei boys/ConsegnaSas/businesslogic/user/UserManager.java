package businesslogic.user;

import main.businesslogic.user.User;

public class UserManager {
    private User currentUser;

    public void fakeLogin(String username) //TODO: bisogna implementare il login vero!
    {
        this.currentUser = User.loadUser(username);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }
}
