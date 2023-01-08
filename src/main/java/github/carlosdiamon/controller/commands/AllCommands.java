package github.carlosdiamon.controller.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AllCommands {
    private final ArrayList<BodyCommands> COMMANDS;

    public AllCommands() {
        COMMANDS = new ArrayList<>();
        addCommand();
    }

    public void addCommand() {
        COMMANDS.add(new HelpCommand());
        COMMANDS.add(new AvatarCommand());
        COMMANDS.add(new MusicCommand());
        COMMANDS.add(new PingCommand());
        COMMANDS.add(new UserInfoCommand());
        COMMANDS.add(new FakeMessage());
        COMMANDS.add(new ClearCommand());
    }

    public boolean actionCommand(SlashCommandInteractionEvent event) {
        String command = event.getName();
        for (BodyCommands bdCmd : COMMANDS) {
            if (command.equals(bdCmd.getName()) && obtainPermissions(event.getMember(), bdCmd.getPermission())) {
                bdCmd.command(event);
                return true;
            }
        }
        return false;
    }

    public boolean obtainPermissions(Member member, Permission permission) {

        if (member == null) return false;
        if (permission == null) return true; // El comando no necesita permisos

        EnumSet<Permission> permissions = member.getPermissions();
        for (Permission perm : permissions) {
            if (perm.getName().equals(permission.getName())) {
                return true;
            }
        }
        return false; // No permiso
    }

    public void loadSlashCommands(Guild guild) {
        OptionData opt1, opt2;
        SubcommandData subComd1, subComd2;
        List<CommandData> commandData = new ArrayList<>();
        for (BodyCommands bdCmd : COMMANDS) {
            switch (bdCmd.getName()) {
                case "avatar" -> {
                    opt1 = new OptionData(OptionType.USER, "user", "User avatar", false);
                    commandData.add(Commands.slash(bdCmd.getName(), bdCmd.getDescription()).addOptions(opt1));
                }
                case "music" -> {
                    subComd1 = new SubcommandData("search", "Search Music");
                    subComd2 = new SubcommandData("options", "Options Music!!!");
                    opt1 = new OptionData(OptionType.STRING, "search", "¡Music!", true);
                    opt2 = new OptionData(OptionType.STRING, "options", "Options Music", true)
                            .addChoice("Queue", "queue")
                            .addChoice("NowPlaying", "nowplaying")
                            .addChoice("Bucle", "bucle")
                            .addChoice("Pause", "pause")
                            .addChoice("Skip", "skip")
                            .addChoice("Stop", "leave");
                    subComd1.addOptions(opt1);
                    subComd2.addOptions(opt2);
                    commandData.add(Commands.slash(bdCmd.getName(), bdCmd.getDescription())
                            .addSubcommands(subComd1, subComd2));
                }
                case "info" -> {
                    opt1 = new OptionData(OptionType.USER, "user", "info User", false);
                    commandData.add(Commands.slash(bdCmd.getName(), bdCmd.getDescription()).addOptions(opt1));
                }
                case "fake_message" -> {
                    opt1 = new OptionData(OptionType.USER, "user", "user", true);
                    opt2 = new OptionData(OptionType.STRING, "message", "fake message", true);
                    commandData.add(Commands.slash(bdCmd.getName(), bdCmd.getDescription()).addOptions(opt1, opt2));
                }
                case "clear" -> {
                    opt1 = new OptionData(OptionType.INTEGER, "count", "count messages", true);
                    commandData.add(Commands.slash(bdCmd.getName(), bdCmd.getDescription())
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
                            .addOptions(opt1));

                }
                default -> commandData.add(Commands.slash(bdCmd.getName(), bdCmd.getDescription()));
            }

        }
        guild.updateCommands().addCommands(commandData).queue();
    }

}
