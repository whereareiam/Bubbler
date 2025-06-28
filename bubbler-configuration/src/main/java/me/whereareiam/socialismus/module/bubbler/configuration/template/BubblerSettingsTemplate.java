package me.whereareiam.socialismus.module.bubbler.configuration.template;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.type.BubbleType;

@Singleton
public class BubblerSettingsTemplate implements DefaultConfig<BubblerSettings> {
	@Override
	public BubblerSettings getDefault() {
		BubblerSettings config = new BubblerSettings();

		// Default values
		config.setBubbleType(BubbleType.TEXT_DISPLAY);
		config.setMinRecipients(1);
		config.setMaxQueueSize(30);

		BubblerSettings.Notify notify = new BubblerSettings.Notify();
		notify.setNotifyNoPlayers(true);
		notify.setNotifyNoNearbyPlayers(true);
		notify.setNotifyNoBubbleSelected(false);

		config.setNotify(notify);

		BubblerSettings.Animation animation = new BubblerSettings.Animation();
		animation.setPopoutDelay(500);
		animation.setExpansionDelay(500);

		config.setAnimation(animation);

		return config;
	}
}
