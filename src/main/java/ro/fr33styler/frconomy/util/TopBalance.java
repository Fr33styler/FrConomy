package ro.fr33styler.frconomy.util;

import ro.fr33styler.frconomy.account.Account;

import java.math.BigDecimal;
import java.util.List;

public class TopBalance {

    private final List<Account> top;
    private final BigDecimal totalBalance;

    public TopBalance(List<Account> top, BigDecimal totalBalance) {
        this.top = top;
        this.totalBalance = totalBalance;
    }

    public List<Account> getTop() {
        return top;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

}
