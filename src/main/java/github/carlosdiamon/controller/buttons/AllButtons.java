package github.carlosdiamon.controller.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;

public class AllButtons {
    private final ArrayList<BodyButtons> BUTTONS;

    public AllButtons() {
        BUTTONS = new ArrayList<>();
        addCommand();
    }

    public void addCommand() {
        BUTTONS.add(new ButtonMusic("MUSIC"));
        BUTTONS.add(new ButtonInfo("INFOUSER"));
    }
    public boolean actionButton(ButtonInteractionEvent event) {
        String[] idButton = event.getButton().getId().split(":");

        for (int i = 0; i < BUTTONS.size(); i++) {
            BodyButtons bdButtons = BUTTONS.get(i);
            if (idButton[0].equals(bdButtons.getActionName())) {
                bdButtons.interaction(event, idButton[1]);
                bdButtons.setAuthor(event.getUser());
                return true;
            }
        }
        return false; // Command Not Found
    }
}
