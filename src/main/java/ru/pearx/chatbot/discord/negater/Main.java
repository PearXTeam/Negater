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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

enum Main
{
    INSTANCE;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Path cfg = Paths.get("config.json");
    private Logger log = LoggerFactory.getLogger("Negater");

    public void mainCycle() throws IOException, LoginException
    {
        if(!Files.exists(cfg))
        {
            try (FileWriter wr = new FileWriter(cfg.toString()))
            {
                gson.toJson(new Config(), wr);
            }
            log.error("The config doesn't exist! Please fill in the token and restart the bot.");
            System.exit(-1);
        }
        else
        {
            JDABuilder bld = new JDABuilder(AccountType.BOT);
            try(FileReader rdr = new FileReader(cfg.toString()))
            {
                bld.setToken(gson.fromJson(rdr, Config.class).token);
            }
            bld.addEventListener(new EventListener()).buildAsync();
        }
    }

    public static void main(String... args) throws IOException, LoginException
    {
        INSTANCE.mainCycle();
    }
}
