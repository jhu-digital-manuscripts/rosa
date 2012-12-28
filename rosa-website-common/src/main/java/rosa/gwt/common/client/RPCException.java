package rosa.gwt.common.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Save a user friendly error message to be passed back through RPC.
 */
public class RPCException extends Exception implements
		IsSerializable {
	private static final long serialVersionUID = 1L;

	private String message;

	public RPCException() {
	}

	public RPCException(String message) {
		super(message);
		this.message = message;
	}

	public RPCException(Throwable cause) {
		this.message = cause.getMessage();
	}

	public RPCException(String message, Throwable cause) {
		this.message = message + " , Caused by: " + cause.getMessage();
	}

	public String getMessage() {
		return message;
	}
}
