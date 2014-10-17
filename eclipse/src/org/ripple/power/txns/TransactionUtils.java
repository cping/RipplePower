package org.ripple.power.txns;

import org.address.ripple.RippleObject;
import org.address.ripple.RippleSeedAddress;
import org.address.ripple.RippleSerializer;
import org.address.ripple.RippleSigner;
import org.address.utils.CoinUtils;
import org.json.JSONObject;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;

public class TransactionUtils {

	public final static long getSequence(JSONObject res) {
		JSONObject obj = res.getJSONObject("result");
		return obj.getJSONObject("account_data").getLong("Sequence");
	}
	
	public final static void submitBlob(final RippleSeedAddress seed,
			final RippleObject rippleobj, final Rollback back) throws Exception {
			RippleObject rbo = new RippleSigner(seed.getPrivateKey(0))
					.sign(rippleobj);
			
			byte[] signedTXBytes = new RippleSerializer()
					.writeBinaryObject(rbo).array();
			RPClient client = RPClient.ripple();
			if (client != null) {
				Request req = client.newRequest(Command.submit);
				req.json("tx_blob",CoinUtils.toHex(signedTXBytes));
				req.once(Request.OnSuccess.class, new Request.OnSuccess() {
					@Override
					public void called(Response response) {
						if (back != null) {
							back.success(response.message);
						}
					}
				});
				req.once(Request.OnError.class, new Request.OnError() {
					@Override
					public void called(Response response) {
						if (back != null) {
							back.error(response.message);
						}
					}
				});
				req.request();
			}
	}

}
