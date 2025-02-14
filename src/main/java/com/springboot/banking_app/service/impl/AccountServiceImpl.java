package com.springboot.banking_app.service.impl;

import com.springboot.banking_app.dto.AccountDto;
import com.springboot.banking_app.dto.TransactionDto;
import com.springboot.banking_app.dto.TransferFundDto;
import com.springboot.banking_app.entity.Account;
import com.springboot.banking_app.entity.Transaction;
import com.springboot.banking_app.exception.AccountException;
import com.springboot.banking_app.mapper.AccountMapper;
import com.springboot.banking_app.repository.AccountRepository;
import com.springboot.banking_app.repository.TransactionRepository;
import com.springboot.banking_app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account saveAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(saveAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account not found!"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, Double amount) {
        Account userAccount = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account not found!"));
        double totalAmount = userAccount.getBalance() + amount;
        userAccount.setBalance(totalAmount);
        Account savedAccount = accountRepository.save(userAccount);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, Double amount) {
        Account userAccount = accountRepository.findById(id).orElseThrow(() -> new AccountException("Account not found!"));

        if(userAccount.getBalance() - amount < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        double total = userAccount.getBalance() - amount;
        userAccount.setBalance(total);
        Account savedAccount = accountRepository.save(userAccount);

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) ->
                AccountMapper.mapToAccountDto(account))
                .collect(Collectors.toList());

    }

    @Override
    public void deleteAccount(Long id) {
        Account userAccount = accountRepository
                .findById(id).orElseThrow(() ->
                        new AccountException("Account not found!"));
        accountRepository.deleteById(id);
    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {
        // Retrieve the account from which we send the amount
        Account fromAccount = accountRepository.findById(transferFundDto.fromAccountId())
                .orElseThrow(() -> new AccountException("Account not found!"));

        Account toAccount = accountRepository.findById(transferFundDto.toAccountId())
                .orElseThrow(() -> new AccountException("Account does not exist"));

        if(fromAccount.getBalance() - transferFundDto.amount() < 0) {
            throw new RuntimeException("Insufficient balance!");
        }
        // Debit the amount from fromAcount object
        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());

        // Credit the amount to account object
        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setAccountId(transferFundDto.fromAccountId());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId);

        if(transactions.isEmpty()) {
            throw new AccountException("Zero Transactions Found!");
        }

        return transactions.stream()
                .map((transaction) -> convertEntityToDto(transaction))
                .collect(Collectors.toList());
    }

    private TransactionDto convertEntityToDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp()
        );
    }
}
