package me.whereareiam.socialismus.module.bubbler.common.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.event.EventListener;
import me.whereareiam.socialismus.api.input.event.base.EventOrder;
import me.whereareiam.socialismus.api.input.event.base.SocialisticEvent;
import me.whereareiam.socialismus.api.input.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcastListener implements EventListener {
	private final BubbleCoordinationService coordinationService;

	@SocialisticEvent(EventOrder.NORMAL)
	public void onChatBroadcast(ChatBroadcastEvent event) {
		coordinationService.coordinate(BubbleMessage.builder()
				.sender(event.getChatMessage().getSender())
				.recipients(event.getChatMessage().getRecipients())
				.content(event.getChatMessage().getContent())
				.build()
		);
	}
}
