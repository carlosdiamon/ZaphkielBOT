package github.carlosdiamon.controller.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class BodyCommands {
    private final String name, description;
    private final Permission permission;

    public BodyCommands(String name, String description, Permission permission){
        this.name = name;
        this.description = description;
        this.permission = permission;

    }
    public BodyCommands(String name, String description){
        this.name = name;
        this.description = description;
        this.permission = null;
    }

    public abstract void command(SlashCommandInteractionEvent event);
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Permission getPermission() {return permission;}
}
