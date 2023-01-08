package github.carlosdiamon.controller.commands;

import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.view.CustomEmbeds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class ClearCommand extends BodyCommands {
    private int countMsg;
    public ClearCommand() {
        super("clear", PropertyControl.getProperty("zaphkiel.command.clearmessages.description")
                , Permission.MESSAGE_MANAGE);
    }

    @Override
    public void command(SlashCommandInteractionEvent event) {
        OptionMapping num = event.getOption("count");
        CustomEmbeds embed = new CustomEmbeds("clear");
        if (num == null) return;
        int realCount;
        try {
            realCount = num.getAsInt();
        } catch (ArithmeticException ex) { // se paso del limite INT: 2,147,483,647
            realCount = 0;
        }
        String footer = PropertyControl.getProperty("zaphkiel.embed.order.footer");
        TextChannel channel = event.getChannel().asTextChannel();

        int count = 0;
        countMsg = 0;
        if (realCount == 1) {
            channel.deleteMessageById(channel.getLatestMessageId()).queue();
        } else {
            while (count < realCount) {
                count = Math.min(100, realCount);
                List<Message> messageList = event.getChannel().getHistory().retrievePast(count).complete();
                if (messageList.isEmpty()) break;

                if (!deleteMsg(messageList, channel)) { // Los mensajes superan los 14 dias
                    channel.sendMessage(PropertyControl.getProperty("zaphkiel.2weeksmessages.response")).queue();
                    break;
                }
                realCount -= count;
            }

        }

        event.reply(PropertyControl.getProperty("zaphkiel.clearmessages.response")
                .replace("%COUNTCLEAR%", String.valueOf(countMsg))).queue();
    }

    private boolean deleteMsg(List<Message> messages, TextChannel channel){
        List<Message> messagesToKeep = messages.stream()
                .filter(m -> {
                    Instant messageInstant = m.getTimeCreated().toInstant();
                    Instant currentInstant = Instant.now();
                    Duration duration = Duration.between(messageInstant, currentInstant);
                    return duration.toDays() < 14; // Mensajes que no superan los 14 Dias
                })
                .toList();

        if (messagesToKeep.isEmpty()) return false; // Superaban los 14 dias
        countMsg += messagesToKeep.size();
        channel.deleteMessages(messagesToKeep).queue();
        return true;
    }
}
