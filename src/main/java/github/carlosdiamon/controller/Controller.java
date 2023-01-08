package github.carlosdiamon.controller;

import github.carlosdiamon.model.persistence.PropertyControl;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Controller{

    private final DefaultShardManagerBuilder builder;

    public Controller() {

        PropertyControl prop = new PropertyControl();

        String BOT_TOKEN = PropertyControl.getEnv("TOKEN_BOT");
        builder = DefaultShardManagerBuilder
                .createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES
                , GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL);
        setDefaultBuilder();
        ShardManager shardMg = builder.build();
        shardMg.addEventListener(new ListenerMessage());

    }

    public void setDefaultBuilder() {
        builder.setActivity(Activity.watching("Programado por: CarlosDiamon"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB);
    }
}
