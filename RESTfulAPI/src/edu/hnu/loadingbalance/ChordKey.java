package edu.hnu.loadingbalance;

public class ChordKey implements Comparable<Object> {

	private String identifier;
	private byte[] key;

	public ChordKey(byte[] key) {
		this.key = key;
	}

	public ChordKey(String identifier) {
		this.identifier = identifier;
		this.key = Hash.hash(identifier);
	}
	
	/**
	 * check if the value is between two hash keys
	 * @param fromKey		the lower  limit
	 * @param toKey		the upper limit
	 * @return		boolean 
	 */
	public boolean isBetween(ChordKey fromKey, ChordKey toKey) {
		if (fromKey.compareTo(toKey) < 0) {
			if (this.compareTo(fromKey) > 0 && this.compareTo(toKey) < 0) {
				return true;
			}
		} else if (fromKey.compareTo(toKey) > 0) {
			byte[] up = {(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff};		//in the test example the length of hash key is 32, if chose SHA-1 to compute hash key, the length of the array should be 20.
			ChordKey upboundry = new ChordKey(up);
			byte[] down = {(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};		//if chose SHA-1 to compute hash key, the length of the array should be 20.
			ChordKey downboundry = new ChordKey(down);
			if (this.compareTo(fromKey) > 0 && this.compareTo(upboundry) < 0) {
				return true;
			}else if(this.compareTo(downboundry) > 0 && this.compareTo(toKey) < 0){
				return true;
			}
		}
		return false;
	}

	public ChordKey createStartKey(int index) {
		byte[] newKey = new byte[key.length];
		System.arraycopy(key, 0, newKey, 0, key.length);
		int carry = 0;
		for (int i = (Hash.KEY_LENGTH - 1) / 8; i >= 0; i--) {
			int value = key[i] & 0xff;
			value += (1 << (index % Hash.NODE_COUNT)) + carry;
			newKey[i] = (byte) value;
			if (value <= 0xff) {
				break;
			}
			carry = (value >> 8) & 0xff;
		}
		return new ChordKey(newKey);
	}

	public int compareTo(Object obj) {
		ChordKey targetKey = (ChordKey) obj;
		for (int i = 0; i < key.length; i++) {
			int loperand = (this.key[i] & 0xff);
			int roperand = (targetKey.getKey()[i] & 0xff);
			if (loperand != roperand) {
				return (loperand - roperand);
			}
		}
		return 0;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < key.length; i++) {
	        String hex = Integer.toHexString(key[i] & 0xFF);
	        if (hex.length() == 1) {
	            hex = '0' + hex;
	        }
	            sb.append(hex.toUpperCase());
	        }
	    return sb.toString();

	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

}

