package ovh.nemesis.partibot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.toilelibre.libe.curl.Curl.$;
import static org.toilelibre.libe.curl.Curl.curl;

public class PartiBot {

    static JDA jda;

    public static void main(String[] args) {
        JDABuilder jdaBuilder = JDABuilder.createDefault("ODYzNDAwNTc4OTYxMjQ0MTYw.YOmWcw.745x-NSxnZGc3MgyqVC4no4KoYk");
        try {
            jda = jdaBuilder.build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        final Runnable refresh = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = $("https://particubes.com");
                    //System.out.println(result);
                    String beta = "Beta-Testers : " + (result.split("<p id=\"next-user-info\">You'll be <span class=\"highlight\">#")[1].split("</span>")[0]);

                    if (!jda.getGuildChannelById(863491833678266428L).getName().equalsIgnoreCase(beta)) {
                        jda.getGuildChannelById(863491833678266428L).getManager().setName(beta).queue();
                    }

                    /*int number = Integer.parseInt($("https://raw.githubusercontent.com/IkutoPhoenix/PartiBot/master/id?token=AH5AUPLEXN3KWXVBC73ZAUDA6U36G").replaceAll("\n", ""));

                    JSONObject res = new JSONObject($("https://api.nemesis.ovh/Particubes/githubmilestone.php?id=" + number));

                    if (res.getString("state").equalsIgnoreCase("open")) {
                        int nb = res.getInt("closed_issues");
                        int total = nb + res.getInt("open_issues");

                        DecimalFormat decimalFormat = new DecimalFormat("#%");
                        beta = res.getString("title") + " - " + nb + "/" + total + " - " + decimalFormat.format(nb / ((float) total));
                        if (!jda.getGuildChannelById(863401615629221898L).getName().equalsIgnoreCase(beta)) {
                            jda.getGuildChannelById(863401615629221898L).getManager().setName(beta).queue();
                        }
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final ScheduledExecutorService user = Executors.newScheduledThreadPool(1);
        user.scheduleAtFixedRate(refresh, 0L, 5L, TimeUnit.MINUTES);


    }

}
