package github.carlosdiamon.controller.commands;

import github.carlosdiamon.model.persistence.PropertyControl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class HelpCommand extends BodyCommands{

    private JDA api;
    public HelpCommand(){
        super("help", PropertyControl.getProperty("zaphkiel.command.help.description"));
    }

    @Override
    public void command(SlashCommandInteractionEvent event) {
        api = event.getJDA();


        event.reply("De momento este comando esta desactivado, volvera cuando se tenga el diseño").queue();

    }


    public User getUserId(String idStr){
        User user;
        try {
            user = api.getUserById(idStr);
        }catch (Exception e){
            return null;
        }
        return user;
    }
}
