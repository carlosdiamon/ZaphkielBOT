package github.carlosdiamon.model.persistence;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Persistence {

    private final String PATH = "src/main/java/github/carlosdiamon/model/persistence/";
    public Persistence(){ // QUITAR LUEGO

    }

    public Properties getDefaultProperties(String archive){
        Properties prop =  new Properties();
        try (InputStream input = new FileInputStream(PATH + archive)) {
            prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return prop;
    }
    public static String readFile(InputStream file) {
        StringBuilder info = new StringBuilder();
        try {
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(file,
                    StandardCharsets.UTF_8));

            String str = "";

            while ((str = bfReader.readLine()) != null) {
                info.append(str).append("\n");
            }

            bfReader.close();

        } catch (IOException e) {
            return null;
        }
        return info.toString();
    }

    public static String readURL(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() == 200) {
            return readFile(connection.getInputStream());
        } else {
            return "ERROR_403";
        }
    }
    public static String readDiscordAPI(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bot " + PropertyControl.getEnv("TOKEN_BOT"));
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() == 200) {
            return readFile(connection.getInputStream());
        } else {
            throw new RuntimeException("EXCEPTION CODE: " + connection.getResponseCode());
        }
    }

}
