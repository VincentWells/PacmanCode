package pacman;

import java.util.Vector;

public class Stack<E> extends Vector<E> {
/**
 * this is the stack class we used in our HPAir lab. I repurposed it for this assignment
 */
	Vector<E> stackVec;

	public Stack() {
		stackVec = new Vector<E>();
	}

	public boolean empty() {
		if(super.size()==0)
		{
			return true;
		}
		else return false;
	}

	public E peek() {
		if(!empty())
		return super.get(super.size()-1);
		else return null;
	}

	public E pop() {
		if(!empty()){
		E e = super.get(super.size()-1);
		super.remove(e);
		return e;
		}
		else return null;
	}

	public E push(E item) {
		super.add(item);
		return item;
	}
}
