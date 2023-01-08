package github.carlosdiamon.controller.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import github.carlosdiamon.controller.AudioManager.GuildMusicManager;
import github.carlosdiamon.controller.AudioManager.PlayerManager;
import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.model.utlity.FormatText;
import github.carlosdiamon.model.utlity.YoutubeSearch;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.view.CustomEmbeds;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class MusicCommand extends BodyCommands{
    private static String title, image;
    private static List<Button> buttons;
    private static StringBuilder desc;
    public MusicCommand(){
        super("music", PropertyControl.getProperty("zaphkiel.command.music.description"));
        buttons = new ArrayList<>();
        buttons.add(Button.primary("MUSIC:ARROWBACK1", Emoji.fromUnicode("⏪")));
        buttons.add(Button.primary("MUSIC:ARROWBACK0", Emoji.fromFormatted("◀")));
        buttons.add(Button.success("MUSIC:CHECK", Emoji.fromFormatted("✅")));
        buttons.add(Button.primary("MUSIC:ARROWFORWARD0", Emoji.fromFormatted("▶")));
        buttons.add(Button.primary("MUSIC:ARROWFORWARD1", Emoji.fromFormatted("⏩")));
    }
    @Override
    public void command(SlashCommandInteractionEvent event) {
        Member bot = event.getGuildChannel().getGuild().getSelfMember();
        CustomEmbeds customEmbeds = new CustomEmbeds("music");
        // Estados del canal de voz
        GuildVoiceState memberVoice = Objects.requireNonNull(event.getMember()).getVoiceState();
        GuildVoiceState botVoice = bot.getVoiceState();

        assert memberVoice != null;
        if (!memberVoice.inAudioChannel()) { // No esta conectado al canal de voz
            event.reply(PropertyControl.getProperty("zaphkiel.channelnotfound.response")).queue();
            return;
        }

        AudioManager audioManager = event.getMember().getGuild().getAudioManager();
        VoiceChannel voiceChannel = Objects.requireNonNull(memberVoice.getChannel()).asVoiceChannel();
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        audioManager.openAudioConnection(voiceChannel); // Conectarse al Canal de voz
        audioManager.setSelfDeafened(true);

        AudioPlayer audioPlayer = musicManager.audioPlayer;
        AudioTrack track = audioPlayer.getPlayingTrack(); // lo que esta sonando
        AudioTrackInfo info;

        OptionMapping optMap = event.getOption("search"); // Si lleno search

        if (optMap == null) optMap = event.getOption("options"); // si no entonces options

        // Textos Embed
        String footer = PropertyControl.getProperty("zaphkiel.embed.order.footer");

        switch (optMap.getAsString()) {
            case "leave":
                event.reply(PropertyControl.getProperty("zaphkiel.disconectchannel.response")).queue();
                audioManager.closeAudioConnection();
                musicManager.scheduler.player.stopTrack();
                musicManager.scheduler.repeating = false;
                musicManager.scheduler.player.setPaused(false);
                musicManager.scheduler.queue.clear();
                break;
            case "nowplaying":
                if (track == null) { // La lista de MUSICA esta vacia
                    event.reply(PropertyControl.getProperty("zaphkiel.queueempety.response")).queue();
                    return;
                }
                title = track.getInfo().title;
                info = track.getInfo();

                desc = new StringBuilder(PropertyControl.getProperty("zaphkiel.embed.nowplaying.description")
                        .replace("%YTACCOUNT%", info.author)
                        .replace("%YTTIME%", FormatText.formatTime(track.getDuration()))
                        .replace("%PAUSE%", (musicManager.scheduler.player.isPaused()) ?
                                PropertyControl.getProperty("zaphkiel.yes.text") :
                                PropertyControl.getProperty("zaphkiel.no.text"))
                        .replace("%BUCLE%", (musicManager.scheduler.repeating) ?
                                PropertyControl.getProperty("zaphkiel.yes.text") :
                                PropertyControl.getProperty("zaphkiel.no.text"))
                        .replace("%PROGBAR%", FormatText.progressYTBar(track.getPosition(),
                                track.getDuration())));
               image = "https://img.youtube.com/vi/"+YoutubeSearch.getIdByUrl(info.uri)+"/0.jpg";
                event.replyEmbeds(customEmbeds.embedThumbail(title, info.uri, image,
                        desc.toString(), footer, ListenerMessage.getAuthor().getUser().getAvatarUrl()
                        , new Color(255, 0, 0))).queue();
                break;
            case "pause":
                if (track == null) {
                    event.reply(PropertyControl.getProperty("zaphkiel.queueempety.response")).queue();
                    return;
                }
                boolean newPause = !musicManager.scheduler.player.isPaused();
                desc = new StringBuilder(PropertyControl.getProperty("zaphkiel.trackpause.response")
                        .replace("%YTTITLE%", track.getInfo().title)
                        .replace("%PAUSE%", (newPause) ? PropertyControl.getProperty("zaphkiel.yes.text") :
                                PropertyControl.getProperty("zaphkiel.no.text")));

                musicManager.scheduler.player.setPaused(newPause);

                event.reply(desc.toString()).queue();
                break;
            case "bucle":
                if (track == null) {
                    event.reply(PropertyControl.getProperty("zaphkiel.queueempety.response")).queue();
                    return;
                }
                boolean newRepeating = !musicManager.scheduler.repeating;

                musicManager.scheduler.repeating = newRepeating;
                desc = new StringBuilder(PropertyControl.getProperty("zaphkiel.trackbucle.response")
                        .replace("%YTTITLE%", track.getInfo().title)
                        .replace("%BUCLE%", (newRepeating) ? PropertyControl.getProperty("zaphkiel.yes.text") :
                                PropertyControl.getProperty("zaphkiel.no.text")));

                event.reply(desc.toString()).queue();
                break;
            case "skip":
                if (audioPlayer.getPlayingTrack() == null) {
                    event.reply(PropertyControl.getProperty("zaphkiel.trackplaycurr.response")).queue();
                    return;
                }

                musicManager.scheduler.nextTrack();
                event.reply(PropertyControl.getProperty("zaphkiel.trackskip.response")).queue();
                break;
            case "queue":
                BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

                if (queue.isEmpty()) { // La lista de MUSICA esta vacia
                    event.reply(PropertyControl.getProperty("zaphkiel.queueempety.response")).queue();
                    return;
                }
                int trackCount = Math.min(queue.size(), 15);
                List<AudioTrack> trackList = new ArrayList<>(queue);

                desc = new StringBuilder(PropertyControl.getProperty("zaphkiel.embed.queue.desc0")
                        .replace("%QUEUE%", String.valueOf(trackList.size())));

                for (int i = 0; i < trackCount; i++) {
                    info = trackList.get(i).getInfo();
                    String numSt = (i + 1 < 10)?"0"+(i+1):""+(i+1);
                    desc.append(PropertyControl.getProperty("zaphkiel.embed.queue.desc1")
                            .replace("%NUMQ%", numSt).replace("%YTTITLE%", info.title)
                            .replace("%YTACCOUNT%", info.author)
                            .replace("%YTTIME%", FormatText.formatTime(trackList.get(i).getDuration())));
                }
                if (trackList.size() > trackCount) {
                    desc.append(PropertyControl.getProperty("zaphkiel.embed.queue.desc2")
                            .replace("%MISSINGMSC%", String.valueOf(trackList.size() - trackCount)));
                }
                title = PropertyControl.getProperty("zaphkiel.embed.queue.title");

                event.replyEmbeds(customEmbeds.normalEmbed(title, desc.toString(), footer,
                        ListenerMessage.getAuthor().getUser().getAvatarUrl(), new Color(0, 73, 255))).queue();
                break;
            case default:
                String link = optMap.getAsString();

                if (!isUrl(link)) { // Si no es un enlace
                    YoutubeSearch ytSearch = new YoutubeSearch(link); // Buscar
                    int searchActual = YoutubeSearch.getSearchAct();
                    ytSearch.search();
                    fillVideoInfo(1);

                    event.replyEmbeds(customEmbeds.embedImage(title, desc.toString(), image, footer,
                                    ListenerMessage.getAuthor().getUser().getAvatarUrl(), new Color(255, 0, 0)))
                            .setActionRow(buttons).queue();
                } else {
                    event.reply("Añadiendo a la lista").queue();
                    PlayerManager.getInstance().loadAndPlay(event.getChannel(), link);
                }
        }

    }
    public static void searchEmbed(String idMessage, MessageChannelUnion channel, int searchActual){
        CustomEmbeds customEmbeds = new CustomEmbeds("music");
        YoutubeSearch.setSearchAct(searchActual);
        if (searchActual < 1 || searchActual > YoutubeSearch.getSEARCH_LIMIT()) return;// Supera el Rango

        fillVideoInfo(searchActual);

        String footer = PropertyControl.getProperty("zaphkiel.embed.order.footer");

        channel.editMessageEmbedsById(idMessage, customEmbeds.embedImage(title, desc.toString(), image, footer,
                        ListenerMessage.getAuthor().getUser().getAvatarUrl(), new Color(255, 0, 0)))
                .setActionRow(buttons).queue();
    }
    public static void playMusic(String idMessage, MessageChannelUnion channel, int num){
        YoutubeSearch.setSearchAct(num);
        channel.deleteMessageById(idMessage).queue();
        int searchActual = YoutubeSearch.getSearchAct();
        String link = "https://www.youtube.com/watch?v="+YoutubeSearch.getVideos().get(searchActual - 1).getVideoId();
        PlayerManager.getInstance().loadAndPlay(channel, link);
    }

    public static void fillVideoInfo(int searchActual) {
        if (YoutubeSearch.getVideos().isEmpty()) {
            title = PropertyControl.getProperty("zaphkiel.exception.tokenyoutube403");
            desc = new StringBuilder("ERROR_403");
            image = "https://images.wondershare.com/recoverit/article/2019/12/youtube-problem-1.jpg";
        }
        desc = new StringBuilder(PropertyControl.getProperty("zaphkiel.embed.search.desc0")
                .replace("%SEARCHACTL%", String.valueOf(searchActual))
                .replace("%SEARCHSIZE%", String.valueOf(YoutubeSearch.getSEARCH_LIMIT()))
                .replace("%SEARCH%", YoutubeSearch.getSearch()) + PropertyControl.getProperty("zaphkiel.embed.search.desc1")
                .replace("%YTTITLE%", YoutubeSearch.getVideos().get(searchActual - 1).getTitle())
                .replace("%YTDESCRIPTION%", YoutubeSearch.getVideos().get(searchActual - 1).getDescription())
                .replace("%YTACCOUNT%", YoutubeSearch.getVideos().get(searchActual - 1).getChannelTitle()));
        title = PropertyControl.getProperty("zaphkiel.embed.search.title")
                .replace("%YTTITLE%", YoutubeSearch.getVideos().get(searchActual - 1).getTitle())
                .replace("%YTDESCRIPTION%", YoutubeSearch.getVideos().get(searchActual - 1).getDescription())
                .replace("%YTACCOUNT%", YoutubeSearch.getVideos().get(searchActual - 1).getChannelTitle());
        image = "https://img.youtube.com/vi/"+YoutubeSearch.getVideos().get(searchActual - 1).getVideoId()+"/0.jpg";
    }

    public boolean isUrl(String url){
        try{
            new URL(url);
            return true;
        }catch (MalformedURLException ex) {
            return false;
        }
    }

}
