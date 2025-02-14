package com.springboot.banking_app.controller;


import com.springboot.banking_app.dto.AccountDto;
import com.springboot.banking_app.dto.TransactionDto;
import com.springboot.banking_app.dto.TransferFundDto;
import com.springboot.banking_app.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Add Account REST API
    @PostMapping
    public ResponseEntity<AccountDto> addAccount(@RequestBody AccountDto accountDto) {
        return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.CREATED);
    }

    // Get Account REST API
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id") Long id) {
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    // Deposit REST API
    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable("id") Long id,
                                              @RequestBody Map<String, Double> request) {
        AccountDto accountDto = accountService.getAccountById(id);
        double amount = request.get("amount");
        accountService.deposit(id, amount);
        return ResponseEntity.ok(accountDto);
    }

    // Withdraw REST API
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountDto> withdraw(@PathVariable("id") Long id,
                                               @RequestBody Map<String, Double> request) {
        AccountDto accountDto = accountService.getAccountById(id);
        double amount = request.get("amount");
        accountService.withdraw(id, amount);
        return ResponseEntity.ok(accountDto);
    }

    // Get All Accounts REST API
    @GetMapping("")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // Delete Account REST API
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") Long id) {
        AccountDto accountDto = accountService.getAccountById(id);
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully");
    }

    // Build Transfer REST API
    @PostMapping("/transfer")
    public ResponseEntity<String> transferFund(@RequestBody TransferFundDto transferFundDto) {
        accountService.transferFunds(transferFundDto);
        return ResponseEntity.ok("Transfer Successful!");
    }

    // Build Transactions REST API
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> fetchAccountTransactions(@PathVariable("id") Long accountId){
        List<TransactionDto> accountTransactions = accountService.getAccountTransactions(accountId);
        return ResponseEntity.ok(accountTransactions);
    }
}
