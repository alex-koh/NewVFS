package com.mercdev.newvfs.client;


import java.util.Collection;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;

public interface CommandBuilder {
	/**
	 * Возвращает индекс команды, которая создается в данном сборщике команд.
	 * @return индекс команды.
	 */
	CommandID getCommandID();
	/**
	 * Устанавливает логин для данной команды.
	 * @param login логин текущего подключения.
	 */
	void setLogin(String login);
	/**
	 * Добавляет параметры с которым вызывается команда
	 * @param param параметор вызова.
	 */
	void setParams(Collection<String> param);
	/**
	 * Возвращает готовую команду.
	 * @return готовая команда, которая отправится на сервер; 
	 * @throws IllegalArgumentException появляется, если параметры заданы 
	 * не верно или не в полном объеме.
	 */
	Command getResult() throws IllegalArgumentException;
}