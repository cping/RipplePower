package org.ripple.power.txns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.data.AccountResponse;
import org.ripple.power.txns.data.ExchangesResponse;
import org.ripple.power.txns.data.Meta;
import org.ripple.power.txns.data.Take;
import org.ripple.power.txns.data.TransactionsResponse;
import org.ripple.power.utils.HttpRequest;

import com.ripple.core.enums.TransactionFlag;

public class RippleDataApi {

	// PS:Java环境下SSL支持有限，不能使用https读取此站，否则解码协议时会崩……dotNet下无事……我给oracle提交bug没人理我……
	private static String DATA_URL = "http://data.ripple.com";

	public static void setDataAPI_URL(String url) {
		DATA_URL = url;
	}

	public static String getDataAPI_URL() {
		return DATA_URL;
	}

	private static String open(String site) {
		HttpRequest request = HttpRequest.get(site);
		request.acceptGzipEncoding();
		String result = null;
		try {
			request.uncompress(true);
			if (request.ok()) {
				result = request.body();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String open(String site, Map<?, ?> maps) {
		HttpRequest request = HttpRequest.get(site, maps, true);
		request.acceptGzipEncoding();
		String result = null;
		try {
			request.uncompress(true);
			if (request.ok()) {
				result = request.body();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static TransactionsResponse transactions(String address) {
		if (!AccountFind.isRippleAddress(address)) {
			return null;
		}
		String link = DATA_URL
				+ String.format("/v2/accounts/%s/transactions", address);
		String result = open(link);
		if (result != null) {
			TransactionsResponse response = new TransactionsResponse();
			response.from(new JSONObject(result));
			return response;
		}
		return null;
	}

	public static ExchangesResponse exchanges(IssuedCurrency base,
			IssuedCurrency counter) {
		return exchanges(base.getTake(), counter.getTake());
	}

	public static ExchangesResponse exchanges(Take base, Take counter) {
		String link = DATA_URL
				+ String.format("/v2/exchanges/%s/%s", base, counter);
		String result = open(link);
		System.out.println(result);
		if (result != null) {
			ExchangesResponse response = new ExchangesResponse();
			response.from(new JSONObject(result));
			return response;
		}
		return null;
	}

	public static double exchange_rates(Take base, Take counter) {
		String link = DATA_URL
				+ String.format("/v2/exchange_rates/%s/%s", base, counter);
		String result = open(link);
		if (result != null) {
			System.out.println(result);
		}
		return -1;
	}

	public static ExchangesResponse exchanges(String address) {
		if (!AccountFind.isRippleAddress(address)) {
			return null;
		}
		String link = DATA_URL
				+ String.format("/v2/accounts/%s/exchanges", address);
		String result = open(link);
		if (result != null) {
			ExchangesResponse response = new ExchangesResponse();
			response.from(new JSONObject(result));
			return response;
		}
		return null;
	}

	public static JSONObject transactionsFind(final AccountInfo accountinfo,
			final ArrayList<IssuedCurrency> issues, String address) {
		JSONObject json = null;
		if (!AccountFind.isRippleAddress(address)) {
			return json;
		}
		String link = DATA_URL
				+ String.format("/v2/accounts/%s/transactions", address);
		String result = open(link);
		if (result != null) {

			json = new JSONObject(result);

			JSONArray arrays = json.optJSONArray("transactions");

			for (int i = 0; i < arrays.length(); i++) {

				TransactionTx transactionTx = new TransactionTx();

				JSONObject transaction = arrays.getJSONObject(i);
				JSONObject tx = transaction.optJSONObject("tx");
				JSONObject meta = transaction.optJSONObject("meta");
				String type = tx.optString("TransactionType");

				transactionTx.account = tx.optString("Account");
				transactionTx.destination = tx.optString("Destination");

				// transactionTx.date_number = date;
				transactionTx.date = transaction.optString("date");

				String fee = CurrencyUtils.getRippleToValue(String.valueOf(tx
						.optLong("Fee")));

				transactionTx.fee = fee;
				transactionTx.hash = tx.optString("hash");
				transactionTx.sequence = tx.optLong("Sequence");
				transactionTx.offersSequence = tx.optLong("OfferSequence");
				transactionTx.inLedger = tx.optLong("inLedger");
				transactionTx.ledgerIndex = tx.optLong("ledger_index");
				transactionTx.flags = tx.optLong("Flags");
				transactionTx.clazz = type;
				if (transactionTx.flags != 0) {
					transactionTx.isPartialPayment = (TransactionFlag.PartialPayment == transactionTx.flags);
					transactionTx.flagsName = TransactionFlagMap
							.getString(transactionTx.flags);
				} else {
					transactionTx.flagsName = "Empty";
				}
				if (tx.has("SendMax")) {
					transactionTx.sendMax = CurrencyUtils.getIssuedCurrency(tx
							.get("SendMax"));
				}
				transactionTx.signingPubKey = tx.optString("SigningPubKey");
				transactionTx.txnSignature = tx.optString("TxnSignature");

				if (meta != null) {
					transactionTx.metaString = meta.toString();
					transactionTx.meta = new Meta();
					transactionTx.meta.from(meta);

					if (meta.has("AffectedNodes")) {
						JSONArray affectedNodes = meta
								.getJSONArray("AffectedNodes");
						int size = affectedNodes.length();

						for (int j = 0; j < size; j++) {
							JSONObject affectedNode = affectedNodes
									.getJSONObject(j);
							JSONArray names = affectedNode.names();
							for (int n = 0; n < names.length(); n++) {
								String key = names.getString(n);

								TransactionTx.AffectedNode node = new TransactionTx.AffectedNode();
								transactionTx.affectedNodeList.add(node);

								node.name = key;
								JSONObject ledger_node = affectedNode
										.getJSONObject(key);
								node.ledgerEntryType = ledger_node
										.optString("LedgerEntryType");
								if (node.ledgerEntryType != null) {

									node.previousTxnID = ledger_node.optString(

									"PreviousTxnID");
									node.txid = node.previousTxnID != null ? node.previousTxnID
											: transactionTx.hash;
									node.ledgerIndex = ledger_node
											.optString("LedgerIndex");
									node.previousTxnLgrSeq = ledger_node
											.optLong("PreviousTxnLgrSeq");

									JSONObject fields = null;
									if (ledger_node.has("FinalFields")) {
										fields = ledger_node
												.optJSONObject("FinalFields");
									} else if (ledger_node.has("NewFields")) {
										fields = ledger_node
												.optJSONObject("NewFields");
									}
									if (fields != null) {

										node.account = fields
												.optString("Account");
										node.regularKey = fields
												.optString("RegularKey");

										node.takerGetsIssuer = fields
												.optString("TakerGetsIssuer");
										node.takerPaysIssuer = fields
												.optString("TakerPaysIssuer");
										node.exchangeRate = fields
												.optString("ExchangeRate");
										node.takerPaysCurrency = fields
												.optString("TakerPaysCurrency");
										node.takerGetsCurrency = fields
												.optString("TakerGetsCurrency");

										node.balance = CurrencyUtils
												.getIssuedCurrency(fields
														.opt("Balance"));

										node.highLimit = CurrencyUtils
												.getIssuedCurrency(fields
														.opt("HighLimit"));
										node.lowLimit = CurrencyUtils
												.getIssuedCurrency(fields
														.opt("LowLimit"));

										node.owner = fields.optString("Owner");
										node.rootIndex = fields
												.optString("RootIndex");
										node.indexPrevious = fields
												.optString("IndexPrevious");
										node.indexNext = fields
												.optString("IndexNext");
										node.sequence = fields
												.optLong("Sequence");
										node.ownerCount = fields
												.optLong("OwnerCount");
										node.transferRate = fields
												.optLong("TransferRate");

										node.takerGets = CurrencyUtils
												.getIssuedCurrency(fields
														.opt("TakerGets"));
										node.takerPays = CurrencyUtils
												.getIssuedCurrency(fields
														.opt("TakerPays"));
										if (fields.has("Flags")) {
											node.flags = fields
													.optLong("Flags");
										} else {
											node.flags = transactionTx.flags;
										}
										node.sell = OfferPrice
												.isSellOrder(node.flags);
										node.sellOrBuy = node.sell ? "sell"
												: "buy";
									}

								}
							}
						}

					}

				}

				switch (type) {
				case "Payment":

					transactionTx.destinationTag = tx.optLong("DestinationTag");
					transactionTx.invoiceID = tx.optString("InvoiceID");

					IssuedCurrency currency = null;
					String counterparty = null;
					if (meta != null && meta.has("DeliveredAmount")) {
						currency = CurrencyUtils.getIssuedCurrency(meta
								.opt("DeliveredAmount"));
					} else {
						currency = CurrencyUtils.getIssuedCurrency(tx
								.opt("Amount"));
					}
					transactionTx.currency = currency;
					String flagType;
					if (address.equals(transactionTx.account)) {
						if (address.equals(transactionTx.destination)) {
							flagType = "Exchange";
						} else {
							flagType = "Send";
							counterparty = transactionTx.destination;
							int index = AccountFind.inCredits(issues, currency);
							if (index >= 0) {

							} else {
								issues.add(currency);
							}
						}
					} else if (address.equals(transactionTx.destination)) {
						flagType = "Receive";
						counterparty = transactionTx.account;
					} else {
						flagType = "Convert";
					}
					transactionTx.mode = flagType;
					transactionTx.counterparty = counterparty;
					break;
				case "TrustSet":
					Object limitAmount = tx.opt("LimitAmount");
					if (limitAmount != null) {
						transactionTx.currency = CurrencyUtils
								.getIssuedCurrency(limitAmount);
						transactionTx.trusted = transactionTx.currency.issuer
								.toString();
					}
					break;
				case "OfferCreate":
					transactionTx.get = CurrencyUtils.getIssuedCurrency(tx
							.opt("TakerGets"));
					transactionTx.pay = CurrencyUtils.getIssuedCurrency(tx
							.opt("TakerPays"));
					break;
				case "OfferCancel":
					JSONArray affectedNodes = meta
							.optJSONArray("AffectedNodes");
					for (int n = 0; n < affectedNodes.length(); n++) {
						JSONObject obj = affectedNodes.getJSONObject(n);
						if (obj.has("DeletedNode")) {
							JSONObject deleted = obj
									.getJSONObject("DeletedNode");
							String ledgerEntryType = deleted
									.optString("LedgerEntryType");
							if ("Offer".equals(ledgerEntryType)) {
								JSONObject ff = deleted
										.optJSONObject("FinalFields");
								String ffactount = ff.optString("Account");
								if (ffactount.equals(transactionTx.account)) {
									transactionTx.get = CurrencyUtils
											.getIssuedCurrency(ff
													.opt("TakerGets"));
									transactionTx.pay = CurrencyUtils
											.getIssuedCurrency(ff
													.opt("TakerPays"));
								}
							}
						}

					}
					break;
				case "AccountSet":
					// Ignore
					break;
				}
				accountinfo.transactions.add(transactionTx);

			}

		}
		return json;

	}

	public static AccountResponse accounts(String address) {
		if (!AccountFind.isRippleAddress(address)) {
			return null;
		}
		String link = DATA_URL + String.format("/v2/accounts/%s", address);
		String result = open(link);
		if (result != null) {
			AccountResponse response = new AccountResponse();
			response.from(new JSONObject(result));
			return response;
		}
		return null;
	}

	public static CurrencyGateway gateways() {
		String link = DATA_URL + "/v2/gateways";
		String result = open(link);
		if (result != null) {
			CurrencyGateway gateway = new CurrencyGateway();
			gateway.copyFrom(new JSONObject(result));
			return gateway;
		}
		return null;
	}

	public static CurrencyGateway.Item currencyTogateways(String curName) {
		return gateways().find(curName);
	}

	/**
	 * 以指定网关数据为标准，转换一种货币价格为另外一种的
	 * 
	 * @param amount
	 * @param baseCurrency
	 * @param baseIssuer
	 * @param exCurrency
	 * @param exIssuer
	 * @return
	 */
	public static Normalize normalize(double amount, String baseCurrency,
			String baseIssuer, String exCurrency, String exIssuer) {
		String link = DATA_URL + "/v2/normalize";
		Map<Object, Object> maps = new HashMap<Object, Object>();
		maps.put("amount", amount);
		if (LSystem.nativeCurrency.equalsIgnoreCase(baseCurrency)) {
			maps.put("currency", LSystem.nativeCurrency.toUpperCase());
		} else {
			maps.put("currency", baseCurrency.toUpperCase());
			maps.put("issuer", baseIssuer);
		}
		maps.put("exchange_currency", exCurrency.toUpperCase());
		maps.put("exchange_issuer", exIssuer.toUpperCase());
		String result = open(link, maps);
		if (result != null) {
			Normalize normalize = new Normalize();
			normalize.copyFrom(new JSONObject(result));
			return normalize;
		}
		return null;
	}

	/**
	 * 以本地默认货币（默认即XRP）获得指定网关货币的汇率
	 * 
	 * @param amount
	 * @param exCurrency
	 * @param exIssuer
	 * @return
	 */
	public static Normalize normalizeNative(double amount, String exCurrency,
			String exIssuer) {
		return normalize(amount, LSystem.nativeCurrency, null, exCurrency,
				exIssuer);
	}

	// rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B
	public static void main(String[] args) {
		System.out.println(RippleDataApi.normalize(100, "BTC",
				"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B", "USD",
				"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"));
	}
}
