package github.carlosdiamon.model.utlity;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatDate {
    public static String formatDate(OffsetDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd, LLL, yyyy",
                new Locale("es", "ES"));
        return formatter.format(date);
    }
}
