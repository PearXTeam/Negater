package ru.pearx.chatbot.discord.negater;

/*
 * Created by mrAppleXZ on 11.08.17 13:41.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

enum Main {
    INSTANCE;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Path cfg = Paths.get("config.json");
    private Logger log = LoggerFactory.getLogger("Negater");
    private List<String> adjectives;
    private List<String> nouns;
    private Random random = new Random();

    public List<String> getAdjectives() {
        return adjectives;
    }

    public List<String> getNouns() {
        return nouns;
    }

    public Random getRandom() {
        return random;
    }

    public void mainCycle() throws IOException, LoginException {
        if (!Files.exists(cfg)) {
            try (FileWriter wr = new FileWriter(cfg.toString())) {
                gson.toJson(new Config(), wr);
            }
            log.error("The config doesn't exist! Please fill in the token and restart the bot.");
            System.exit(-1);
        }
        else {
            JDABuilder bld = new JDABuilder(AccountType.BOT);
            try (FileReader rdr = new FileReader(cfg.toString())) {
                bld.setToken(gson.fromJson(rdr, Config.class).token);
            }
            adjectives = loadWords("adjectives");
            nouns = loadWords("nouns");
            bld.addEventListener(new EventListener()).buildAsync();
        }
    }

    private List<String> loadWords(String baseFileName) throws IOException {
        log.info("Loading the " + baseFileName + " list...");
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + baseFileName + ".txt"), StandardCharsets.UTF_8))) {
            List<String> ret = rdr.lines().collect(Collectors.toList());
            log.info("Done loading the " + baseFileName + " list.");
            return ret;
        }
    }

    public static void main(String... args) throws IOException, LoginException {
        INSTANCE.mainCycle();
    }
}
