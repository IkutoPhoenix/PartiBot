package ovh.nemesis.partibot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.toilelibre.libe.curl.Curl.$;
import static org.toilelibre.libe.curl.Curl.curl;

public class PartiBot extends ListenerAdapter {

    static JDA jda;
    static JSONObject jsonObject;

    public static void main(String[] args) {
        JDABuilder jdaBuilder = JDABuilder.createDefault("hidden")
                .addEventListeners(new EventListener());
        try {
            jda = jdaBuilder.build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (!update()) {
            System.out.println("Update error");
            System.exit(4);
        }

        final Runnable refresh = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = $("https://particubes.com");
                    String beta = "Beta-Testers : " + (result.split("<p id=\"next-user-info\">You'll be <span class=\"highlight\">#")[1].split("</span>")[0]);

                    if (!jda.getGuildChannelById(863491833678266428L).getName().equalsIgnoreCase(beta)) {
                        jda.getGuildChannelById(863491833678266428L).getManager().setName(beta).queue();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final ScheduledExecutorService user = Executors.newScheduledThreadPool(1);
        user.scheduleAtFixedRate(refresh, 0L, 5L, TimeUnit.MINUTES);


    }



    public static boolean update() {
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = new JSONObject($("https://raw.githubusercontent.com/IkutoPhoenix/PartiBot/master/config.json"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        jsonObject = jsonObject1;
        JSONArray data = jsonObject.getJSONArray("messages");
        for (int i = 0; i < data.length(); i++) {
            try {
                MessageBuilder messageBuilder = new MessageBuilder(data.getJSONObject(i).getString("text"));
                ArrayList<Button> buttons = new ArrayList<>();
                for (int j = 0; j < Math.min(5, data.getJSONObject(i).getJSONArray("buttons").length()); ++j) {
                    Button button = Button.of(ButtonStyle.valueOf(data.getJSONObject(i).getJSONArray("buttons").getJSONObject(j).getString("type").toUpperCase()), data.getJSONObject(i).getJSONArray("buttons").getJSONObject(j).getString("id"), data.getJSONObject(i).getJSONArray("buttons").getJSONObject(j).getString("text"));
                    buttons.add(button);
                }
                messageBuilder.setActionRows(ActionRow.of(buttons));
                jda.getTextChannelById(data.getJSONObject(i).getLong("channel_id")).editMessageById(data.getJSONObject(i).getLong("id"), messageBuilder.build()).complete();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
