package org.address.ripple;

public class RippleSchemas {
	
	public enum PrimitiveTypes {
		UINT16(1),
		UINT32(2),
		UINT64(3),
		HASH128(4),
		HASH256(5),
		AMOUNT(6),
		VARIABLE_LENGTH(7),
		ACCOUNT(8),
		OBJECT(14),
		ARRAY(15),
		UINT8(16),
		HASH160(17),
		PATHSET(18),
		VECTOR256(19);

		public int typeCode;
		PrimitiveTypes(int typeCode){
			this.typeCode = typeCode;
		}

		static int MAXBYTEVALUE=0;
		static final PrimitiveTypes reverseLookup[];
		static {
			for(PrimitiveTypes type : values()){
				MAXBYTEVALUE=Math.max(MAXBYTEVALUE, type.typeCode);
			}
			MAXBYTEVALUE++;
			reverseLookup = new PrimitiveTypes[MAXBYTEVALUE];
			for(PrimitiveTypes type : values()){
				reverseLookup[type.typeCode] = type;
			}
		}
		public static PrimitiveTypes fromByteValue(int type) {
			if(type<0 || type>=MAXBYTEVALUE){
				return null;
			}
			return reverseLookup[type];
		}
	};

	public enum BinaryFormatField {
		CloseResolution(PrimitiveTypes.UINT8, 1),
		TemplateEntryType(PrimitiveTypes.UINT8, 2),
		TransactionResult(PrimitiveTypes.UINT8, 3),

		LedgerEntryType(PrimitiveTypes.UINT16, 1),
		TransactionType(PrimitiveTypes.UINT16, 2),

		Flags(PrimitiveTypes.UINT32, 2),
		SourceTag(PrimitiveTypes.UINT32, 3),
		Sequence(PrimitiveTypes.UINT32, 4),
		PreviousTxnLgrSeq(PrimitiveTypes.UINT32, 5),
		LedgerSequence(PrimitiveTypes.UINT32, 6),
		CloseTime(PrimitiveTypes.UINT32, 7),
		ParentCloseTime(PrimitiveTypes.UINT32, 8),
		SigningTime(PrimitiveTypes.UINT32, 9),
		Expiration(PrimitiveTypes.UINT32, 10),
		TransferRate(PrimitiveTypes.UINT32, 11),
		WalletSize(PrimitiveTypes.UINT32, 12),
		OwnerCount(PrimitiveTypes.UINT32, 13),
		DestinationTag(PrimitiveTypes.UINT32, 14),

		HighQualityIn(PrimitiveTypes.UINT32, 16),
		HighQualityOut(PrimitiveTypes.UINT32, 17),
		LowQualityIn(PrimitiveTypes.UINT32, 18),
		LowQualityOut(PrimitiveTypes.UINT32, 19),
		QualityIn(PrimitiveTypes.UINT32, 20),
		QualityOut(PrimitiveTypes.UINT32, 21),
		StampEscrow(PrimitiveTypes.UINT32, 22),
		BondAmount(PrimitiveTypes.UINT32, 23),
		LoadFee(PrimitiveTypes.UINT32, 24),
		OfferSequence(PrimitiveTypes.UINT32, 25),
		FirstLedgerSequence(PrimitiveTypes.UINT32, 26),
		LastLedgerSequence(PrimitiveTypes.UINT32, 27),
		TransactionIndex(PrimitiveTypes.UINT32, 28),
		OperationLimit(PrimitiveTypes.UINT32, 29),
		ReferenceFeeUnits(PrimitiveTypes.UINT32, 30),
		ReserveBase(PrimitiveTypes.UINT32, 31),
		ReserveIncrement(PrimitiveTypes.UINT32, 32),
		SetFlag(PrimitiveTypes.UINT32, 33),
		ClearFlag(PrimitiveTypes.UINT32, 34),
		
