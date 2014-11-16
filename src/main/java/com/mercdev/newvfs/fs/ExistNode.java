package com.mercdev.newvfs.fs;

import java.util.Iterator;

import com.mercdev.newvfs.server.Account;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 2/26/14
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExistNode implements PathNode {
	// Предшествующий файл в записи пути
	File parrent;
	// Файл, к которому ведёт путь 
	File file;
	
	LockHandler locks;

	public ExistNode(File parrent, File file, LockHandler locks) {
		if (file==null)
			throw new NullPointerException("file==null");
		this.parrent = parrent;
		this.file = file;
		this.locks = locks;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public PathNode find(String name) {
		if (isDirectory())
			for(File var : file.getChildren())
				if(var.getName().equals(name))
					return new ExistNode(file,var,locks);
		return new EmptyNode(file,name);
	}

	@Override
	public boolean isExist() {
		return true;
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isDirectory() {
		return !file.isFile();
	}

	@Override
	public boolean isUnLock() {
		return locks.isLock(file);
	}

	@Override
	public boolean lock(Account c) {
		return locks.lock(file,c);
	}

	@Override
	public boolean unlock(Account c) {
		return locks.unLock(file,c);
	}

	@Override
	public boolean remove() {
		// Если несуществует или он заблокирован, то ничего не делать
		if (isUnLock()) {
			// Если у директории есть потомки, то ничего не делать
			if (isDirectory()&&file.getChildren().iterator().hasNext())
				return false;
			// Удалить файл из родительской директории
			Iterator<File> p =  parrent.getChildren().iterator();
			while(p.hasNext())
				if(p.next().equals(file))
					break;
			p.remove();
			return true;
		}
		return false;
	}

	@Override
	public boolean mkDir(FileMaker maker) {
		return false;
	}

	@Override
	public boolean mkFile(FileMaker maker) {
		return false;
	}

	@Override
	public Iterable<PathNode> getChildren() {
		if (isFile())
			return null;
		return new Iterable<PathNode>() {
			@Override
			public Iterator<PathNode> iterator() {
				final Iterator<File> children = file.getChildren().iterator();
				return new Iterator<PathNode>() {
					@Override
					public boolean hasNext() {
						return children.hasNext();
					}

					@Override
					public PathNode next() {
						return new ExistNode(file,children.next(),locks);
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
/*TODO Убрать
	@Override
	public Iterable<PathNode> getUnLockedChildren() {
		if (!isExist()&&isFile())
			return new NullIterator();
		return new IterableImpl(
			new Iterator<PathNode>() {
				Iterator<File> p;
				File file = null;
				@Override
				public boolean hasNext() {
					return file==null;
				}

				@Override
				public PathNode next() {
					PathNode node = new NodeImpl(file,NodeImpl.this);
					nextUnLock();
					return node;
				}

				@Override
				public void remove() {
					throw UnsupportedOperationException();
				}
				private void nextUnLock() {
					file = null;
					while(p.hasNext()) {
						file = p.next();
						if (file.getLock()==0)
							break;
					}
				}
				{
					p = file.getCildren();
					nextUnLock();
				}
			}
		);
	}

	@Override
	public Iterable<PathNode> getParrents() {
		return new IterableImpl(
			new Iterator<PathNode>() {
				PathNode node = getParrent();
				@Override
				public boolean hasNext() {
					return node==null;
				}

				@Override
				public PathNode next() {
					PathNode temp = node;
					node = node.getParrent();
					return node;
				}

				@Override
				public void remove() {
					throw UnsupportedOperationException();
				}
			}
		)
	}
*/
}
