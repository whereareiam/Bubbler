package me.whereareiam.socialismus.module.bubbler.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BubblerSettings {
    private int minRecipients;
    private int maxQueueSize;
    private int headDistance;
    private Notify notify;

    @Getter
    @Setter
    @ToString
    public static class Notify {
        private boolean notifyNoPlayers;
        private boolean notifyNoNearbyPlayers;
        private boolean notifyNoBubbleSelected;
    }
}
