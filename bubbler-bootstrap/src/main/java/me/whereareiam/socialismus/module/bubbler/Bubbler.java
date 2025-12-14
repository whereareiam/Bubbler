package me.whereareiam.socialismus.module.bubbler;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.command.CommandRegistrar;
import me.whereareiam.socialismus.module.bubbler.common.CommonConfiguration;
import me.whereareiam.socialismus.module.bubbler.common.listener.ChatBroadcastListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;

import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class Bubbler extends SocialisticModule {
	private final Injector parentInjector;
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void onLoad() {
		injector =
				Guice.createInjector(
						new BubblerInjectorConfiguration(
								parentInjector.getInstance(Scheduler.class),
								parentInjector.getInstance(PlatformInteractor.class),
								parentInjector.getInstance(ConfigurationTypeResolver.class),
								reloadableRegistry,
								parentInjector.getInstance(RequirementEvaluatorService.class),
								parentInjector.getInstance(CommandService.class),
								parentInjector.getInstance(PlayerRegistry.class)),
						new CommonConfiguration(workingPath));

		ProtocolVersion.VERSION = injector.getInstance(PlatformInteractor.class).getServerVersion();

		EventManager eventManager = parentInjector.getInstance(EventManager.class);
		Stream.of(injector.getInstance(ChatBroadcastListener.class)).forEach(eventManager::register);
	}

	@Override
	public void onEnable() {
		injector.getInstance(CommandRegistrar.class).registerCommands();
	}

	@Override
	public void onDisable() {
		injector.getInstance(Scheduler.class).cancelByModule("bubbler");
	}

	@Override
	public void onUnload() {
	}
}
