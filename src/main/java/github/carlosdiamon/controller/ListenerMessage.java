package github.carlosdiamon.controller;

import github.carlosdiamon.controller.buttons.AllButtons;
import github.carlosdiamon.controller.commands.AllCommands;
import github.carlosdiamon.model.UserDAO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ListenerMessage extends ListenerAdapter {
    private final AllCommands ALL_COMMANDS;
    private final AllButtons ALL_BUTTONS;
    private static UserDAO author;
    private static Guild guild;
    private static MessageChannelUnion channelUnion;
    public ListenerMessage() {
        ALL_COMMANDS = new AllCommands();
        ALL_BUTTONS = new AllButtons();
    }
    @Override
    public void onReady(ReadyEvent event) {
        System.out.printf("%#s is Ready\n\n", event.getJDA().getSelfUser());
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        author = new UserDAO(event.getUser(), event.getMember());
        guild = event.getGuild();
        channelUnion = event.getChannel();
        ALL_COMMANDS.actionCommand(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        channelUnion = event.getChannel();
        if (event.getButton().getId() == null) return;
        event.deferEdit().queue();
        ALL_BUTTONS.actionButton(event);
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        ALL_COMMANDS.loadSlashCommands(event.getGuild());

    }

    public static UserDAO getAuthor() {return author;}

    public static Guild getGuild() {return guild;}
    public static MessageChannelUnion getChannelUnion() {return channelUnion;}

    public AllCommands getALL_COMMANDS() {
        return ALL_COMMANDS;
    }


}
