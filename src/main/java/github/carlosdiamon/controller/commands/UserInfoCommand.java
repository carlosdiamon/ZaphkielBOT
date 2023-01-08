package github.carlosdiamon.controller.commands;

import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.model.utlity.FormatDate;
import github.carlosdiamon.model.utlity.UserInfo;
import github.carlosdiamon.view.CustomEmbeds;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserInfoCommand extends  BodyCommands {
    private static List<Button> buttons;
    private static CustomEmbeds customEmbeds;
    private static User user;
    private static Member member;
    private static UserInfo userInf;
    private static Color color;
    public UserInfoCommand() {
        super("info", PropertyControl.getProperty("zaphkiel.command.userinfo.description"));
        customEmbeds = new CustomEmbeds("info");
        buttons = new ArrayList<>();
        buttons.add(Button.primary("INFOUSER:BANNER", Emoji.fromUnicode("🎨")));
        buttons.add(Button.primary("INFOUSER:USER", Emoji.fromFormatted("📋")));
        buttons.add(Button.primary("INFOUSER:SERVER", Emoji.fromFormatted("🧾")));
        color = new Color(235, 0, 243);
    }
    @Override
    public void command(SlashCommandInteractionEvent event) {

        OptionMapping optionTag = event.getOption("user");
        member = event.getMember();
        user = event.getUser();

        if (optionTag != null) {
            member = optionTag.getAsMember();
            user = optionTag.getAsUser();
        }

        userInf = new UserInfo(user.getId());

        if (!userInf.info()) {
            event.reply(PropertyControl.getProperty("zaphkiel.exception.error")).queue();
            return;
        }
        String colorHex = userInf.getBannerHex();
        if (!colorHex.isEmpty()) {
            color = Color.decode(colorHex);
        } else {
            color = new Color(182, 4, 4);
        }


        event.replyEmbeds(basicInfoUser()).addActionRow(buttons).queue();

    }

    private static MessageEmbed basicInfoUser() {

        String userID = user.getId();
        String avatarUser = user.getEffectiveAvatarUrl();
        String title = PropertyControl.getProperty("zaphkiel.embed.infouser.title")
                .replace("%USER%", user.getName());
        String footer = PropertyControl.getProperty("zaphkiel.embed.order.footer");


        String flags = userInf.flags();
        String colorBanner = userInf.getBannerHex();

        List<String> fields = new ArrayList<>();

        String[] userArray = {userID, user.getDiscriminator(), flags, FormatDate.formatDate(user.getTimeCreated())
        , colorBanner};

        for (int i = 0; i < userArray.length; i++) {
            if (!userArray[i].isEmpty()) {
                fields.add(PropertyControl.getProperty("zaphkiel.embed.infouser.field.title"+i));
                fields.add(userArray[i]);
            }

        }

        return customEmbeds.embedInfoUser(title, fields, avatarUser, footer,
                ListenerMessage.getAuthor().getUser().getAvatarUrl(), color);
    }

    public static void guiOptions(String idMessage, MessageChannelUnion channel, int option){
        MessageEmbed messageEmbed;
        switch (option) {
            case 1:
                messageEmbed = bannerUser();
                channel.deleteMessageById(idMessage).queue();
                if (userInf.getBanner().isEmpty()) {
                    channel.sendMessageEmbeds(bannerUser()).addFiles(FileUpload.fromData(
                            new File("src/main/resources/DEFAULTBANNER.png"), "DEFAULTBANNER.png"))
                            .addActionRow(buttons).queue();
                } else {
                    channel.sendMessageEmbeds(bannerUser()).addActionRow(buttons).queue();
                }
                break;
            case 2:
                messageEmbed = basicInfoUser();
                channel.deleteMessageById(idMessage).queue();
                channel.sendMessageEmbeds(messageEmbed).addActionRow(buttons).queue();
                break;
            case 3:
                messageEmbed = infoMember();
                channel.deleteMessageById(idMessage).queue();
                if (member == null) {
                    channel.sendMessageEmbeds(infoMember()).addFiles(FileUpload.fromData(
                                    new File("src/main/resources/ERROR404.png"), "ERROR404.png"))
                            .addActionRow(buttons).queue();
                } else {
                    channel.sendMessageEmbeds(infoMember()).addActionRow(buttons).queue();
                }
                break;
            default:
                messageEmbed = null;
                break;
        }

    }

    private static MessageEmbed infoMember() {
        String title = PropertyControl.getProperty("zaphkiel.embed.infomember.title")
                .replace("%USER%", user.getName());

        if (member != null) {
            List<String> fields = new ArrayList<>();
            StringBuilder roles = new StringBuilder();

            for (Role rol: member.getRoles()) {
                roles.append("<@&").append(rol.getId()).append(">");
            }

            String[] memberArray = {roles.toString(), userInf.permission(member),
                    (member.getTimeBoosted() != null)?FormatDate.formatDate(member.getTimeBoosted()):
                    PropertyControl.getProperty("zaphkiel.embed.infomember.response.boost"),
                    FormatDate.formatDate(member.getTimeJoined())};

            for (int i = 0; i < memberArray.length; i++) {
                fields.add(PropertyControl.getProperty("zaphkiel.embed.infomember.field.title"+i)
                        .replace("%NAMEGUILD%", member.getGuild().getName()));
                fields.add(memberArray[i]);
            }
            String footer = PropertyControl.getProperty("zaphkiel.embed.order.footer");
            String avatarMember = member.getEffectiveAvatarUrl();
            return customEmbeds.embedInfoUser(title, fields, avatarMember, footer,
                    ListenerMessage.getAuthor().getUser().getAvatarUrl(), color);
        } else {
            return customEmbeds.embedBanner(title, "attachment://ERROR404.png", color);
        }
    }

    private static MessageEmbed bannerUser() {
        String idBanner = userInf.getBanner();
        String title = PropertyControl.getProperty("zaphkiel.embed.infouser.titlebanner")
                .replace("%MEMBER%", user.getName());
        // 1440x508
        if (idBanner.isEmpty()) {
            return customEmbeds.embedBanner(title, "attachment://DEFAULTBANNER.png", color);
        }
        String format = "png";
        if (userInf.getBanner().startsWith("a_") ) {
            format = "gif";
        }


        String banner = String.format(User.BANNER_URL, userInf.getUserID(), userInf.getBanner()
                , format+"?size=1024");

        return  customEmbeds.embedBanner(title, banner, color);
    }

}