		IndexNext(PrimitiveTypes.UINT64, 1),
		IndexPrevious(PrimitiveTypes.UINT64, 2),
		BookNode(PrimitiveTypes.UINT64, 3),
		OwnerNode(PrimitiveTypes.UINT64, 4),
		BaseFee(PrimitiveTypes.UINT64, 5),
		ExchangeRate(PrimitiveTypes.UINT64, 6),
		LowNode(PrimitiveTypes.UINT64, 7),
		HighNode(PrimitiveTypes.UINT64, 8),
		
		EmailHash(PrimitiveTypes.HASH128, 1),
		
		LedgerHash(PrimitiveTypes.HASH256, 1),
		ParentHash(PrimitiveTypes.HASH256, 2),
		TransactionHash(PrimitiveTypes.HASH256, 3),
		AccountHash(PrimitiveTypes.HASH256, 4),
		PreviousTxnID(PrimitiveTypes.HASH256, 5),
		LedgerIndex(PrimitiveTypes.HASH256, 6),
		WalletLocator(PrimitiveTypes.HASH256, 7),
		RootIndex(PrimitiveTypes.HASH256, 8),
		BookDirectory(PrimitiveTypes.HASH256, 16),
		InvoiceID(PrimitiveTypes.HASH256, 17),
		Nickname(PrimitiveTypes.HASH256, 18),
		Feature(PrimitiveTypes.HASH256, 19),
		
		TakerPaysCurrency(PrimitiveTypes.HASH160, 1),
		TakerPaysIssuer(PrimitiveTypes.HASH160, 2),
		TakerGetsCurrency(PrimitiveTypes.HASH160, 3),
		TakerGetsIssuer(PrimitiveTypes.HASH160, 4),
		
		Amount(PrimitiveTypes.AMOUNT, 1),
		Balance(PrimitiveTypes.AMOUNT, 2),
		LimitAmount(PrimitiveTypes.AMOUNT, 3),
		TakerPays(PrimitiveTypes.AMOUNT, 4),
		TakerGets(PrimitiveTypes.AMOUNT, 5),
		LowLimit(PrimitiveTypes.AMOUNT, 6),
		HighLimit(PrimitiveTypes.AMOUNT, 7),
		Fee(PrimitiveTypes.AMOUNT, 8),
		SendMax(PrimitiveTypes.AMOUNT, 9),
		MinimumOffer(PrimitiveTypes.AMOUNT, 16),
		RippleEscrow(PrimitiveTypes.AMOUNT, 17),

		PublicKey(PrimitiveTypes.VARIABLE_LENGTH, 1),
		MessageKey(PrimitiveTypes.VARIABLE_LENGTH, 2),
		SigningPubKey(PrimitiveTypes.VARIABLE_LENGTH, 3),
		TxnSignature(PrimitiveTypes.VARIABLE_LENGTH, 4),
		Generator(PrimitiveTypes.VARIABLE_LENGTH, 5),
		Signature(PrimitiveTypes.VARIABLE_LENGTH, 6),
		Domain(PrimitiveTypes.VARIABLE_LENGTH, 7),
		FundCode(PrimitiveTypes.VARIABLE_LENGTH, 8),
		RemoveCode(PrimitiveTypes.VARIABLE_LENGTH, 9),
		ExpireCode(PrimitiveTypes.VARIABLE_LENGTH, 10),
		CreateCode(PrimitiveTypes.VARIABLE_LENGTH, 11),

		Account(PrimitiveTypes.ACCOUNT, 1),
		Owner(PrimitiveTypes.ACCOUNT, 2),
		Destination(PrimitiveTypes.ACCOUNT, 3),
		Issuer(PrimitiveTypes.ACCOUNT, 4),
		Target(PrimitiveTypes.ACCOUNT, 7),
		RegularKey(PrimitiveTypes.ACCOUNT, 8),

		Paths(PrimitiveTypes.PATHSET, 1),

