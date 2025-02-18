package me.whereareiam.socialismus.module.bubbler.configuration.template;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;

@Singleton
public class BubblerSettingsTemplate implements DefaultConfig<BubblerSettings> {
    @Override
    public BubblerSettings getDefault() {
        BubblerSettings config = new BubblerSettings();

        // Default values
        config.setMinRecipients(1);
        config.setMaxQueueSize(30);
        config.setHeadDistance(1);

        BubblerSettings.Notify notify = new BubblerSettings.Notify();
        notify.setNotifyNoPlayers(true);
        notify.setNotifyNoNearbyPlayers(true);
        notify.setNotifyNoBubbleSelected(false);

        config.setNotify(notify);

        return config;
    }
}
