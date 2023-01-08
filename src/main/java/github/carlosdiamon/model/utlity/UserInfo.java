package github.carlosdiamon.model.utlity;

import github.carlosdiamon.model.persistence.Persistence;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;

public class UserInfo {

    private static final String DISCORD_URL = "https://discord.com/api/v10/users/";
    private HashMap<String, String> allFlags = new HashMap<>();
    private String userID, banner, bannerHex;
    private int userFlags;
    public UserInfo(String userID) {
        this.userID = userID;
        allFlags.put("HypeSquad Bravery", "<:HYPESQUAD_ONLINE_HOUSE_1:1058138388563238982>");
        allFlags.put("HypeSquad Brilliance", "<:HYPESQUAD_ONLINE_HOUSE_2:1058138386956832848>");
        allFlags.put("HypeSquad Balance", "<:HYPESQUAD_ONLINE_HOUSE_3:1058138394942767185>");

        allFlags.put("Active Developer", "<:ACTIVE_DEVELOPER:1058147619232288778>");
        allFlags.put("Bug Hunter Level 1", "<:BUG_HUNTER_LEVEL_1:1058138397983653918>");
        allFlags.put("Partnered Server Owner", "<:PARTNER:1058138396071043182>");
        allFlags.put("Early Supporter", "<:PREMIUM_EARLY_SUPPORTER:1058138392656883774>");
        allFlags.put("Team User", "<:TEAM_PSEUDO_USER:1058138393739010099>");
        allFlags.put("Verified Bot", "<:VERIFIED_BOT:1060746805471617075>");

    }

    public boolean info() {

        try {
            URL url = new URL(DISCORD_URL + userID);
            String strJson = Persistence.readDiscordAPI(url);
            JSONObject json = new JSONObject(strJson);
            banner = json.optString("banner");
            bannerHex = json.optString("banner_color");
            userFlags = json.getInt("public_flags");
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    // Obtener Badges

    public String flags() {
        StringBuilder str = new StringBuilder();
        EnumSet<User.UserFlag> flags = User.UserFlag.getFlags(userFlags);
        for (User.UserFlag flag : flags) {
            str.append(allFlags.get(flag.getName()));
        }

        return str.toString();
    }

    public String permission(Member member) {
        StringBuilder str = new StringBuilder();
        EnumSet<Permission> per = member.getPermissions();
        for (Permission permission : per) {
            str.append("**").append(permission.getName()).append("** ");
        }

        return str.toString();
    }

    public String getBanner() {
        return banner;
    }

    public String getBannerHex() {
        return bannerHex;
    }

    public String getUserID() {return userID;}

}
