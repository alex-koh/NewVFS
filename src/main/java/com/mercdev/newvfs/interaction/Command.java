package com.mercdev.newvfs.interaction;

import java.io.Serializable;
import java.util.Collection;

public interface Command extends Serializable {
	/**
	 * ћетод возвращает индекс команды
	 * @return индекс команды
	 */
	CommandID getID();
	/**
	 * ћетод возвращает список параметров команды
	 * @return список параметров команды
	 */
	Collection<Path> getParams();
	/**
	 * ћетод возвращает им€ пользовател€, отправившего команду
	 * @return им€ пользовател€
	 */
	String getLogin();
}