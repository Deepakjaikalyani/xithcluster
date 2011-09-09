package br.edu.univercidade.cc.xithcluster.communication.protocol;

public class Record {
	
	private RecordType type;
	
	private Object[] fields;
	
	public Record(RecordType type, Object[] fields) {
		this.type = type;
		this.fields = fields;
	}

	public RecordType getType() {
		return type;
	}
	
	public void setType(RecordType type) {
		this.type = type;
	}
	
	public Object[] getFields() {
		return fields;
	}
	
	public void setFields(Object[] fields) {
		this.fields = fields;
	}
	
	public Integer getFieldAsInt(int i) {
		return (Integer) fields[0];
	}
	
	public byte[] getFieldAsBytes(int i) {
		return (byte[]) fields[0];
	}
	
}
