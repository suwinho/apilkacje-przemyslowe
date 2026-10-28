package pl.edu.ug.cmms;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DurationFormat {

    private static final Pattern PATTERN = Pattern.compile("(\\d+) h (\\d+) min");

    private DurationFormat() {
    }

    static String format(Duration duration) {
        return duration.toHours() + " h " + duration.toMinutesPart() + " min";
    }

    static Duration parse(String text) {
        Matcher m = PATTERN.matcher(text.trim());
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid duration: " + text);
        }
        return Duration.ofHours(Long.parseLong(m.group(1))).plusMinutes(Long.parseLong(m.group(2)));
    }
}
