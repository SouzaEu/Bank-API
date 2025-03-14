package br.com.fiap.BankAPI.service;

import br.com.fiap.BankAPI.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContaService {
    public void validateAccount(Account account) {
        if (checkBlankOrNull(account.getNomeTitular()) || checkBlankOrNull(account.getCpf())
                || checkBalanceNullOrZero(account.getSaldo()) || checkType(account.getTipo().toString())
                || checkOpeningDate(account.getDataAbertura())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "erro no corpo da resposta");
        }
    }

    public Account deposit(Account account, BigDecimal value) {
        checkValue(value);
        account.setSaldo(account.getSaldo().add(value));
        return account;
    }

    private void checkValue(BigDecimal value) {
        if (!checkPositiveValue(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor deve ser maior que 0, valor: " + value);
        }
    }

    public Account withdraw(Account account, BigDecimal value) {
        checkValue(value);
        checkBalance(account.getSaldo(), value);
        account.setSaldo(account.getSaldo().subtract(value));
        return account;
    }

    // Percebi que estou repetindo muito o termo 'transfer', isso não é bom, né?
    public Account transfer(Account destinyAccount, Account originAccount, Transfer transfer) {
        checkValue(transfer.getValue());
        checkBalance(originAccount.getSaldo(), transfer.getValue());
        withdraw(originAccount, transfer.getValue());
        return deposit(destinyAccount, transfer.getValue());
    }

    private static void checkBalance(BigDecimal balance, BigDecimal value) {
        if (balance.compareTo(value) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo da conta insuficiente, saldo: " + balance);
        }
    }

    public boolean checkBlankOrNull(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean checkBalanceNullOrZero(BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            return true;
        }
        return false;
    }

    public boolean checkType(String accountType) {
        if (accountType == null || accountType.isEmpty() || AccountType.valueOf(accountType).toString().isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean checkOpeningDate(LocalDate date) {
        if (LocalDate.now().isBefore(date)) {
            return true;
        }
        return false;
    }


    public boolean checkPositiveValue(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }


}
