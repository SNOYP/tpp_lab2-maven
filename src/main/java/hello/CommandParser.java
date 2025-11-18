package hello;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("(\\w+)\\s+(\\w+)\\s*\\((.+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PARAM_PATTERN = Pattern.compile("(\\w+)='([^']*)'", Pattern.CASE_INSENSITIVE);

    public static class ParsedCommand {
        public String command;
        public String table;
        public Map<String, String> params = new HashMap<>();
    }

    public static ParsedCommand parse(String input) {
        ParsedCommand cmd = new ParsedCommand();
        Matcher matcher = COMMAND_PATTERN.matcher(input.trim());

        if (matcher.find()) {
            cmd.command = matcher.group(1).toLowerCase();
            cmd.table = matcher.group(2).toLowerCase();
            String paramsStr = matcher.group(3);

            Matcher paramMatcher = PARAM_PATTERN.matcher(paramsStr);
            while (paramMatcher.find()) {
                cmd.params.put(paramMatcher.group(1), paramMatcher.group(2));
            }
        } else {
            return null;
        }
        return cmd;
    }
}