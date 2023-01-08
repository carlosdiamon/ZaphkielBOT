package github.carlosdiamon.controller.commands;

import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.model.utlity.FormatText;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;


public class PingCommand extends BodyCommands{

    public PingCommand(){
        super("ping", PropertyControl.getProperty("zaphkiel.command.ping.description"));
    }
    @Override
    public void command(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        long currentTime = Instant.now().toEpochMilli();
        long timeUser =event.getMessageChannel().getTimeCreated().getLong(ChronoField.MILLI_OF_SECOND);
        String ping = String.format("%03d", (currentTime - timeUser));
        ping = ping.substring(0, 3);

        FormatText formatText = new FormatText("ping");

        String msg = PropertyControl.getProperty("zaphkiel.ping.response")
                .replace("%PING%", ping+" ms")
                .replace("%WEBSOCKET%", event.getJDA().getGatewayPing()+" ms");

        formatText.formatString(msg);
        event.getHook().editOriginal(msg).queue();
    }

}
