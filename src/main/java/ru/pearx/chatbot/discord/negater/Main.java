package ru.pearx.chatbot.discord.negater;

/*
 * Created by mrAppleXZ on 11.08.17 13:41.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Main
{
    INSTANCE;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Path cfg = Paths.get("config.json");
    private IDiscordClient discord;
    private Logger log = LoggerFactory.getLogger("Negater");
    private Pattern pat = Pattern.compile("если что(?:-?то)?(.*?)(?:[?\\n,.$]|$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public void mainCycle() throws IOException
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
            try(FileReader rdr = new FileReader(cfg.toString()))
            {
                discord = new ClientBuilder().withToken(gson.fromJson(rdr, Config.class).token).build();
            }
            discord.login();
            discord.getDispatcher().registerListener(this);
        }
    }

    @EventSubscriber
    public void onMessage(MessageReceivedEvent e)
    {
        String msg = e.getMessage().getContent();
        Matcher mat = pat.matcher(msg);
        if(mat.find())
        {
            String g = "Если что" + mat.group(1) + "?";
            e.getChannel().sendMessage(g);
        }
    }

    public static void main(String... args) throws IOException
    {
        INSTANCE.mainCycle();
    }
}
