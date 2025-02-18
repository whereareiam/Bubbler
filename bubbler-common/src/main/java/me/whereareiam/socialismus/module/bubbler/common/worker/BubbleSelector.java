package me.whereareiam.socialismus.module.bubbler.common.worker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.type.Participants;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;

import java.util.List;

@Singleton
public class BubbleSelector {
    private final LoggingHelper loggingHelper;
    private final RequirementEvaluatorService requirementValidator;

    // Configs
    private final Provider<BubblerMessages> bubblerMessages;
    private final Provider<BubblerSettings> bubblerSettings;
    private final Provider<List<Bubble>> bubbles;

    // Communication
    private final SerializationService serializer;

    @Inject
    public BubbleSelector(LoggingHelper loggingHelper, RequirementEvaluatorService requirementEvaluator, SerializationService serializer,
                          WorkerProcessor<BubbleMessage> workerProcessor, Provider<BubblerMessages> bubblerMessages,
                          Provider<BubblerSettings> bubblerSettings, Provider<List<Bubble>> bubbles) {
        this.loggingHelper = loggingHelper;
        this.requirementValidator = requirementEvaluator;
        this.serializer = serializer;
        this.bubblerMessages = bubblerMessages;
        this.bubblerSettings = bubblerSettings;
        this.bubbles = bubbles;

        workerProcessor.addWorker(new Worker<>(this::selectChat, 50, true, false));
    }

    public BubbleMessage selectChat(BubbleMessage bubbleMessage) {
        loggingHelper.debug("Selecting bubble for user " + bubbleMessage.getSender().getUsername());

        Bubble bubble = bubbles.get().parallelStream()
                .filter(b -> requirementValidator.check(b.getRequirements().get(Participants.SENDER), bubbleMessage.getSender()))
                .findFirst()
                .orElse(null);

        if (bubble == null) {
            notifyAboutAbsentBubble(bubbleMessage);
            bubbleMessage.setCancelled(true);

            return bubbleMessage;
        }

        loggingHelper.debug("Selected bubble " + bubble.getId() + " for user " + bubbleMessage.getSender().getUsername());
        bubbleMessage.setBubble(bubble);

        return bubbleMessage;
    }

    private void notifyAboutAbsentBubble(BubbleMessage bubbleMessage) {
        if (!bubblerSettings.get().getNotify().isNotifyNoBubbleSelected()) return;

        bubbleMessage.getSender().sendMessage(
                serializer.format(bubbleMessage.getSender(), bubblerMessages.get().getNoBubbleSelected())
        );
    }
}
