package me.whereareiam.socialismus.module.bubbler.common.worker.recipient;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

import java.util.stream.Collectors;

@Singleton
public class RecipientResolver {
    private final PlayerContainerService playerContainer;

    @Inject
    public RecipientResolver(WorkerProcessor<BubbleMessage> workerProcessor, PlayerContainerService playerContainer) {
        this.playerContainer = playerContainer;
        workerProcessor.addWorker(new Worker<>(this::resolveRecipients, 0, true, false));
    }

    private BubbleMessage resolveRecipients(BubbleMessage bubbleMessage) {
        if (!bubbleMessage.getRecipients().isEmpty()) return bubbleMessage;

        bubbleMessage.setRecipients(playerContainer.getPlayers().stream()
                .filter(p -> !p.equals(bubbleMessage.getSender()))
                .filter(p -> p.getLocation().equals(bubbleMessage.getSender().getLocation()))
                .collect(Collectors.toSet()));

        return bubbleMessage;
    }
}
