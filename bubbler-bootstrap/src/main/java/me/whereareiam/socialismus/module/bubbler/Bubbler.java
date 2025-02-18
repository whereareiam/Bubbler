package me.whereareiam.socialismus.module.bubbler;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Map;
import java.util.stream.Stream;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.input.event.EventManager;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.input.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.api.output.module.SocialisticModule;
import me.whereareiam.socialismus.module.bubbler.command.CommandRegistrar;
import me.whereareiam.socialismus.module.bubbler.common.BubbleQueue;
import me.whereareiam.socialismus.module.bubbler.common.listener.ChatBroadcastListener;
import me.whereareiam.socialismus.module.bubbler.common.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.configuration.ConfigBinder;

public class Bubbler extends SocialisticModule {
  private final Injector parentInjector;
  private final Registry<Reloadable> reloadableRegistry;
  private final Registry<Map<String, CommandEntity>> commandRegistry;
  private Injector injector;

  @Inject
  public Bubbler(
      Injector parentInjector,
      Registry<Reloadable> reloadableRegistry,
      Registry<Map<String, CommandEntity>> commandRegistry) {
    this.parentInjector = parentInjector;
    this.reloadableRegistry = reloadableRegistry;
    this.commandRegistry = commandRegistry;
  }

  @Override
  public void onLoad() {
    injector =
        Guice.createInjector(
            new BubblerInjectorConfiguration(
                parentInjector.getInstance(Scheduler.class),
                parentInjector.getInstance(PlatformInteractor.class),
                parentInjector.getInstance(SerializationService.class),
                reloadableRegistry,
                commandRegistry,
                parentInjector.getInstance(RequirementEvaluatorService.class),
                parentInjector.getInstance(LoggingHelper.class),
                parentInjector.getInstance(ConfigurationManager.class),
                parentInjector.getInstance(ConfigurationLoader.class),
                parentInjector.getInstance(CommandService.class),
                parentInjector.getInstance(PlayerContainerService.class)),
            new ConfigBinder(workingPath));

    injector.getInstance(BubbleQueue.class);
    ProtocolVersion.setVersion(
        injector.getInstance(PlatformInteractor.class).getServerVersion());

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
  public void onUnload() {}
}
