package me.whereareiam.socialismus.module.bubbler.api.input;

import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

public interface BubbleCoordinationService {
    void coordinate(BubbleMessage bubbleMessage);
}
