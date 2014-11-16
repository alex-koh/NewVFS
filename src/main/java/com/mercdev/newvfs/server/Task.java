package com.mercdev.newvfs.server;

import com.mercdev.newvfs.fs.FileSystem;
import com.mercdev.newvfs.interaction.Command;
/**
 * Интерфейс описывает задачу вызываемую в файловой системе
 * 
 * @author alex
 *
 */
interface Task{
	void invoke(FileSystem fs, Account account, Command command);
}
