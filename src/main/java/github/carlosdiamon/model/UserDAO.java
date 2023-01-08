package github.carlosdiamon.model;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class UserDAO {
    private User user;
    private Member member;

    public UserDAO(User user, Member member) {
        this.user = user;
        this.member = member;
    }

    public User getUser() {
        return user;
    }

    public Member getMember() {
        return member;
    }

}
