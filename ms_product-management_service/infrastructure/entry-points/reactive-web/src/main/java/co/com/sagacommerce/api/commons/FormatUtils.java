package co.com.sagacommerce.api.commons;

import java.util.regex.Pattern;

public class FormatUtils {

    public static final Pattern NUMBER_REGEX = Pattern.compile("^\\d+$") ;

    public static boolean isNumber(String value) {
        return !value.matches(NUMBER_REGEX.pattern());
    }
}
