/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package org.ripple.power.txns;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.util.Charsetfunctions;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.StringUtils;

import com.ripple.client.enums.Command;
import com.ripple.utils.MathUtils;

public class TemporaryWebSocket {

	private final static boolean ok(JSONObject obj) {
		return "success".equalsIgnoreCase(obj.optString("status"));
	}

	public final static void post(final Command cmd, final JSONObject post, final Rollback status) {

		try {
			URI uri = new URI(RPClient.getRippledNode());

			WebSocketClient c = new WebSocketClient(uri, new Draft_17()) {

				private boolean isText = false, isContinuous = false;

				private StringBuffer _buffer = new StringBuffer();

				public void success(String message) {

					if (StringUtils.isEmpty(message)) {
						status.error(new JSONObject("{error:\"null\"}"));
					} else {
						JSONObject obj = new JSONObject(message);
						if (ok(obj)) {
							status.success(obj);
						} else {
							status.error(obj);
						}
					}
				}

				public void fail(String message) {
					status.error(new JSONObject("{error:\"" + message + "\"}"));
				}

				@Override
				public void onOpen(ServerHandshake handshakedata) {
					if (post.isNull("id")) {
						post.put("id", MathUtils.random(1, 10));
					}
					if (post.isNull("command")) {
						post.put("command", cmd.toString());
					}
					send(post.toString());
				}

				@Override
				public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
					synchronized (frame) {
						onFragment(frame);
						// 这部分不仅仅会传递字符类型，不过基于ripple访问要求，接收字符串就够了
						Framedata.Opcode curop = frame.getOpcode();
						if (!isText) {
							isText = (curop == Framedata.Opcode.TEXT);
						}
						isContinuous = (curop == Framedata.Opcode.CONTINUOUS);
						if (isText || isContinuous) {
							try {
								_buffer.append(Charsetfunctions.stringUtf8(frame.getPayloadData()));
							} catch (InvalidDataException e) {
							}
						}
						if (frame.isFin() && isContinuous) {
							onMessage(_buffer.toString());
						}

					}
				}

				@Override
				public void onMessage(String message) {
					success(message);
					close();
				}

				@Override
				public void onError(Exception ex) {
					fail(ex.getMessage());
					close();
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {

				}

			};
			if (LSystem.applicationProxy != null) {
				if (LSystem.applicationProxy.isSocket()) {
					c.setSocket(LSystem.applicationProxy.getHostname(), LSystem.applicationProxy.getPort());
				} else {
					c.setProxy(LSystem.applicationProxy.getProxy());
				}
			}
			c.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
