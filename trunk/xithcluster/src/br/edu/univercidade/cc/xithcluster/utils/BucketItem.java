package br.edu.univercidade.cc.xithcluster.utils;

class BucketItem<T> {
	
	private T item;
	
	private boolean inUse;

	public BucketItem(T item) {
		if (item == null) {
			throw new IllegalArgumentException();
		}
		
		this.item = item;
	}

	public boolean isInUse() {
		return inUse;
	}
	
	public void setInUse() {
		inUse = true;
	}
	
	public void setNotInUse() {
		inUse = false;
	}
	
	public T getItem() {
		return item;
	}
	
	public boolean contains(T item) {
		return this.item.equals(item);
	}
	
}