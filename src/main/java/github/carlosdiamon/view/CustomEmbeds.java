package github.carlosdiamon.view;


import github.carlosdiamon.model.utlity.FormatText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class CustomEmbeds {

    private EmbedBuilder embed;
    private final FormatText FORMAT_TEXT;

    public CustomEmbeds(String command) {
        embed = new EmbedBuilder();
        FORMAT_TEXT = new FormatText(command);
    }

    public MessageEmbed embedComplete(String title, String urlTitle, String urlThumbnail,
                                     String description, String image, String footer,
                                     String iconFooter, Color color) {
        clearEmbed();
        embed.setTitle(FORMAT_TEXT.formatString(title), urlTitle);
        embed.setThumbnail(urlThumbnail);
        embed.setDescription(FORMAT_TEXT.formatString(description));
        embed.setImage(image);
        embed.setFooter(FORMAT_TEXT.formatString(footer), iconFooter);
        embed.setColor(color);
        return embed.build();
    }
    public MessageEmbed embedThumbail(String title, String urlTitle, String urlThumbnail,
                                     String description, String footer,
                                     String iconFooter, Color color) {
        clearEmbed();
        embed.setTitle(FORMAT_TEXT.formatString(title), urlTitle);
        embed.setThumbnail(urlThumbnail);
        embed.setDescription(FORMAT_TEXT.formatString(description));
        embed.setFooter(FORMAT_TEXT.formatString(footer), iconFooter);
        embed.setColor(color);
        return embed.build();
    }
    public MessageEmbed embedThumbail(String title, String urlThumbnail,
                                      String description, String footer,
                                      String iconFooter, Color color) {
        clearEmbed();
        embed.setTitle(FORMAT_TEXT.formatString(title));
        embed.setThumbnail(urlThumbnail);
        embed.setDescription(FORMAT_TEXT.formatString(description));
        embed.setFooter(FORMAT_TEXT.formatString(footer), iconFooter);
        embed.setColor(color);
        return embed.build();
    }
    public MessageEmbed embedImage(String title, String description, String image,
                                     String footer, String iconFooter, Color color) {
        clearEmbed();
        embed.setTitle(FORMAT_TEXT.formatString(title));
        embed.setDescription(FORMAT_TEXT.formatString(description));
        embed.setImage(image);
        embed.setFooter(FORMAT_TEXT.formatString(footer), iconFooter);
        embed.setColor(color);
        return embed.build();
    }
    public MessageEmbed normalEmbed(String title, String description, String footer,
                                    String iconFooter, Color color) {
        clearEmbed();
        embed.setTitle(FORMAT_TEXT.formatString(title));
        embed.setDescription(FORMAT_TEXT.formatString(description));
        embed.setFooter(FORMAT_TEXT.formatString(footer), iconFooter);
        embed.setColor(color);
        return embed.build();
    }
   // Embed para InfoUser
    public MessageEmbed embedBanner(String title, String urlBanner, Color color) {
        clearEmbed();
        embed.setTitle(title);
        embed.setImage(urlBanner);
        embed.setColor(color);
        return embed.build();

    }
   public MessageEmbed embedInfoUser(String title, List<String> fields, String urlThumbnail,
                                     String footer, String iconFooter, Color color) {
        clearEmbed();
       embed.setTitle(FORMAT_TEXT.formatString(title))
               .setThumbnail(urlThumbnail)
               .setFooter(FORMAT_TEXT.formatString(footer), iconFooter)
               .setColor(color);

       if (fields.size() % 2 != 0) embed.build();

       for (int i = 0; i < fields.size() - 1; i += 2) {
            embed.addField(fields.get(i), fields.get(i + 1), false);
       }

       return embed.build();
   }

   public void clearEmbed() {
        embed.clear();
        embed.clearFields();
   }

   public MessageEmbed getEmbed() {return embed.build();}

}
