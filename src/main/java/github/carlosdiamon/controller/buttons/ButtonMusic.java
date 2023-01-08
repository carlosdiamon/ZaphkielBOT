package github.carlosdiamon.controller.buttons;

import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.controller.commands.MusicCommand;
import github.carlosdiamon.model.utlity.YoutubeSearch;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonMusic extends BodyButtons {


    public ButtonMusic(String actionName) {
        super(actionName);
    }

    @Override
    public void interaction(ButtonInteractionEvent event, String button) {
        MessageChannelUnion channel = ListenerMessage.getChannelUnion();
        int searchAct = YoutubeSearch.getSearchAct();

        switch (button) {
            case "ARROWBACK1":
                searchAct = 1;
                break;
            case "ARROWBACK0":
                searchAct -= 1;
                break;
            case "CHECK":
                MusicCommand.playMusic(event.getMessageId(), channel, searchAct);
                return;
            case "ARROWFORWARD0":
                searchAct += 1;
                break;
            case "MUSIC:ARROWFORWARD1":
                searchAct = YoutubeSearch.getSEARCH_LIMIT();
                break;
        }
        MusicCommand.searchEmbed(event.getMessageId(), channel, searchAct);
    }
}
