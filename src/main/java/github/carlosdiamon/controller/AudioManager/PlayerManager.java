package github.carlosdiamon.controller.AudioManager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import github.carlosdiamon.controller.ListenerMessage;
import github.carlosdiamon.model.persistence.PropertyControl;
import github.carlosdiamon.model.utlity.FormatText;
import github.carlosdiamon.view.CustomEmbeds;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;


import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        musicManagers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(MessageChannelUnion channel, String trackUrl) {
        GuildMusicManager musicManager = this.getMusicManager(channel.asGuildMessageChannel().getGuild());
        CustomEmbeds message = new CustomEmbeds("music");
        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
                String description = PropertyControl.getProperty("zaphkiel.addingqueue0.response");
                description = description.replace("%YTACCOUNT%", track.getInfo().author);
                description = description.replace("%YTTIME%", FormatText.formatTime(track.getDuration()));
                String footer = PropertyControl.getProperty("zaphkiel.embed.addqueue.footer");
                String image = "https://img.youtube.com/vi/"+track.getInfo().identifier+"/0.jpg";
                        channel.sendMessageEmbeds(message.embedThumbail(track.getInfo().title, image, description, footer,
                                ListenerMessage.getAuthor().getUser().getAvatarUrl(),
                                new Color(255, 0, 0))).queue();

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();
                String description = PropertyControl.getProperty("zaphkiel.addingqueue1.response");
                description = description.replace("%YTACCOUNT%", playlist.getTracks().get(0).getInfo().author);
                long timeYT = 0;
                for (AudioTrack track : tracks) {
                    timeYT += track.getDuration();
                    musicManager.scheduler.queue(track);
                }
                description = description.replace("%YTSIZE%", String.valueOf(tracks.size()));
                description = description.replace("%YTTIME%", FormatText.formatTime(timeYT));
                String footer = PropertyControl.getProperty("zaphkiel.embed.addqueue.footer");
                String image = "https://img.youtube.com/vi/"+playlist.getTracks().get(0).getInfo().identifier+"/0.jpg";

                channel.sendMessageEmbeds(message.embedThumbail(playlist.getName(), image, description, footer,
                        ListenerMessage.getAuthor().getUser().getAvatarUrl(),
                        new Color(255, 0, 0))).queue();

            }

            @Override
            public void noMatches() {
                // Cuando la lista este vacia
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage(PropertyControl.getProperty("zaphkiel.trackerror.response")).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

}