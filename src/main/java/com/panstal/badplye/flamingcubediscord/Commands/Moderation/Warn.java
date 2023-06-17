package com.panstal.badplye.flamingcubediscord.Commands.Moderation;

import com.panstal.badplye.flamingcubediscord.Logs.Moderation;
import com.panstal.badplye.flamingcubediscord.Main;
import com.panstal.badplye.flamingcubediscord.MySQL.Entries.PunishmentRecord;
import com.panstal.badplye.flamingcubediscord.Utilities.TimeParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class Warn extends Punishment {
    public Warn(SlashCommandInteractionEvent cmdEvent, Main plugin) {
        super(cmdEvent, plugin);
    }
    public void executeCommand() {
        event.deferReply().queue();

        EmbedBuilder response = new EmbedBuilder();
        if(!checkPermissions()) {
            response.setTitle("Permission Error");
            response.setDescription("This user has a equal or higher rank than you!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }
        if(!checkProof()) {
            response.setTitle("Permission Error");
            response.setDescription("Your rank requires proof to be included in this punishment!");
            response.setColor(Color.RED);
            event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
            return;
        }

        String reason = event.getOption("reason").getAsString();
        String id = generateID();

        sendNotice(target,"Warned", reason, id, "N/A");

        Guild staffDiscord = event.getJDA().getGuildById(mcPlugin.getConfig().getString("staff-guild"));
        TextChannel logChannel = staffDiscord.getTextChannelById(mcPlugin.getConfig().getString("mod-log"));

        if(!checkProof() || event.getOption("proof") != null) {
            logChannel.sendMessageEmbeds(Moderation.staffLog(noticeSent, mcPlugin, id, "Warning", "N/A", target, sender, event.getOption("reason").getAsString(), event.getOption("proof").getAsString()).build()).queue();
            PunishmentRecord dbRecord = new PunishmentRecord(id, String.valueOf(System.currentTimeMillis() / 1000), "Warning", target.getId(), sender.getId(), event.getOption("reason").getAsString(), event.getOption("proof").getAsString(), Long.parseLong("9999999999"), true);
            database.createPunishment(dbRecord);

        } else {
            logChannel.sendMessageEmbeds(Moderation.staffLog(noticeSent, mcPlugin, id, "Warning", "N/A", target, sender, event.getOption("reason").getAsString()).build()).queue();
            PunishmentRecord dbRecord = new PunishmentRecord(id, String.valueOf(System.currentTimeMillis() / 1000), "Warning", target.getId(), sender.getId(), event.getOption("reason").getAsString(), "None", Long.parseLong("9999999999"), true);
            database.createPunishment(dbRecord);
        }

        // send a message to the channel to confirm that the user was banned
        response.setTitle("Success");
        response.setDescription(target.getName() + " has been warned!");
        response.setColor(Color.GREEN);
        event.getHook().sendMessageEmbeds(response.build()).setEphemeral(true).queue();
    }
}
