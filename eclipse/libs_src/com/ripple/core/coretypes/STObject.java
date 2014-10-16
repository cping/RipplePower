package com.ripple.core.coretypes;

import com.ripple.core.coretypes.hash.Hash128;
import com.ripple.core.coretypes.hash.Hash160;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.core.coretypes.uint.UInt16;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.coretypes.uint.UInt64;
import com.ripple.core.coretypes.uint.UInt8;
import com.ripple.core.serialized.enums.EngineResult;
import com.ripple.core.serialized.enums.LedgerEntryType;
import com.ripple.core.serialized.enums.TransactionType;
import com.ripple.core.fields.Field;
import com.ripple.core.fields.HasField;
import com.ripple.core.fields.Type;
import com.ripple.core.fields.TypedFields;
import com.ripple.core.formats.Format;
import com.ripple.core.formats.LEFormat;
import com.ripple.core.formats.TxFormat;
import com.ripple.core.serialized.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.TreeMap;

public class STObject implements SerializedType, Iterable<Field> {
    // Internally the fields are stored in a TreeMap
    public static class FieldsMap extends TreeMap<Field, SerializedType> {}
    // There's no nice predicates
    public static interface FieldFilter {
        boolean evaluate(Field a);
    }

    protected FieldsMap fields;
    public Format format;

    public STObject() {
        fields = new FieldsMap();
    }
    public STObject(FieldsMap fieldsMap) {
        fields = fieldsMap;
    }

