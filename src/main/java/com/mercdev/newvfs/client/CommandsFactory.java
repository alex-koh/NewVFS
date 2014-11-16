package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.CommandID;

public interface CommandsFactory {
	/**
	 * —оздает новую команду, соответствующую заданному индексу.
	 * @param id индекс команды;
	 * @param login им€ пользовател€;
	 * @return требуема€ команда.
	 */
	CommandBuilder getCommandBuilder(CommandID id);
	/**
	 * ”становка имени учетной записи.
	 * @param login им€ учетной записи.
	 */
	void setLogin(String login);
	/**
	 * –азделитель, примен€емый в записе пути к файлу.
	 * @return обычно один или два символа ("/").
	 */
	String getPathSeporator();
}