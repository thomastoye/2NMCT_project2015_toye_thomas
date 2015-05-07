package be.thomastoye.findafrietkot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteImageFactory {
    public static RemoteImage createImage(String url) {
        Matcher m = Pattern.compile("([0-9.]*)(?=\\.jpg)")
                .matcher(url);
        if(m.find()){
            try {
                int parsed = Integer.parseInt(m.group());
                return new RemoteImage(parsed);
            } catch (NumberFormatException e) { return null; }
        } else
        {
            return null;
        }
    }
}
