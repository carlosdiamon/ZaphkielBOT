package github.carlosdiamon.controller.commands;

import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.view.CustomEmbeds;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;

public class AvatarCommand extends BodyCommands{

    public AvatarCommand(){
        super("avatar", PropertyControl.getProperty("zaphkiel.command.avatar.description"));

    }

    @Override
    public void command(SlashCommandInteractionEvent event) {
        String name = "";
        String avatarURL = "";
        Member member;
        User user;

        OptionMapping optionTag = event.getOption("user");

        if (optionTag != null) {
            member = optionTag.getAsMember();
            user = optionTag.getAsUser();
        } else {
            member = event.getMember();
            user = event.getUser();
        }

        name = user.getName();
        // Si el usuario se encuentra o no en el servidor
        avatarURL = (member != null)?member.getEffectiveAvatarUrl():user.getAvatarUrl();

        CustomEmbeds embed = new CustomEmbeds("avatar");

        String footer = PropertyControl.getProperty("zaphkiel.embed.order.footer");
        event.replyEmbeds(embed.embedImage(name, "", avatarURL+"?size=2048"
                , footer, ListenerMessage.getAuthor().getUser().getAvatarUrl()
                , new Color(255, 113, 0))).queue();

    }

}
