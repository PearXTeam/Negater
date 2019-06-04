package ru.pearx.chatbot.discord.negater;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by mrAppleXZ on 07.02.18 18:51.
 */
public class EventListener extends ListenerAdapter {
    private Pattern patternIfWhat = Pattern.compile("если что(?:-?то)?(.*?)(?:[?\\n,.$]|$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            String msg = e.getMessage().getContentDisplay();
            Matcher matIfWhat = patternIfWhat.matcher(msg);
            if (matIfWhat.find()) {
                String g = "Если что" + matIfWhat.group(1) + "?";
                e.getChannel().sendMessage(g).queue();
            }
            if (msg.toLowerCase().contains("кстати")) {
                Main main = Main.INSTANCE;
                Random rand = main.getRandom();
                List<String> adjs = main.getAdjectives();
                List<String> nouns = main.getNouns();
                char[] adj = adjs.get(rand.nextInt(adjs.size())).toCharArray();
                adj[0] = Character.toUpperCase(adj[0]);
                e.getChannel().sendMessage(new String(adj) + " " + nouns.get(rand.nextInt(nouns.size())) + ".").queue();
            }
        }
    }
}
