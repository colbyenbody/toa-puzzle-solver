package com.toapuzzlesolver;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ToaPuzzleSolver
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ToaPuzzleSolverPlugin.class);
		RuneLite.main(args);
	}
}