package com.springboot.banking_app.service;

import com.springboot.banking_app.dto.AccountDto;
import com.springboot.banking_app.dto.TransactionDto;
import com.springboot.banking_app.dto.TransferFundDto;
import com.springboot.banking_app.entity.Account;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccountById(Long id);
    AccountDto deposit(Long id, Double amount);
    AccountDto withdraw(Long id, Double amount);
    List<AccountDto> getAllAccounts();
    void deleteAccount(Long id);
    void transferFunds(TransferFundDto transferFundDto);
    List<TransactionDto> getAccountTransactions(Long accountId);
}
