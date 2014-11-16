package com.mercdev.newvfs.interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Path {
	private boolean beginFromRoot;
	private Collection<String> terms;
	public Path(boolean beginFromRoot, Collection<String> terms) {
		this.beginFromRoot = beginFromRoot;
		terms = new ArrayList<String>(terms);
		this.terms = Collections.unmodifiableCollection(terms);		
	}
	public boolean getBeginFromRoot() {
		return beginFromRoot;
	}
	public Collection<String> getTerms() {
		return terms;
	}
}
