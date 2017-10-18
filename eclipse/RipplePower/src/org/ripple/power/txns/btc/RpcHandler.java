package org.ripple.power.txns.btc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ripple.power.config.LSystem;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStreamReader;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class RpcHandler implements HttpHandler {

	/** JSON-RPC error codes */

	private static final int RPC_INVALID_REQUEST = -32600; // Invalid request
	private static final int RPC_METHOD_NOT_FOUND = -32601; // Method not found

	private static final int RPC_INTERNAL_ERROR = -32603; // Internal server
															// error

	/** Bitcoin RPC error codes */
	private static final int RPC_DATABASE_ERROR = -20; // Database error
	private static final int RPC_INVALID_PARAMETER = -8; // Invalid parameter
	private static final int RPC_INVALID_ADDRESS_OR_KEY = -5; // Invalid address
																// or key

	/** RPC port */
	private final int rpcPort;

	/** Allowed RPC hosts */
	private final List<InetAddress> rpcAllowIp;

	/** RPC user */
	private final String rpcUser;

	/** RPC password */
	private final String rpcPassword;

	/** HTTP server */
	private HttpServer server;

	/**
	 * Create the JSON-RPC request handler
	 *
	 * @param rpcPort
	 *            RPC port
	 * @param rpcAllowIp
	 *            List of allowed host addresses
	 * @param rpcUser
	 *            RPC user
	 * @param rpcPassword
	 *            RPC password
	 */
	public RpcHandler(int rpcPort, List<InetAddress> rpcAllowIp, String rpcUser, String rpcPassword) {
		this.rpcPort = rpcPort;
		this.rpcAllowIp = rpcAllowIp;
		this.rpcUser = rpcUser;
		this.rpcPassword = rpcPassword;
		//
		// Create the HTTP server using a single execution thread
		//
		try {
			server = HttpServer.create(new InetSocketAddress(rpcPort), 10);
			HttpContext context = server.createContext("/", this);
			context.setAuthenticator(new RpcAuthenticator(LSystem.applicationName));
			server.setExecutor(null);
			server.start();
			BTCLoader.info(String.format("RPC handler started on port %d", rpcPort));
		} catch (IOException exc) {
			BTCLoader.error("Unable to set up HTTP server", exc);
		}
	}

	/**
	 * Shutdowns the RPC request handler
	 */
	public void shutdown() {
		if (server != null) {
			BTCLoader.info("Stopping RPC handler");
			server.stop(5);
			BTCLoader.info("RPC handler stopped");
		}
	}

	/**
	 * Handle an HTTP request
	 *
	 * @param exchange
	 *            HTTP exchange
	 * @throws IOException
	 *             Error detected while handling the request
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			int responseCode;
			String responseBody;
			//
			// Get the HTTP request
			//
			InetSocketAddress requestAddress = exchange.getRemoteAddress();
			String requestMethod = exchange.getRequestMethod();
			Headers requestHeaders = exchange.getRequestHeaders();
			String contentType = requestHeaders.getFirst("Content-Type");
			Headers responseHeaders = exchange.getResponseHeaders();
			BTCLoader.debug(String.format("%s request received from %s", requestMethod, requestAddress.getAddress()));
			if (!rpcAllowIp.contains(requestAddress.getAddress())) {
				responseCode = HttpURLConnection.HTTP_UNAUTHORIZED;
				responseBody = "Your IP address is not authorized to access this server";
				responseHeaders.set("Content-Type", "text/plain");
			} else if (!exchange.getRequestMethod().equals("POST")) {
				responseCode = HttpURLConnection.HTTP_BAD_METHOD;
				responseBody = String.format("%s requests are not supported", exchange.getRequestMethod());
				responseHeaders.set("Content-Type", "text/plain");
			} else if (contentType == null || !contentType.equals("application/json-rpc")) {
				responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
				responseBody = "Content type must be application/json-rpc";
				responseHeaders.set("Content-Type", "text/plain");
			} else {
				responseBody = processRequest(exchange);
				responseCode = HttpURLConnection.HTTP_OK;
				responseHeaders.set("Content-Type", "application/json-rpc");
			}
			//
			// Return the HTTP response
			//
			responseHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate, private");
			responseHeaders.set("Server", "JavaBitcoin");
			byte[] responseBytes = responseBody.getBytes("UTF-8");
			exchange.sendResponseHeaders(responseCode, responseBytes.length);
			try (OutputStream out = exchange.getResponseBody()) {
				out.write(responseBytes);
			}
			BTCLoader.debug(String.format("RPC request from %s completed", requestAddress.getAddress()));
		} catch (IOException exc) {
			BTCLoader.error("Unable to process RPC request", exc);
			throw exc;
		}
	}

	/**
	 * Handle a JSON-RPC request
	 *
	 * @param exchange
	 *            HTTP exchange
	 * @throws IOException
	 *             I/O exception
	 * @return The response in JSON format
	 */
	private String processRequest(HttpExchange exchange) throws IOException {
		String method = "";
		Object params = null;
		Object id = null;
		Object result = null;
		int errorCode = 0;
		String errorMessage = "";
		//
		// Parse the request
		//
		try (InputStreamReader in = new InputStreamReader(exchange.getRequestBody(), "UTF-8")) {

			Object object = new JSONObject(new JSONTokener(in));
			if (object == null || !(object instanceof JSONObject)) {
				errorCode = RPC_INVALID_REQUEST;
				errorMessage = "The request must be a JSON structured object";
			} else {
				JSONObject request = (JSONObject) object;
				object = request.get("method");
				if (object == null || !(object instanceof String)) {
					errorCode = RPC_INVALID_REQUEST;
					errorMessage = "The request must include the 'method' field";
				} else {
					method = (String) object;
					params = request.get("params");
					id = request.get("id");
				}
			}
		} catch (Exception exc) {
			errorCode = RPC_INVALID_REQUEST;
			errorMessage = String.format("Parse exception %s", exc.getMessage());
			BTCLoader.error(errorMessage);
		} catch (Throwable exc) {
			errorCode = RPC_INTERNAL_ERROR;
			errorMessage = "Unable to parse request";
			BTCLoader.error(errorMessage, exc);
		}
		//
		// Process the request
		//
		if (errorCode == 0) {
			try {
				switch (method.toLowerCase()) {
				case "getinfo":
					result = getInfo();
					break;
				case "getlog":
					result = getLog();
					break;
				case "getpeerinfo":
					result = getPeerInfo();
					break;
				case "getblock":
					result = getBlock(params);
					break;
				case "getblockhash":
					result = getBlockHash(params);
					break;
				case "getstacktraces":
					result = getStackTraces();
					break;
				default:
					errorCode = RPC_METHOD_NOT_FOUND;
					errorMessage = String.format("Method '%s' is not recognized", method);
				}
			} catch (BlockStoreException exc) {
				errorCode = RPC_DATABASE_ERROR;
				errorMessage = "Unable to access database";
			} catch (RequestException exc) {
				errorCode = exc.getCode();
				errorMessage = exc.getMessage();
			} catch (IllegalArgumentException exc) {
				errorCode = RPC_INVALID_PARAMETER;
				errorMessage = exc.getMessage();
			}
		}
		//
		// Return the response
		//
		JSONObject response = new JSONObject();
		if (errorCode != 0) {
			JSONObject error = new JSONObject();
			error.put("code", errorCode);
			error.put("message", errorMessage);
			response.put("error", error);
		} else {
			response.put("result", result);
		}
		if (id != null)
			response.put("id", id);
		return response.toString();
	}

	/**
	 * Process 'getinfo' request
	 *
	 * @return Response as a JSONObject
	 */
	private JSONObject getInfo() {
		BTCLoader.debug("Processing 'getinfo'");
		JSONObject result = new JSONObject();
		//
		// Get the network difficulty as a Double
		//
		BigInteger targetDifficulty = BTCLoader.blockStore.getTargetDifficulty();
		double networkDifficulty = (NetParams.PROOF_OF_WORK_LIMIT.divide(targetDifficulty)).doubleValue();
		result.put("difficulty", networkDifficulty);
		//
		// Get the chain height as an Integer
		//
		result.put("blocks", BTCLoader.blockStore.getChainHeight());
		//
		// Get the connection count as an Integer
		//
		List<Peer> connectionList = BTCLoader.networkHandler.getConnections();
		result.put("connections", connectionList.size());
		return result;
	}

	/**
	 * Process 'getlog' request
	 *
	 * @return Response as a JSONArray
	 */
	private JSONArray getLog() {
		BTCLoader.debug("Processing 'getlog'");
		JSONArray result = new JSONArray();
		Logger logger = Logger.getLogger("");
		Handler[] handlers = logger.getHandlers();
		for (Handler handler : handlers) {
			if (handler instanceof MemoryLogHandler) {
				result.put(((MemoryLogHandler) handler).getMessages());
				break;
			}
		}
		return result;
	}

	/**
	 * Process 'getpeerinfo' request
	 *
	 * @return Response as a JSONArray
	 */
	private JSONArray getPeerInfo() {
		BTCLoader.debug("Processing 'getpeerinfo'");
		JSONArray result = new JSONArray();
		List<Peer> connectionList = BTCLoader.networkHandler.getConnections();
		for (Peer peer : connectionList) {
			JSONObject peerInfo = new JSONObject();
			PeerAddress addr = peer.getAddress();
			peerInfo.put("addr", addr.toString());
			peerInfo.put("conntime", addr.getTimeConnected());
			peerInfo.put("inbound", !addr.isOutbound());
			peerInfo.put("version", peer.getVersion());
			peerInfo.put("subver", peer.getUserAgent());
			peerInfo.put("services", Long.toString(peer.getServices()));
			peerInfo.put("banscore", peer.getBanScore());
			peerInfo.put("startingheight", peer.getHeight());
			result.put(peerInfo);
		}
		return result;
	}

	/**
	 * Process 'getblock' request
	 *
	 * @param params
	 *            Request parameters
	 * @return Response as a JSONObject
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 * @throws RequestException
	 *             Error while processing the request
	 */
	private JSONObject getBlock(Object params) throws BlockStoreException, RequestException {
		if (params == null || !(params instanceof JSONArray) || ((JSONArray) params).length() == 0)
			throw new RequestException(RPC_INVALID_PARAMETER, "The block hash must be specified");
		Object elem = ((JSONArray) params).get(0);
		if (!(elem instanceof String))
			throw new RequestException(RPC_INVALID_PARAMETER, "The block hash must be a string");
		BTCLoader.debug("Processing 'getblock' for " + (String) elem);
		JSONObject result = new JSONObject();
		StoredBlock storedBlock = BTCLoader.blockStore.getStoredBlock(new Sha256Hash((String) elem));
		if (storedBlock == null)
			throw new RequestException(RPC_INVALID_ADDRESS_OR_KEY, "Block not found");
		Block block = storedBlock.getBlock();
		JSONArray idList = new JSONArray();
		List<Transaction> txList = block.getTransactions();
		for (Transaction tx : txList) {
			idList.put(tx.getHashAsString());
		}
		result.put("hash", block.getHashAsString());
		result.put("previousblockhash", block.getPrevBlockHash().toString());
		result.put("merkleroot", block.getMerkleRoot().toString());
		result.put("size", block.getBytes().length);
		result.put("tx", idList);
		result.put("time", block.getTimeStamp());
		result.put("version", block.getVersion());
		result.put("nonce", block.getNonce());
		result.put("difficulty", block.getTargetDifficulty());
		result.put("height", storedBlock.getHeight());
		result.put("chainwork", storedBlock.getChainWork().toString(16));
		return result;
	}

	/**
	 * Process 'getblockhash' request
	 *
	 * @param params
	 *            Request parameters
	 * @return Response as a String
	 * @throws BlockStoreException
	 *             Unable to get block from database
	 * @throws RequestException
	 *             Error while processing the request
	 */
	private String getBlockHash(Object params) throws BlockStoreException, RequestException {
		if (params == null || !(params instanceof JSONArray) || ((JSONArray) params).length() == 0)
			throw new RequestException(RPC_INVALID_PARAMETER, "The block height must be specified");
		Object elem = ((JSONArray) params).get(0);
		if (!(elem instanceof Long))
			throw new RequestException(RPC_INVALID_PARAMETER, "The block height must be an integer");
		BTCLoader.debug("Processing 'getblockhash' for " + (Long) elem);
		Sha256Hash blockHash = BTCLoader.blockStore.getBlockId(((Long) elem).intValue());
		if (blockHash == null)
			throw new RequestException(RPC_INVALID_ADDRESS_OR_KEY, "Block not found");
		return blockHash.toString();
	}

	/**
	 * Process 'getstacktraces' request
	 *
	 * Response parameters: locks - An array of lock objects for locks with
	 * waiters threads - An array of thread objects
	 *
	 * Lock object: name - Lock class name hash - Lock identity hash code thread
	 * - Identifier of thread holding the lock
	 *
	 * Monitor object: name - Monitor class name hash - Monitor identity hash
	 * depth - Stack depth where monitor locked trace - Stack element where
	 * monitor locked
	 *
	 * Thread object: blocked - Lock object if thread is waiting on a lock id -
	 * Thread identifier locks - Array of monitor objects for locks held by this
	 * thread name - Thread name state - Thread state trace - Array of stack
	 * trace elements
	 *
	 * @return Response as a JSONObject
	 */
	private JSONObject getStackTraces() {
		JSONArray threadsJSON = new JSONArray();
		JSONArray locksJSON = new JSONArray();
		ThreadMXBean tmxBean = ManagementFactory.getThreadMXBean();
		boolean tmxMI = tmxBean.isObjectMonitorUsageSupported();
		ThreadInfo[] tList = tmxBean.dumpAllThreads(tmxMI, false);
		//
		// Generate the response
		//
		for (ThreadInfo tInfo : tList) {
			JSONObject threadJSON = new JSONObject();
			//
			// General thread information
			//
			threadJSON.put("id", tInfo.getThreadId());
			threadJSON.put("name", tInfo.getThreadName());
			threadJSON.put("state", tInfo.getThreadState().toString());
			//
			// Gather lock usage
			//
			if (tmxMI) {
				MonitorInfo[] mList = tInfo.getLockedMonitors();
				if (mList.length > 0) {
					JSONArray monitorsJSON = new JSONArray();
					for (MonitorInfo mInfo : mList) {
						JSONObject lockJSON = new JSONObject();
						lockJSON.put("name", mInfo.getClassName());
						lockJSON.put("hash", mInfo.getIdentityHashCode());
						lockJSON.put("depth", mInfo.getLockedStackDepth());
						lockJSON.put("trace", mInfo.getLockedStackFrame().toString());
						monitorsJSON.put(lockJSON);
					}
					threadJSON.put("locks", monitorsJSON);
				}
				if (tInfo.getThreadState() == Thread.State.BLOCKED) {
					LockInfo lInfo = tInfo.getLockInfo();
					if (lInfo != null) {
						JSONObject lockJSON = new JSONObject();
						lockJSON.put("name", lInfo.getClassName());
						lockJSON.put("hash", lInfo.getIdentityHashCode());
						lockJSON.put("thread", tInfo.getLockOwnerId());
						threadJSON.put("blocked", lockJSON);
						boolean addLock = true;
						for (int i = 0; i < locksJSON.length(); i++) {
							Object lock = locksJSON.get(i);
							if (((String) ((JSONObject) lock).get("name")).equals(lInfo.getClassName())) {
								addLock = false;
								break;
							}
						}
						if (addLock)
							locksJSON.put(lockJSON);
					}
				}
			}
			//
			// Add the stack trace
			//
			StackTraceElement[] elements = tInfo.getStackTrace();
			JSONArray traceJSON = new JSONArray();
			for (StackTraceElement element : elements)
				traceJSON.put(element.toString());
			threadJSON.put("trace", traceJSON);
			//
			// Add the thread to the response
			//
			threadsJSON.put(threadJSON);
		}
		//
		// Return the response
		//
		JSONObject response = new JSONObject();
		response.put("threads", threadsJSON);
		response.put("locks", locksJSON);
		return response;
	}

	/**
	 * RPC request authenticator
	 */
	private class RpcAuthenticator extends BasicAuthenticator {

		/**
		 * Crete a Basic Authenticator
		 *
		 * @param realm
		 *            HTTP realm
		 */
		public RpcAuthenticator(String realm) {
			super(realm);
		}

		/**
		 * Check the credentials for the RPC request
		 *
		 * @param user
		 *            User name
		 * @param password
		 *            User password
		 */
		@Override
		public boolean checkCredentials(String user, String password) {
			return (user.equals(rpcUser) && password.equals(rpcPassword));
		}
	}

	public int getRpcPort() {
		return rpcPort;
	}
}
