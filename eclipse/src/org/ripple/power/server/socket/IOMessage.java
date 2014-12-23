package org.ripple.power.server.socket;

class IOMessage {

	public static final int TYPE_DISCONNECT = 0;

	public static final int TYPE_CONNECT = 1;

	public static final int TYPE_HEARTBEAT = 2;

	public static final int TYPE_MESSAGE = 3;

	public static final int TYPE_JSON_MESSAGE = 4;

	public static final int TYPE_EVENT = 5;

	public static final int TYPE_ACK = 6;

	public static final int TYPE_ERROR = 7;

	public static final int TYPE_NOOP = 8;

	public static final int FIELD_TYPE = 0;

	public static final int FIELD_ID = 1;

	public static final int FIELD_ENDPOINT = 2;

	public static final int FIELD_DATA = 3;

	public static final int NUM_FIELDS = 4;

	private final String[] fields = new String[NUM_FIELDS];

	private int type;
	
	public IOMessage(int type, String id, String namespace, String data) {
		this.type = type;
		this.fields[FIELD_ID] = id;
		this.fields[FIELD_TYPE] = "" + type;
		this.fields[FIELD_ENDPOINT] = namespace;
		this.fields[FIELD_DATA] = data;
	}

	public IOMessage(int type, String namespace, String data) {
		this(type, null, namespace, data);
	}

	public IOMessage(String message) {
		String[] fields = message.split(":", NUM_FIELDS);
		for (int i = 0; i < fields.length; i++) {
			this.fields[i] = fields[i];
			if(i == FIELD_TYPE)
				this.type = Integer.parseInt(fields[i]);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < fields.length; i++) {
			builder.append(':');
			if (fields[i] != null)
				builder.append(fields[i]);
		}
		return builder.substring(1);
	}

	public int getType() {
		return type;
	}

	public String getId() {
		return fields[FIELD_ID];
	}

	public void setId(String id) {
		fields[FIELD_ID] = id;
	}

	public String getEndpoint() {
		return fields[FIELD_ENDPOINT];
	}

	public String getData() {
		return fields[FIELD_DATA];
	}

}