    public static STObject fromJSON(String offerJson) {
        try {
            return fromJSONObject(new JSONObject(offerJson));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public static STObject fromJSONObject(JSONObject json) {
        return translate.fromJSONObject(json);
    }
    public static STObject fromHex(String hex) {
        return STObject.translate.fromHex(hex);
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.keySet().iterator();
    }

    public String prettyJSON() {
        try {
            return translate.toJSONObject(this).toString(4);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a subclass of STObject using the same fields
     */
    public static STObject formatted(STObject source) {
        return STObjectFormatter.doFormatted(source);

    }

    public Format getFormat() {
        if (format == null) computeFormat();
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    private void computeFormat() {
        UInt16 tt = get(UInt16.TransactionType);
        if (tt != null) {
            setFormat(TxFormat.fromNumber(tt));
        }
        UInt16 let = get(UInt16.LedgerEntryType);
        if (let != null) {
            setFormat(LEFormat.fromNumber(let));
        }
    }

    public FieldsMap getFields() {
        return fields;
    }

    public SerializedType get(Field field) {
        return fields.get(field);
    }

    public static EngineResult engineResult(STObject obj) {
        return (EngineResult) obj.get(Field.TransactionResult);
    }

    static public LedgerEntryType ledgerEntryType(STObject obj) {
        return (LedgerEntryType) obj.get(Field.LedgerEntryType);
    }

    public static TransactionType transactionType(STObject obj) {
        return (TransactionType) obj.get(Field.TransactionType);
    }

    public SerializedType remove(Field f) {
        return fields.remove(f);
    }

    public boolean has(Field f) {
        return fields.containsKey(f);
    }

    public <T extends HasField> boolean has(T hf) {
        return has(hf.getField());
    }

    public void put (TypedFields.UInt8Field f, UInt8 o) {put(f.getField(), o);}
    public void put (TypedFields.Vector256Field f, Vector256 o) {put(f.getField(), o);}
    public void put (TypedFields.VariableLengthField f, VariableLength o) {put(f.getField(), o);}
    public void put (TypedFields.UInt64Field f, UInt64 o) {put(f.getField(), o);}
    public void put (TypedFields.UInt32Field f, UInt32 o) {put(f.getField(), o);}
    public void put (TypedFields.UInt16Field f, UInt16 o) {put(f.getField(), o);}
    public void put (TypedFields.PathSetField f, PathSet o) {put(f.getField(), o);}
    public void put (TypedFields.STObjectField f, STObject o) {put(f.getField(), o);}
    public void put (TypedFields.Hash256Field f, Hash256 o) {put(f.getField(), o);}
    public void put (TypedFields.Hash160Field f, Hash160 o) {put(f.getField(), o);}
    public void put (TypedFields.Hash128Field f, Hash128 o) {put(f.getField(), o);}
    public void put (TypedFields.STArrayField f, STArray o) {put(f.getField(), o);}
    public void put (TypedFields.AmountField f, Amount o) {put(f.getField(), o);}
    public void put (TypedFields.AccountIDField f, AccountID o) {put(f.getField(), o);}

    public <T extends HasField> void putTranslated(T f, Object value) {
        putTranslated(f.getField(), value);
    }

    public void put(Field f, SerializedType value) {
        fields.put(f, value);
    }

    public void putTranslated(Field f, Object value) {
        TypeTranslator typeTranslator = Translators.forField(f);
        SerializedType st = null;
        try {
            st = typeTranslator.fromValue(value);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't put `" +value+ "` into field `" + f + "`\n" + e.toString());
        }
        fields.put(f, st);
    }

    public AccountID get(TypedFields.AccountIDField f) {
        return (AccountID) get(f.getField());
    }

    public Amount get(TypedFields.AmountField f) {
        return (Amount) get(f.getField());
    }

    public STArray get(TypedFields.STArrayField f) {
        return (STArray) get(f.getField());
    }

    public Hash128 get(TypedFields.Hash128Field f) {
        return (Hash128) get(f.getField());
    }

    public Hash160 get(TypedFields.Hash160Field f) {
        return (Hash160) get(f.getField());
    }

    public Hash256 get(TypedFields.Hash256Field f) {
        return (Hash256) get(f.getField());
    }

    public STObject get(TypedFields.STObjectField f) {
        return (STObject) get(f.getField());
    }

    public PathSet get(TypedFields.PathSetField f) {
        return (PathSet) get(f.getField());
    }

    public UInt16 get(TypedFields.UInt16Field f) {
        return (UInt16) get(f.getField());
    }

    public UInt32 get(TypedFields.UInt32Field f) {
        return (UInt32) get(f.getField());
    }

    public UInt64 get(TypedFields.UInt64Field f) {
        return (UInt64) get(f.getField());
    }

    public UInt8 get(TypedFields.UInt8Field f) {
        return (UInt8) get(f.getField());
    }

    public Vector256 get(TypedFields.Vector256Field f) {
        return (Vector256) get(f.getField());
    }

    public VariableLength get(TypedFields.VariableLengthField f) {
        return (VariableLength) get(f.getField());
    }

    // SerializedTypes implementation
    @Override
    public Object toJSON() {
        return translate.toJSON(this);
    }

    public JSONObject toJSONObject() {
        return translate.toJSONObject(this);
    }

    public byte[] toBytes() {
        return translate.toBytes(this);
    }

    @Override
    public String toHex() {
        return translate.toHex(this);
    }

    public void toBytesSink(BytesSink to, FieldFilter p) {
        BinarySerializer serializer = new BinarySerializer(to);

        for (Field field : this) {
            if (p.evaluate(field)) {
                SerializedType value = fields.get(field);
                serializer.add(field, value);
            }
        }
    }
    @Override
    public void toBytesSink(BytesSink to) {
        toBytesSink(to, new FieldFilter() {
            @Override
            public boolean evaluate(Field field) {
                return field.isSerialized();
            }
        });
    }

    public static class Translator extends TypeTranslator<STObject> {

        @Override
        public STObject fromParser(BinaryParser parser, Integer hint) {
            STObject so = new STObject();
            TypeTranslator<SerializedType> tr;
            SerializedType st;
            Field field;
            Integer sizeHint;

            // hint, is how many bytes to parse
            if (hint != null) {
                // end hint
                hint = parser.pos() + hint;
            }

            while (!(parser.end() || hint != null && parser.pos() >= hint)) {
                field = parser.readField();
                if (field == Field.ObjectEndMarker) {
                    break;
                }
                tr = Translators.forField(field);
                sizeHint = field.isVLEncoded() ? parser.readVLLength() : null;
                st = tr.fromParser(parser, sizeHint);
                if (st == null) {
                    throw new IllegalStateException("Parsed " + field + " as null");
                }
                so.put(field, st);
            }

            return STObject.formatted(so);
        }

        @Override
        public Object toJSON(STObject obj) {
            return toJSONObject(obj);
        }

        @Override
        public JSONObject toJSONObject(STObject obj) {
            JSONObject json = new JSONObject();

            for (Field f : obj) {
                try {
                    SerializedType obj1 = obj.get(f);
                    Object object = obj1.toJSON();
                    json.put(f.name(), object);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            return json;
        }

        @Override
        public STObject fromJSONObject(JSONObject jsonObject) {
            STObject so = new STObject();

            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    Object value   = jsonObject.get(key);
                    Field fieldKey = Field.fromString(key);
                    if (fieldKey == null) {
                        continue;
                    }
                    so.putTranslated(fieldKey, value);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }            }
            return STObject.formatted(so);
        }
    }

    public int size() {
        return fields.size();
    }

    static public Translator translate = new Translator();

    public static TypedFields.STObjectField stobjectField(final Field f) {
        return new TypedFields.STObjectField() {@Override public Field getField() {return f; } };
    }

    static public TypedFields.STObjectField TransactionMetaData = stobjectField(Field.TransactionMetaData);
    static public TypedFields.STObjectField CreatedNode = stobjectField(Field.CreatedNode);
    static public TypedFields.STObjectField DeletedNode = stobjectField(Field.DeletedNode);
    static public TypedFields.STObjectField ModifiedNode = stobjectField(Field.ModifiedNode);
    static public TypedFields.STObjectField PreviousFields = stobjectField(Field.PreviousFields);
    static public TypedFields.STObjectField FinalFields = stobjectField(Field.FinalFields);
    static public TypedFields.STObjectField NewFields = stobjectField(Field.NewFields);
    static public TypedFields.STObjectField TemplateEntry = stobjectField(Field.TemplateEntry);

    public static class Translators {
        private static TypeTranslator forType(Type type) {
            switch (type) {

                case STObject:      return translate;
                case Amount:        return Amount.translate;
                case UInt16:        return UInt16.translate;
                case UInt32:        return UInt32.translate;
                case UInt64:        return UInt64.translate;
                case Hash128:       return Hash128.translate;
                case Hash256:       return Hash256.translate;
                case VariableLength:return VariableLength.translate;
                case AccountID:     return AccountID.translate;
                case STArray:       return STArray.translate;
                case UInt8:         return UInt8.translate;
                case Hash160:       return Hash160.translate;
                case PathSet:       return PathSet.translate;
                case Vector256:     return Vector256.translate;

                default:            throw new RuntimeException("Unknown type");
            }
        }

        public static TypeTranslator<SerializedType> forField(Field field) {
            if (field.tag == null) {
                switch (field) {
                    case LedgerEntryType:
                        field.tag = LedgerEntryType.translate;
                        break;
                    case TransactionType:
                        field.tag = TransactionType.translate;
                        break;
                    case TransactionResult:
                        field.tag = EngineResult.translate;
                        break;
                    default:
                        field.tag = forType(field.getType());
                        break;
                }
            }
            return getCastedTag(field);
        }

        @SuppressWarnings("unchecked")
        private static TypeTranslator<SerializedType> getCastedTag(Field field) {
            return (TypeTranslator<SerializedType>) field.tag;
        }
    }
}
