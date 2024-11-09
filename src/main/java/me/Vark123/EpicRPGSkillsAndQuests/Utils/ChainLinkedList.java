package me.Vark123.EpicRPGSkillsAndQuests.Utils;

import java.util.LinkedList;

public class ChainLinkedList<T> extends LinkedList<T> {

	private static final long serialVersionUID = -9076605189548869072L;

	public T getNext(T element) {
		int index = indexOf(element);
		if(index < 0 || (index + 1) >= size())
			return null;
		return get(index + 1);
	}

	public T getPrevious(T element) {
		int index = indexOf(element);
		if(index < 0 || (index - 1) < 0)
			return null;
		return get(index - 1);
	}
	
}
