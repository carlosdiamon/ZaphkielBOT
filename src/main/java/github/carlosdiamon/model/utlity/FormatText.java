package github.carlosdiamon.model.utlity;

import github.carlosdiamon.controller.ListenerMessage;

import java.util.concurrent.TimeUnit;

public class FormatText {

    private final String COMMAND;
    public FormatText(String COMMAND) {
        this.COMMAND = COMMAND;
    }

    public String formatString(String text){
        text = text.replace("%AUTHOR%", ListenerMessage.getAuthor().getUser().getName())
                .replace("%TEXTCHANNEL%", ListenerMessage.getChannelUnion().getName())
                .replace("%NAMEGUILD%", ListenerMessage.getGuild().getName())
                .replace("%COMMAND%", COMMAND);

        return text;
    }
    public static String formatTime(long timeInMillis) {
        long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        long minutes = (timeInMillis - TimeUnit.HOURS.toMillis(hours)) / TimeUnit.MINUTES.toMillis(1);
        long seconds = (timeInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
                / TimeUnit.SECONDS.toMillis(1);
        if (hours < 1) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public static String progressYTBar(long currenTime, long videoTime){

        long currentVideoSeg = currenTime / 1000;
        long videoSeg = videoTime / 1000;
        int sizeBar = 30;
        long filled = sizeBar * currentVideoSeg / videoSeg;
        return "`[" +"▇".repeat(Math.toIntExact(filled))+"-".repeat(Math.toIntExact(sizeBar - filled))+"]`**" +
                formatTime(currenTime) + " / " + formatTime(videoTime) + "**";

    }

}
