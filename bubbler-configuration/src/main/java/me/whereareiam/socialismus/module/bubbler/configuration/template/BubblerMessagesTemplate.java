package me.whereareiam.socialismus.module.bubbler.configuration.template;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;

@Singleton
public class BubblerMessagesTemplate implements DefaultConfig<BubblerMessages> {
    @Override
    public BubblerMessages getDefault() {
        BubblerMessages config = new BubblerMessages();

        // Default values
        config.setNoPlayers("{prefix}<red>There are no players online to send a bubble message to.");
        config.setNoNearbyPlayers("{prefix}<red>There are no players nearby to send a bubble message to.");
        config.setNoBubbleSelected("{prefix}<red>There are no bubbles available to send a message with.");
        config.setMessageSuccess("{prefix}<white>Successfully sent a bubble message.");

        return config;
    }
}
