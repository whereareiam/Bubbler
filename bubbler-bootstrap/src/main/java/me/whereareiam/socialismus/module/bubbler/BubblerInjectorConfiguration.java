package me.whereareiam.socialismus.module.bubbler;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
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
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.common.BubbleCoordinator;
import me.whereareiam.socialismus.module.bubbler.common.processor.BubbleMessageProcessor;
import me.whereareiam.socialismus.module.bubbler.common.worker.BubbleSelector;
import me.whereareiam.socialismus.module.bubbler.common.worker.container.ContentArranger;
import me.whereareiam.socialismus.module.bubbler.common.worker.container.ContentTimeEvaluator;
import me.whereareiam.socialismus.module.bubbler.common.worker.recipient.RecipientResolver;
import me.whereareiam.socialismus.module.bubbler.common.worker.recipient.RecipientSelector;

import java.util.Map;

public class BubblerInjectorConfiguration extends AbstractModule {
    private final Scheduler scheduler;
    private final PlatformInteractor platformInteractor;
    private final SerializationService serializationService;

    private final Registry<Reloadable> reloadableRegistry;
    private final Registry<Map<String, CommandEntity>> commandRegistry;
    private final RequirementEvaluatorService requirementEvaluator;

    private final LoggingHelper loggingHelper;
    private final ConfigurationManager configurationManager;
    private final ConfigurationLoader configurationLoader;

    private final CommandService commandService;
    private final PlayerContainerService playerContainerService;

    public BubblerInjectorConfiguration(Scheduler scheduler, PlatformInteractor platformInteractor, SerializationService serializationService, Registry<Reloadable> reloadableRegistry, Registry<Map<String, CommandEntity>> commandRegistry,
                                        RequirementEvaluatorService requirementEvaluator, LoggingHelper loggingHelper, ConfigurationManager configurationManager, ConfigurationLoader configurationLoader, CommandService commandService,
                                        PlayerContainerService playerContainerService) {
        this.scheduler = scheduler;
        this.platformInteractor = platformInteractor;
        this.serializationService = serializationService;

        this.reloadableRegistry = reloadableRegistry;
        this.commandRegistry = commandRegistry;
        this.requirementEvaluator = requirementEvaluator;

        this.loggingHelper = loggingHelper;
        this.configurationManager = configurationManager;
        this.configurationLoader = configurationLoader;

        this.commandService = commandService;
        this.playerContainerService = playerContainerService;
    }

    @Override
    protected void configure() {
        bind(BubbleCoordinationService.class).to(BubbleCoordinator.class);
        bind(new TypeLiteral<WorkerProcessor<BubbleMessage>>() {}).to(BubbleMessageProcessor.class);

        bind(Scheduler.class).toInstance(scheduler);
        bind(PlatformInteractor.class).toInstance(platformInteractor);
        bind(SerializationService.class).toInstance(serializationService);
        bind(RequirementEvaluatorService.class).toInstance(requirementEvaluator);

        bind(new TypeLiteral<Registry<Reloadable>>() {}).toInstance(reloadableRegistry);
        bind(new TypeLiteral<Registry<Map<String, CommandEntity>>>() {}).toInstance(commandRegistry);

        bind(LoggingHelper.class).toInstance(loggingHelper);
        bind(ConfigurationManager.class).toInstance(configurationManager);
        bind(ConfigurationLoader.class).toInstance(configurationLoader);

        bind(CommandService.class).toInstance(commandService);
        bind(PlayerContainerService.class).toInstance(playerContainerService);

        bind(RecipientResolver.class).asEagerSingleton();
        bind(RecipientSelector.class).asEagerSingleton();
        bind(BubbleSelector.class).asEagerSingleton();
        bind(ContentArranger.class).asEagerSingleton();
        bind(ContentTimeEvaluator.class).asEagerSingleton();
    }
}
