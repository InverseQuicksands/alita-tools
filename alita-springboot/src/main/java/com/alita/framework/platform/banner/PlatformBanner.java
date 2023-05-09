package com.alita.framework.platform.banner;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * PlatformBanner
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-24 22:04:00
 */
public class PlatformBanner implements Banner {
    private static final String[] BANNER = {
            "                 \n" +
                    "#############################################################################\n" +
                    "		 █████╗ ███████╗ ██████╗ ██╗     ██╗   ██╗███████╗ \n" +
                    "        ██╔══██╗██╔════╝██╔═══██╗██║     ██║   ██║██╔════╝ \n" +
                    "        ███████║█████╗  ██║   ██║██║     ██║   ██║███████╗ \n" +
                    "        ██╔══██║██╔══╝  ██║   ██║██║     ██║   ██║╚════██║ \n" +
                    "        ██║  ██║███████╗╚██████╔╝███████╗╚██████╔╝███████║ \n" +
                    "        ╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝ \n" +
                    "#############################################################################\n",
            "                        桃花庵歌\n" +
                    "\t桃花坞里桃花庵，桃花庵下桃花仙；桃花仙人种桃树，又摘桃花卖酒钱。\n" +
                    "\t酒醒只在花前坐，酒醉还来花下眠；半醒半醉日复日，花落花开年复年。\n" +
                    "\t但愿老死花酒间，不愿鞠躬车马前；车尘马足富者趣，酒盏花枝贫者缘。\n" +
                    "\t若将富贵比贫贱，一在平地一在天；若将贫贱比车马，他得驱驰我得闲。\n" +
                    "\t别人笑我太疯癫，我笑他人看不穿；不见五陵豪杰墓，无花无酒锄作田。\n",
    };

    private static final String SPRING_BOOT = " :: Spring Boot ::";

    private static final int STRAP_LINE_SIZE = 42;


    /**
     * Print the banner to the specified print stream.
     *
     * @param environment the spring environment
     * @param sourceClass the source class for the application
     * @param printStream the output print stream
     */
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {

        for (String line : BANNER) {
            printStream.println(line);
        }
        String springbootVersion = SpringBootVersion.getVersion();
        springbootVersion = (springbootVersion != null) ? " (v" + springbootVersion + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (springbootVersion.length() + SPRING_BOOT.length())) {
            padding.append(" ");
        }

        printStream.println(
                AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT, AnsiColor.DEFAULT, " ",
                        AnsiStyle.FAINT, springbootVersion)
        );
        printStream.println();
    }
}
