/**
 * @author liguang
 * @date 2022/9/20 星期二 3:00 下午
 */
public class ConfigBanner {


    private static final String ENCODE_JOIN  = ";";

    private static final String ENCODE_START = "\033[";

    private static final String ENCODE_END   = "m";

    private static final String RESET        = "0;" + ConsoleColor.DEFAULT;

    public static String encode(ConsoleColor element) {
        return ENCODE_START + element + ENCODE_END;
    }

    public static String toColorString(Object... elements) {
        StringBuilder sb = new StringBuilder();
        build(sb, elements);
        return sb.toString();
    }

    private static void build(StringBuilder sb, Object[] elements) {
        boolean writingAnsi = false;
        boolean containsEncoding = false;
        for (Object element : elements) {
            if (element instanceof ConsoleColor) {
                containsEncoding = true;
                if (!writingAnsi) {
                    sb.append(ENCODE_START);
                    writingAnsi = true;
                } else {
                    sb.append(ENCODE_JOIN);
                }
            } else {
                if (writingAnsi) {
                    sb.append(ENCODE_END);
                    writingAnsi = false;
                }
            }
            sb.append(element);
        }
        if (containsEncoding) {
            sb.append(writingAnsi ? ENCODE_JOIN : ENCODE_START);
            sb.append(RESET);
            sb.append(ENCODE_END);
        }
    }

    static enum ConsoleColor {

        DEFAULT("39"),

        BLACK("30"),

        RED("31"),

        GREEN("32"),

        YELLOW("33"),

        BLUE("34"),

        MAGENTA("35"),

        CYAN("36"),

        WHITE("37"),

        BRIGHT_BLACK("90"),

        BRIGHT_RED("91"),

        BRIGHT_GREEN("92"),

        BRIGHT_YELLOW("93"),

        BRIGHT_BLUE("94"),

        BRIGHT_MAGENTA("95"),

        BRIGHT_CYAN("96"),

        BRIGHT_WHITE("97"),

        NORMAL("0"),

        BOLD("1"),

        FAINT("2"),

        ITALIC("3"),

        UNDERLINE("4");

        private final String code;

        ConsoleColor(String code){
            this.code = code;
        }

        @Override
        public String toString() {
            return this.code;
        }
    }

    // banner生成：https://www.toolscat.com/img/text-ascii?p=display&f=Catwalk&t=toolscat.com
    private static final String configBanner = "\n"
        + " __  _____   _   ___    ___   _   ___    ___   _   ___ \n"
        + " \\ \\/ /_ _| /_\\ / _ \\  | _ ) /_\\ |_ _|  / __| /_\\ |_ _|\n"
        + "  >  < | | / _ \\ (_) | | _ \\/ _ \\ | |  | (__ / _ \\ | | \n"
        + " /_/\\_\\___/_/ \\_\\___/  |___/_/ \\_\\___|  \\___/_/ \\_\\___|\n"
        + "                                                       \n";


    public static void printBanner() {
        String envInfo = getConfigRuntimeInfo();
        String bannerStr = toColorString(ConsoleColor.BRIGHT_MAGENTA, configBanner, ConsoleColor.DEFAULT, envInfo);
        System.out.println(bannerStr);
    }



    public static String getConfigRuntimeInfo() {
        String envInfo = String.format("\r\n*********************************************************  \nENV_CODE     : %s \nVersion      : %s \nConfigStore  : %s \nConfigServer : %s \nSSL          : %s   \n*********************************************************",
            "dev", "1.0.0", "name1", "127.0.0.1", "xxxx");
        return envInfo;
    }

}
