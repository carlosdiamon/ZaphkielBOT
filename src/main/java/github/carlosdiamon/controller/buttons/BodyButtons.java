package github.carlosdiamon.controller.buttons;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class BodyButtons {

    private User author;
    private String actionName;
    public BodyButtons(String actionName) {
        this.actionName = actionName;
    }
    public abstract void interaction(ButtonInteractionEvent event, String button);

    public String getActionName() {return actionName;}
    public void setAuthor(User author) {this.author = author;}
    public User getAuthor() {return author;}
}
