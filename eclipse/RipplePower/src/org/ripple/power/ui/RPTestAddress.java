package org.ripple.power.ui;

import java.util.ArrayList;

/** 官方提供的一些测试用地址与私钥,理论上讲里面是没钱，或者仅有少量货币的的…… **/
public class RPTestAddress {

	ArrayList<FreeAddressItem> list = new ArrayList<FreeAddressItem>();

	public class FreeAddressItem {

		String freeAddress;
		String freeSecret;

		public FreeAddressItem(String adress, String secret) {
			this.freeAddress = adress;
			this.freeSecret = secret;
		}
	}

	public RPTestAddress() {
		list.add(new FreeAddressItem("raqFu9wswvHYS4q5hZqZxVSYei73DQnKL8",
				"shUzHiYxoXX2FgA54j42cXCZ9dTVT"));
		list.add(new FreeAddressItem("rJZdUusLDtY9NEsGea7ijqhVrXv98rYBYN",
				"sEd7rBGm5kxzauRTAV2hbsNz7N45X91"));
		list.add(new FreeAddressItem("rN1zVC4KQsFJbQ2WANBB79gGippD3V5Cez",
				"sndtXVKYciroTtw7ebh9gc1r1Wx37"));
		list.add(new FreeAddressItem("rPPJo7vmmRhu9Um3hzBh623GZD8uYrmBfN",
				"snQcauWoti84Ju4zheFSr1h62gJe3"));
		list.add(new FreeAddressItem("rKCNQ3MxG7tPfhi8adiXxHtu6z31a497DB",
				"sstipxsmMokg3rvb345SF183fy7Xn"));
		list.add(new FreeAddressItem("r4spegDYKta7KFb5XTNfEjyauEhFFb7ymw",
				"shSfJ4oBkus6QnWyZWt2g33Vm57zN"));
	}

}
