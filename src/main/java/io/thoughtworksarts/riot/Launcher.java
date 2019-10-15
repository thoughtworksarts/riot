package io.thoughtworksarts.riot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {
    public static void main(String... args) {
        log.info("(Launcher) Starting Riot...");
        Main.main(args);
    }
}
