package me.whereareiam.socialismus.module.bubbler.common.listener;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.event.EventListener;
import me.whereareiam.socialismus.api.input.event.base.EventOrder;
import me.whereareiam.socialismus.api.input.event.base.SocialisticEvent;
import me.whereareiam.socialismus.api.input.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;

@Singleton
public class ChatBroadcastListener implements EventListener {
    private final BubbleCoordinationService coordinationService;
    private final Provider<BubblerSettings> bubblerSettings;

    @Inject
    public ChatBroadcastListener(BubbleCoordinationService coordinationService, Provider<BubblerSettings> bubblerSettings) {
        this.coordinationService = coordinationService;
        this.bubblerSettings = bubblerSettings;
    }

    @SocialisticEvent(EventOrder.NORMAL)
    public void onChatBroadcast(ChatBroadcastEvent event) {
        if (event.getChatMessage().getChat().getParameters().getType().isGlobal()) return;
        if (event.getChatMessage().getRecipients().size() <= bubblerSettings.get().getMinRecipients()) return;

        coordinationService.coordinate(BubbleMessage.builder()
                .sender(event.getChatMessage().getSender())
                .recipients(event.getChatMessage().getRecipients())
                .content(event.getChatMessage().getContent())
                .build()
        );
    }
}
