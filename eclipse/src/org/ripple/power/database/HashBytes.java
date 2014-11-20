package org.ripple.power.database;

public class HashBytes {
	
	private byte[] bytes;
 
    public HashBytes next;
    public HashBytes value;
    
    public HashBytes(byte[] bytes, HashBytes next) {
        this.bytes = bytes;
        this.next = next;
    }
    public HashBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (byte b : bytes) {
            hash += b;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HashBytes other = (HashBytes) obj;
        byte[] bytes1 = this.bytes;
        byte[] bytes2 = other.bytes;
        if (bytes.length != bytes2.length) {
            return false;
        }
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        String ret = "";
        for (int i = 0; i < bytes.length; i++) {
            ret += bytes[i]+", ";
        }
        return ret;
    }
    
}
