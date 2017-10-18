package org.ripple.power.txns.btc;

import java.math.BigInteger;

public class BlockTransaction {

	protected Sha256Hash normID;

	protected Sha256Hash txHash;

	protected long txTime;

	protected Sha256Hash blockHash;

	protected Address address;

	protected BigInteger value;

	public BlockTransaction(Sha256Hash normID, Sha256Hash txHash, long txTime, Sha256Hash blockHash, Address address,
			BigInteger value) {
		this.normID = normID;
		this.txHash = txHash;
		this.txTime = txTime;
		this.blockHash = blockHash;
		this.address = address;
		this.value = value;
	}

	public Sha256Hash getNormalizedID() {
		return normID;
	}

	public Sha256Hash getTxHash() {
		return txHash;
	}

	public long getTxTime() {
		return txTime;
	}

	public Sha256Hash getBlockHash() {
		return blockHash;
	}

	public Address getAddress() {
		return address;
	}

	public BigInteger getValue() {
		return value;
	}
}
