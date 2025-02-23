package me.whereareiam.socialismus.module.bubbler.command.executor;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.serializer.SerializationService;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;
import java.util.Set;

@Singleton
public class BubbleCommand extends CommandBase {
	private static final String COMMAND_NAME = "bubble";

	private final SerializationService serializer;
	private final BubbleCoordinationService coordinationService;

	private final Provider<BubblerCommands> commands;
	private final Provider<BubblerMessages> messages;

	@Inject
	public BubbleCommand(SerializationService serializer, BubbleCoordinationService coordinationService, Provider<BubblerCommands> commands,
	                     Provider<BubblerMessages> messages) {
		super(COMMAND_NAME);

		this.serializer = serializer;
		this.coordinationService = coordinationService;
		this.commands = commands;
		this.messages = messages;
	}

	@Command("%command." + COMMAND_NAME)
	@CommandDescription("%description." + COMMAND_NAME)
	@CommandCooldown("%cooldown." + COMMAND_NAME)
	@Permission("%permission." + COMMAND_NAME)
	public void onCommand(DummyPlayer dummyPlayer, @Argument(value = "message") String message) {
		coordinationService.coordinate(BubbleMessage.builder()
				.sender(dummyPlayer)
				.recipients(Set.of())
				.content(Component.text(message))
				.build()
		);

		dummyPlayer.sendMessage(serializer.format(dummyPlayer, messages.get().getMessageSuccess()));

		User user = PacketEvents.getAPI().getPlayerManager().getUser(dummyPlayer.getAudience());

		new TextDisplayPacket.Builder()
				.withText(Component.text("Bubble!"))
				.withPosition(new Vector3d(0, 120, 0))
				.build()
				.send(user);
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commands.get().getCommands().get(COMMAND_NAME);
	}

	@Override
	public Map<String, String> getTranslations() {
		CommandEntity command = commands.get().getCommands().get("bubble");

		return Map.of(
				"command." + command.getAliases().getFirst() + ".name", command.getUsage().replace("{alias}", String.join("|", command.getAliases())),
				"command." + command.getAliases().getFirst() + ".permission", command.getPermission(),
				"command." + command.getAliases().getFirst() + ".description", command.getDescription(),
				"command." + command.getAliases().getFirst() + ".usage", command.getUsage()
		);
	}
}
