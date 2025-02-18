package me.whereareiam.socialismus.module.bubbler.common;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import lombok.Getter;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

@Getter
@Singleton
public class BubbleQueue implements Reloadable {
    private static final Map<DummyPlayer, Queue<BubbleMessage>> bubbleQueue = new HashMap<>();

    @Inject
    public BubbleQueue(Registry<Reloadable> reloadableRegistry) {
        reloadableRegistry.register(this);
    }

    public static BubbleMessage queue(DummyPlayer dummyPlayer) {
        return bubbleQueue.get(dummyPlayer).poll();
    }

    public static void add(BubbleMessage bubbleMessage) {
        bubbleQueue.computeIfAbsent(bubbleMessage.getSender(), k -> new LinkedList<>()).add(bubbleMessage);
    }

    public static void remove(DummyPlayer dummyPlayer, BubbleMessage bubbleMessage) {
        bubbleQueue.get(dummyPlayer).remove(bubbleMessage);
    }

    public static void clear(DummyPlayer dummyPlayer) {
        bubbleQueue.remove(dummyPlayer);
    }
    
    public static void clearAll() {
        bubbleQueue.clear();
    }

    @Override
    public void reload() {
        clearAll();
    }
}
