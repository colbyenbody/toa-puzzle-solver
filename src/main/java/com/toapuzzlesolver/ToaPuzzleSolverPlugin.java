package com.toapuzzlesolver;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.NavigationButton;

import java.util.Arrays;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "TOA Puzzle Solver"
)
public class ToaPuzzleSolverPlugin extends Plugin {


	@Inject
	private Client client;

	private int lastRegionId = -1;

	//Adds all instance IDs
	private static final List<Integer> TOA_INSTANCE_IDS = Arrays.asList( 13454, 14164,14676,15188,15444,
			15700,15955,13906,14162,
			14674,15186,15698,15954,
			15953,14160,14672,15184,15696);
	//IDs for specific rooms
	private static final List<Integer> TOA_PUZZLE_IDS = Arrays.asList(14674,15186,14162,15698);
	private static final int TOA_PUZZLE_AKKHA = 14674;
	private static final int TOA_PUZZLE_BABA = 15186;
	private static final int TOA_PUZZLE_KEPHRI = 14162;
	private static final int TOA_PUZZLE_ZEBAK = 15698;

	//
	public enum PuzzleRoom {
		NONE("Not in a puzzle room",0),
		AKKHA("Akkha",TOA_PUZZLE_AKKHA),
		BABA("Ba-Ba",TOA_PUZZLE_BABA),
		KEPHRI("Kephri",TOA_PUZZLE_KEPHRI),
		ZEBAK("Zebak",TOA_PUZZLE_ZEBAK);

		private String roomName;
		private int roomId;
		private PuzzleRoom(String roomName, int roomId) {
			this.roomName = roomName;
			this.roomId = roomId;
		}
		public String getRoomName() {
			return this.roomName;
		}
		public int getRoomId() {
			return this.roomId;
		}

	}
	private PuzzleRoom currentRoom = PuzzleRoom.NONE;

	@Inject
	private ToaPuzzleSolverConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("TOA Puzzle Solver started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("TOA Puzzle Solver stopped!");
	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		ChatMessageType chatMessageType = chatMessage.getType();

		if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
			return;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		checkRegion();
	}

	// Getter for current room
	public PuzzleRoom getRoom() {
		return currentRoom;
	}

	// Setter for current room
	private void setRoom(int regionId) {
		switch(regionId) {
			case TOA_PUZZLE_AKKHA:
				currentRoom = PuzzleRoom.AKKHA;
				return;
			case TOA_PUZZLE_BABA:
				currentRoom = PuzzleRoom.BABA;
				return;
			case TOA_PUZZLE_KEPHRI:
				currentRoom = PuzzleRoom.KEPHRI;
				return;
			case TOA_PUZZLE_ZEBAK:
				currentRoom = PuzzleRoom.ZEBAK;
				return;
			default:
				currentRoom = PuzzleRoom.NONE;
				return;
		}
	}

	// checks if in TOA region ID
	private void checkRegion() {
		final int regionId = getRegionId();
		if (!TOA_INSTANCE_IDS.contains(regionId)) {
			lastRegionId = regionId;
			return;
		}
		if(regionId != lastRegionId) {
			if (TOA_INSTANCE_IDS.contains(regionId)) {
				setRoom(regionId);
			}
		}
		lastRegionId = regionId;
	}

	// Getter for region ID
	private int getRegionId() {
		Player player = client.getLocalPlayer();
		if (player == null) {
			return -1;
		}
		return WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
	}


	@Provides
	ToaPuzzleSolverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ToaPuzzleSolverConfig.class);
	}
}
