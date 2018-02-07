package ru.pearx.chatbot.discord.negater;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by mrAppleXZ on 07.02.18 18:51.
 */
public class EventListener extends ListenerAdapter
{
    private Pattern pat = Pattern.compile("если что(?:-?то)?(.*?)(?:[?\\n,.$]|$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Override
    public void onMessageReceived(MessageReceivedEvent e)
    {
        if(!e.getAuthor().isBot())
        {
            String msg = e.getMessage().getContentDisplay();
            Matcher mat = pat.matcher(msg);
            if (mat.find())
            {
                String g = "Если что" + mat.group(1) + "?";
                e.getChannel().sendMessage(g).queue();
            }
        }
    }
}
