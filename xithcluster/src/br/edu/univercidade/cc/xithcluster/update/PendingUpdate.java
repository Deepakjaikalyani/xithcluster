package br.edu.univercidade.cc.xithcluster.update;

import java.util.HashMap;
import java.util.Map;

public class PendingUpdate {
	
	public enum Type {
		BRANCH_ADDED, BRANCH_REMOVED, NODE_ADDED, NODE_REMOVED, PROPERTY_CHANGED, SWITCH_CHILD_CHANGED, TRANSFORM_CHANGED, NODE_COMPONENT_CHANGED, SCISSOR_RECT_CHANGED, CLIPPER_CHANGED;
		
		public static Type getByOrdinal(int i) {
			for (Type pendingUpdateType : values()) {
				if (pendingUpdateType.ordinal() == i) {
					return pendingUpdateType;
				}
			}
			
			return null;
		}
	}
	
	private Object target;
	
	private Type type;
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public PendingUpdate(Type type, Object target) {
		this.type = type;
		this.target = target;
	}
	
	public Type getType() {
		return type;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public Object getData(String key) {
		return map.get(key);
	}
	
	public void setData(String key, Object data) {
		this.map.put(key, data);
	}
	
	public void removeData(String key) {
		setData(key, null);
	}
	
}
