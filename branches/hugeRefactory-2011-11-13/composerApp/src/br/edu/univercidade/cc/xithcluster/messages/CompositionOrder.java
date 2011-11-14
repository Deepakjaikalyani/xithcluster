package br.edu.univercidade.cc.xithcluster.messages;

public class CompositionOrder implements Comparable<CompositionOrder> {
	
	private Long creationTime;
	
	private Integer order;
	
	public CompositionOrder(int order) {
		creationTime = System.currentTimeMillis();
		this.order = order;
	}
	
	@Override
	public int compareTo(CompositionOrder o) {
		if (order == o.order) {
			return creationTime.compareTo(o.creationTime);
		}
		
		return order.compareTo(o.order);
	}
	
}
