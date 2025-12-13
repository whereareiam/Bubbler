package me.whereareiam.socialismus.module.bubbler.common.config.template;

import com.google.inject.Singleton;
import me.whereareiam.configura.TemplateProvider;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;

@Singleton
public class BubblerMessagesTemplate implements TemplateProvider<BubblerMessages> {
    @Override
    public BubblerMessages supply(BubblerMessages config) {
        // Default values
        config.setNoPlayers("{prefix}<red>There are no players online to send a bubble message to.");
        config.setNoNearbyPlayers("{prefix}<red>There are no players nearby to send a bubble message to.");
        config.setNoBubbleSelected("{prefix}<red>There are no bubbles available to send a message with.");
        config.setMessageSuccess("{prefix}<white>Successfully sent a bubble message.");

        return config;
    }
}
