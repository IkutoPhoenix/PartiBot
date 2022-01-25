package ovh.nemesis.partibot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.Locale;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().toLowerCase(Locale.ROOT).startsWith("partibot!message <#") && e.getMessage().getContentRaw().toLowerCase(Locale.ROOT).endsWith(">") && e.getMember().hasPermission(Permission.ADMINISTRATOR) && !e.getAuthor().isBot()) {
            String chanId = e.getMessage().getContentRaw().toLowerCase(Locale.ROOT).split("<#|>")[1];
            Message message = e.getGuild().getTextChannelById(chanId).sendMessage("Message :)").complete();
            e.getTextChannel().sendMessage("The message id is : `" + message.getIdLong() + "`, its channel id is : `" + e.getTextChannel().getIdLong() + "`").queue();
        } else if (e.getMessage().getContentRaw().toLowerCase(Locale.ROOT).equalsIgnoreCase("partibot!update") && e.getMember().hasPermission(Permission.ADMINISTRATOR) && !e.getAuthor().isBot()) {
            if (!PartiBot.update()) {
                e.getTextChannel().sendMessage("Config Update failed").queue();
            } else {
                e.getTextChannel().sendMessage("Config Updated").queue();
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        JSONArray data = PartiBot.jsonObject.getJSONArray("messages");
        for (int i = 0; i < data.length(); i++) {
            for (int k = 0; k < Math.min(5, data.getJSONObject(i).getJSONArray("rows").length()); ++k) {
                JSONArray buttons = data.getJSONObject(i).getJSONArray("rows").getJSONArray(k);
                for (int j = 0; j < Math.min(5, buttons.length()); ++j) {
                    if (event.getComponentId().equalsIgnoreCase(buttons.getJSONObject(j).getString("id"))) {
                        Guild guild = PartiBot.jda.getTextChannelById(data.getJSONObject(i).getLong("channel_id")).getGuild();
                        Member member = event.getMember();
                        Role role = guild.getRoleById(buttons.getJSONObject(j).getLong("role_id"));
                        if (member.getRoles().contains(role)) {
                            guild.removeRoleFromMember(member.getIdLong(), role).queue();
                            event.reply("You have now the role : **" + event.getButton().getLabel() + "**").setEphemeral(true).queue();
                        } else {
                            guild.addRoleToMember(member.getIdLong(), role).queue();
                            event.reply("You no longer have the role : **" + event.getButton().getLabel() + "**").setEphemeral(true).queue();
                        }
                        //event.editButton(Button.of(ButtonStyle.valueOf(buttons.getString("type").toUpperCase()), data.getJSONObject(i).getJSONArray("buttons").getJSONObject(j).getString("id"), data.getJSONObject(i).getJSONArray("buttons").getJSONObject(j).getString("text"))).queue();
                        break;
                    }
                }
            }
        }
    }

}
