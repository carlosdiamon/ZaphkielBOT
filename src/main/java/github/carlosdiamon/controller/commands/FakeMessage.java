package github.carlosdiamon.controller.commands;

import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.view.CustomEmbeds;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class FakeMessage extends BodyCommands{
    private final String API = "https://oneki.up.railway.app/api/fake/discord/message?avatar=%s&message=%s&username=%s";
    public FakeMessage() {
        super("fake_message", PropertyControl.getProperty("zaphkiel.command.fakemessage.description"));
    }

    @Override
    public void command(SlashCommandInteractionEvent event){
        event.deferReply().queue();
        CustomEmbeds customEmbeds = new CustomEmbeds("fakeMessage");
        OptionMapping optUser = event.getOption("user");
        OptionMapping optMess = event.getOption("message");

        if (optUser == null || optMess == null) return;

        User user = optUser.getAsUser();
        String message = optMess.getAsString();

        String urlAPI = String.format(API, user.getEffectiveAvatarUrl(), message, user.getName());

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--no-sandbox");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(new Dimension(500, 500));
        driver.get(urlAPI);

        BufferedImage image = null;
        try {
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            image = ImageIO.read(screenshot);
            image = image.getSubimage(0, 0, 500, 100);
            ImageIO.write(image, "PNG", new File("src/main/resources/fakeMessage.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        driver.quit();

        String title = "Mensaje";
        Color color = new Color(255, 113, 0);
        event.getHook().sendMessageEmbeds(customEmbeds.embedBanner(title, "attachment://fakeMessage.png", color))
                .addFiles(FileUpload.fromData(new File("src/main/resources/fakeMessage.png"), "fakeMessage.png")).queue();
    }
}
