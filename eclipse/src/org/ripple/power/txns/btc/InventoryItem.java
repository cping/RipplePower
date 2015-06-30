/*
 * Copyright 2014 Ronald Hoffman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ripple.power.txns.btc;

import java.io.EOFException;

import org.ripple.power.Helper;

/**
 * InventoryItem represents an inventory item (block or transaction).  Inventory items
 * are used in messages that announce the availability of an item or request an item
 * from a peer.
 *
 * <p>Inventory Item:</p>
 * <pre>
 *   Size       Field               Description
 *   ====       =====               ===========
 *   4 bytes    Type                0=Error, 1=Transaction, 2=Block, 3=Filtered Block
 *  32 bytes    Hash                Object hash
 * </pre>

 */
public class InventoryItem implements ByteSerializable {

    /** Inventory error code */
    public static final int INV_ERROR = 0;

    /** Transaction inventory item */
    public static final int INV_TX = 1;

    /** Block inventory item */
    public static final int INV_BLOCK = 2;

    /** Filtered block inventory item */
    public static final int INV_FILTERED_BLOCK = 3;

    /** Item hash */
    private final Sha256Hash hash;

    /** Item type */
    private final int type;

    /**
     * Create an inventory item
     *
     * @param       type                Inventory item type (INV_BLOCK, INV_FILTERED_BLOCK, INV_TX)
     * @param       hash                Inventory item hash
     */
    public InventoryItem(int type, Sha256Hash hash) {
        this.hash = hash;
        this.type = type;
    }

    /**
     * Create an inventory item
     *
     * @param       inBuffer            Input buffer
     * @throws      EOFException        End-of-data while processing byte stream
     */
    public InventoryItem(SerializedBuffer inBuffer) throws EOFException {
        type = inBuffer.getInt();
        hash = new Sha256Hash(Helper.reverseBytes(inBuffer.getBytes(32)));
    }

    /**
     * Write the serialized object to the output buffer
     *
     * @param       outBuffer           Output buffer
     * @return                          Output buffer
     */
    @Override
    public SerializedBuffer getBytes(SerializedBuffer outBuffer) {
        outBuffer.putInt(type)
                 .putBytes(Helper.reverseBytes(hash.getBytes()));
        return outBuffer;
    }

    /**
     * Return the serialized bytes
     *
     * @return                          Byte array
     */
    @Override
    public byte[] getBytes() {
        return getBytes(new SerializedBuffer(36)).toByteArray();
    }

    /**
     * Return the inventory item type
     *
     * @return                          Inventory item type
     */
    public int getType() {
        return type;
    }

    /**
     * Return the inventory item hash
     *
     * @return                          Inventory item hash
     */
    public Sha256Hash getHash() {
        return hash;
    }

    /**
     * Return the inventory item hash code
     *
     * @return                          Hash code
     */
    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    /**
     * Check if an inventory item equals this item
     *
     * @param       obj                 Object to check
     * @return                          TRUE if the items are equal
     */
    @Override
    public boolean equals(Object obj) {
        return (obj!=null && (obj instanceof InventoryItem) && hash.equals(((InventoryItem)obj).hash) &&
                                        type==((InventoryItem)obj).type);
    }
}
