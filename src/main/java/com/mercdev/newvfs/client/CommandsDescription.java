package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.CommandID;

public interface CommandsDescription {
	/**
	 * ¬озвращает им€ команды по еЄ индексу.
	 * @param id индекс.
	 * @return им€ команды
	 */
	String getName(CommandID id);
	/**
	 * ¬озвращает индекс команды по еЄ имени.
	 * @param name строка имени команды;
	 * @return индекс команды или null, если команды с таким именем не 
	 * существует.
	 */
	CommandID getID(String name);
	/**
	 * ¬озвращает описание пути.
	 * @return описание пути.
	 */
	PathFormat getPathFormat();
}
