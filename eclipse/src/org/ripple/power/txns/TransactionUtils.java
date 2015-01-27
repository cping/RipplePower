package org.ripple.power.txns;

import org.json.JSONObject;
import org.ripple.power.CoinUtils;
import org.ripple.power.RippleObject;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.RippleSerializer;
import org.ripple.power.RippleSigner;
import org.ripple.power.ui.RPClient;

import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.VariableLength;
import com.ripple.core.coretypes.hash.HalfSha512;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.core.coretypes.hash.prefixes.HashPrefix;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.serialized.BytesList;
import com.ripple.core.serialized.MultiSink;
import com.ripple.core.types.known.tx.Transaction;
import com.ripple.crypto.ecdsa.IKeyPair;
import com.ripple.crypto.ecdsa.Seed;

public class TransactionUtils {

	public final static void submitBlob(final RippleSeedAddress seed,
			final Transaction txn, final String fee, final long sequence,
			final Rollback back) throws Exception {
		IKeyPair keyPair = Seed.getKeyPair(seed.getPrivateKey());
		VariableLength pubKey = new VariableLength(keyPair.pubBytes());
		txn.put(UInt32.Sequence, new UInt32(sequence));
		if (fee != null) {
			txn.put(Amount.Fee, Amount.fromString(fee));
		}
		txn.put(VariableLength.SigningPubKey, pubKey);
		Hash256 signingHash = txn.signingHash();
		VariableLength signature = new VariableLength(keyPair.sign(signingHash
				.bytes()));
		txn.put(VariableLength.TxnSignature, signature);
		BytesList blob = new BytesList();
		HalfSha512 id = HalfSha512.prefixed256(HashPrefix.transactionID);
		txn.toBytesSink(new MultiSink(blob, id));
		String tx_blob = blob.bytesHex();
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			req.json("tx_blob", tx_blob);
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

	public final static long getSequence(JSONObject res) {
		JSONObject obj = res.getJSONObject("result");
		return obj.getJSONObject("account_data").getLong("Sequence");
	}

	public final static void submitBlob(final RippleSeedAddress seed,
			final RippleObject rippleobj, final Rollback back) throws Exception {

		RippleObject rbo = new RippleSigner(seed.getPrivateKey(0))
				.sign(rippleobj);

		byte[] signedTXBytes = new RippleSerializer().writeBinaryObject(rbo)
				.array();
		RPClient client = RPClient.ripple();
		if (client != null) {
			Request req = client.newRequest(Command.submit);
			req.json("tx_blob", CoinUtils.toHex(signedTXBytes));
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