		Indexes(PrimitiveTypes.VECTOR256, 1),
		Hashes(PrimitiveTypes.VECTOR256, 2),
		Features(PrimitiveTypes.VECTOR256, 3),

		TransactionMetaData(PrimitiveTypes.OBJECT, 2),
		CreatedNode(PrimitiveTypes.OBJECT, 3),
		DeletedNode(PrimitiveTypes.OBJECT, 4),
		ModifiedNode(PrimitiveTypes.OBJECT, 5),
		PreviousFields(PrimitiveTypes.OBJECT, 6),
		FinalFields(PrimitiveTypes.OBJECT, 7),
		NewFields(PrimitiveTypes.OBJECT, 8),
		TemplateEntry(PrimitiveTypes.OBJECT, 9),

		SigningAccounts(PrimitiveTypes.ARRAY, 2),
		TxnSignatures(PrimitiveTypes.ARRAY, 3),
		Signatures(PrimitiveTypes.ARRAY, 4),
		Template(PrimitiveTypes.ARRAY, 5),
		Necessary(PrimitiveTypes.ARRAY, 6),
		Sufficient(PrimitiveTypes.ARRAY, 7),
		AffectedNodes(PrimitiveTypes.ARRAY, 8);

		PrimitiveTypes primitive;
		int fieldId;
		BinaryFormatField(PrimitiveTypes primitve, int fieldValue){
			this.primitive = primitve;
			this.fieldId = fieldValue;
		}
		static int MAXBYTEVALUE=0;
		static final BinaryFormatField[][] typeFieldLookup;
		static {
			for(BinaryFormatField f : values()){
				MAXBYTEVALUE = Math.max(MAXBYTEVALUE, f.fieldId);
			}
			MAXBYTEVALUE++;
			typeFieldLookup = new BinaryFormatField[PrimitiveTypes.MAXBYTEVALUE][BinaryFormatField.MAXBYTEVALUE];
			
			for(BinaryFormatField f : values()){
				typeFieldLookup[f.primitive.typeCode][f.fieldId] = f;
			}
		}
		public static BinaryFormatField lookup(int type, int fieldType) {
			BinaryFormatField fieldToReturn=null;
			if(type<0 || type>=PrimitiveTypes.MAXBYTEVALUE){
				fieldToReturn=null;
			}
			else if(fieldType<0 || fieldType>=BinaryFormatField.MAXBYTEVALUE){
				fieldToReturn=null;
			}
			else {
				fieldToReturn = typeFieldLookup[type][fieldType];
			}
			if(fieldToReturn==null){
				throw new RuntimeException("Could not find type "+type+", field "+fieldType);
			}
			return fieldToReturn;
		}
	}

	public enum TransactionTypes {
		PAYMENT(0),
		CLAIM(1),
		WALLET_ADD(2),
		ACCOUNT_SET(3),
		PASSWORD_FUND(4),
		REGULAR_KEY_SET(5),
		NICKNAME_SET(6),
		OFFER_CREATE(7),
		OFFER_CANCEL(8),
		CONTRACT(9),
		CONTRACT_REMOVE(10),
		TRUST_SET(20),
		FEATURE(100),
		FEE(101);
		
		static int MAXBYTEVALUE=0;
		static TransactionTypes reverseLookup[];
		static {
			for(TransactionTypes type : values()){
				MAXBYTEVALUE = Math.max(MAXBYTEVALUE, type.byteValue);
			}
			MAXBYTEVALUE++;
			reverseLookup = new TransactionTypes[MAXBYTEVALUE];
			for(TransactionTypes type : values()){
				reverseLookup[type.byteValue] = type;
			}
		}
		
		public byte byteValue;
		
		TransactionTypes(int txTypeByteValue){
			this.byteValue = (byte) txTypeByteValue;
		}
		
		public static TransactionTypes fromType(int txType) {
			if(txType<0 || txType>=MAXBYTEVALUE){
				return null;
			}
			return reverseLookup[txType];
		}
	};
}
