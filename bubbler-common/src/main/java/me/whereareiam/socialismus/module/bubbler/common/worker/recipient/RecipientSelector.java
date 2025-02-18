package me.whereareiam.socialismus.module.bubbler.common.worker.recipient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.type.Participants;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RecipientSelector {
    private final RequirementEvaluatorService requirementEvaluator;
    private final Provider<BubblerSettings> settings;
    private final Provider<BubblerMessages> messages;
    private final PlatformInteractor interactor;
    private final LoggingHelper loggingHelper;
    private final SerializationService serializer;

    @Inject
    public RecipientSelector(WorkerProcessor<BubbleMessage> workerProcessor, RequirementEvaluatorService requirementEvaluator, Provider<BubblerSettings> settings,
                             Provider<BubblerMessages> messages, PlatformInteractor interactor, LoggingHelper loggingHelper, SerializationService serializer) {
        this.requirementEvaluator = requirementEvaluator;
        this.settings = settings;
        this.messages = messages;
        this.interactor = interactor;
        this.loggingHelper = loggingHelper;
        this.serializer = serializer;

        workerProcessor.addWorker(new Worker<>(this::selectRecipients, 100, true, false));
    }

    private BubbleMessage selectRecipients(BubbleMessage bubbleMessage) {
        if (bubbleMessage.getBubble() == null) {
            bubbleMessage.setCancelled(true);
            return bubbleMessage;
        }

        Bubble bubble = bubbleMessage.getBubble();
        DummyPlayer sender = bubbleMessage.getSender();

        int oldRecipients = bubbleMessage.getRecipients().size();
        Set<DummyPlayer> recipients = bubbleMessage.getRecipients();

        recipients = recipients.parallelStream()
                .filter(recipient -> isWithinRadius(sender, recipient, bubble.getDisplay().getRadius()))
                .filter(recipient -> requirementEvaluator.check(bubble.getRequirements().get(Participants.RECIPIENT), recipient))
                .collect(Collectors.toSet());

        if (recipients.isEmpty() && settings.get().getNotify().isNotifyNoNearbyPlayers())
            sender.sendMessage(serializer.format(sender, messages.get().getNoNearbyPlayers()));

        bubbleMessage.setRecipients(recipients);
        loggingHelper.debug("Recipients before: " + oldRecipients + ", after: " + bubbleMessage.getRecipients().size());

        if (recipients.isEmpty()) bubbleMessage.setCancelled(true);
        if (recipients.isEmpty() && settings.get().getNotify().isNotifyNoPlayers())
            sender.sendMessage(serializer.format(sender, messages.get().getNoPlayers()));

        return bubbleMessage;
    }

    private boolean isWithinRadius(DummyPlayer sender, DummyPlayer recipient, double radius) {
        return interactor.areWithinRange(sender.getUniqueId(), recipient.getUniqueId(), radius);
    }
}