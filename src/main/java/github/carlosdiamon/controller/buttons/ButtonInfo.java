package github.carlosdiamon.controller.buttons;

import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.controller.commands.UserInfoCommand;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonInfo extends BodyButtons{
    public ButtonInfo(String actionName) {
        super(actionName);
    }

    @Override
    public void interaction(ButtonInteractionEvent event, String button) {
        MessageChannelUnion channel = ListenerMessage.getChannelUnion();
        int option = 1; // Banner por defecto
        switch (button) {
            case "USER":
                option = 2;
                break;
            case "SERVER":
                option = 3;
                break;
        }
        UserInfoCommand.guiOptions(event.getMessageId(), channel, option);
    }
}
