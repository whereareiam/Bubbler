package me.whereareiam.socialismus.module.bubbler.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.command.CommandBase;
import me.whereareiam.socialismus.api.output.command.CommandCooldown;
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
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
	private final BubbleCoordinationService coordinationService;
	private final Provider<BubblerCommands> commands;

	@Inject
	public BubbleCommand(
			BubbleCoordinationService coordinationService,
			Provider<BubblerCommands> commands
	) {
		super(COMMAND_NAME);

		this.coordinationService = coordinationService;
		this.commands = commands;
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
