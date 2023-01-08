package github.carlosdiamon.model.persistence;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Properties;

public class PropertyControl {

    private static Properties prop;
    private static Dotenv env;

    public PropertyControl() {
        Persistence pers = new Persistence(); // Modifiar luego
        PropertyControl.env = Dotenv.configure().load();
        PropertyControl.prop = pers.getDefaultProperties("es_CO.lang");
    }
    public static String getEnv(String envProp){ return PropertyControl.env.get(envProp);}
    public static String getProperty(String property) {
        return prop.getProperty(property);
    }
}
