package org.ripple.power.txns.data;

import java.util.List;

public class AccountLineResult {
    String account;
    int ledger_current_index;
    List<Line> lines;
    boolean validated;
}
