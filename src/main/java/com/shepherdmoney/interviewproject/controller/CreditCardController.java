package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.ApplicationUser;
import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.repository.BalanceHistoryRepository;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.temporal.ChronoUnit;


@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    @Autowired
    UserRepository userRepo;
    @Autowired
    CreditCardRepository creditCardRepo;

    @Autowired
    BalanceHistoryRepository balanceRepo;
    @PostMapping("/credit-card")
   public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length
        if (userRepo.existsById(payload.getUserId())){
            //create credit card details
            CreditCard creditCard = new CreditCard();
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setNumber(payload.getCardNumber());
            creditCard.setOwnerId(payload.getUserId());
            creditCardRepo.save(creditCard);

            //add credit card to user
            ApplicationUser user = userRepo.getReferenceById(payload.getUserId());
            ArrayList<Integer> creditCards=user.getCreditCards();
            creditCards.add(creditCard.getId());
            user.setCreditCards(creditCards);
            userRepo.save(user);

            //return value
            int creditCardId=creditCard.getId();
            return ResponseEntity.ok(creditCardId);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null
        ArrayList<CreditCardView> creditCardView = new ArrayList<>();
        if (userRepo.existsById(userId))
        {
            ApplicationUser user = userRepo.getReferenceById(userId);
            ArrayList<Integer> creditCards=user.getCreditCards();

            for (Integer creditCardId : creditCards) {
                CreditCard creditCard=creditCardRepo.getReferenceById(creditCardId);
                CreditCardView viewCard = new CreditCardView(creditCard.getIssuanceBank(),creditCard.getNumber());
                creditCardView.add(viewCard);
            }

            return ResponseEntity.ok(creditCardView);
        }
        else{
            return ResponseEntity.ok(creditCardView);
        }
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
        CreditCard creditCard= creditCardRepo.findByCreditCardNumber(creditCardNumber);
        if(creditCard!=null){
            return ResponseEntity.ok(creditCard.getOwnerId());
        }
        else{
            return ResponseEntity.badRequest().build();
        }

    }
    /*
    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<Object> postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        //TODO: Given a list of transactions, update credit cards' balance history.
        //      1. For the balance history in the credit card
        //      2. If there are gaps between two balance dates, fill the empty date with the balance of the previous date
        //      3. Given the payload `payload`, calculate the balance different between the payload and the actual balance stored in the database
        //      4. If the different is not 0, update all the following budget with the difference
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a balance amount of {date: 4/11, amount: 110}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 100}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.
        for (UpdateBalancePayload updateBalancePayload : payload) {
            System.out.println(updateBalancePayload.getCreditCardNumber() + " " + updateBalancePayload.getBalanceDate() + " " + updateBalancePayload.getBalanceAmount());
            BalanceHistory balance= new BalanceHistory();
            balance.setDate(updateBalancePayload.getBalanceDate());
            balance.setBalance(updateBalancePayload.getBalanceAmount());

            CreditCard creditCard= creditCardRepo.findByCreditCardNumber(updateBalancePayload.getCreditCardNumber());
            if(creditCard==null){
                return ResponseEntity.badRequest().build();
            }
            //return ResponseEntity.ok(creditCard.getOwnerId());
            TreeMap<LocalDate, Object> balanceHistory = creditCard.getBalanceHistory();
            if (balanceHistory.isEmpty()){
                balanceHistory.put(updateBalancePayload.getBalanceDate(),balance);
                balanceRepo.save(balance);
            }
            else{
                //ceiling key
                LocalDate ceilingKey = balanceHistory.ceilingKey(updateBalancePayload.getBalanceDate());

                //floor key
                LocalDate floorKey = balanceHistory.floorKey(updateBalancePayload.getBalanceDate());
                if (floorKey==null){

                    LocalDate startDate = balance.getDate();
                    long daysDifference = ChronoUnit.DAYS.between(startDate,ceilingKey);


                    for (int i = 0; i < daysDifference-1; i++) {
                        LocalDate currentDate = startDate.plusDays(i);
                        BalanceHistory addBalance= new BalanceHistory();
                        addBalance.setDate(currentDate);
                        addBalance.setBalance(balance.getBalance());
                        balanceRepo.save(addBalance);
                        balanceHistory.put(currentDate,addBalance);


                    }

                }
                else if (ceilingKey==null){

                    LocalDate endDate = balance.getDate();
                    long daysDifference = ChronoUnit.DAYS.between(floorKey,endDate);
                    double storeBalance = balanceHistory.get(floorKey).getBalance();


                    for (int i = 1; i < daysDifference; i++) {
                        LocalDate currentDate = floorKey.plusDays(i);
                        BalanceHistory addBalance= new BalanceHistory();
                        addBalance.setDate(currentDate);
                        addBalance.setBalance(storeBalance);
                        balanceRepo.save(addBalance);
                        balanceHistory.put(currentDate,addBalance);

                    }
                    BalanceHistory addBalance= new BalanceHistory();
                    addBalance.setDate(endDate);
                    addBalance.setBalance(balance.getBalance());
                    balanceRepo.save(addBalance);
                    balanceHistory.put(endDate,addBalance);

                }
                else{
                   double currentBalance=balanceHistory.get(balance.getDate()).getBalance();
                   double givenBalance=balance.getBalance();
                   double diff = givenBalance - currentBalance;
                   if (diff!=0){
                       LocalDate endDate = balanceHistory.lastEntry().getKey();
                       long daysDifference = ChronoUnit.DAYS.between(balance.getDate(),endDate);

                       for (int i = 0; i < daysDifference; i++) {

                           LocalDate currentDate = floorKey.plusDays(i);
                           double currentAmt = balanceHistory.get(currentDate).getBalance();
                           balanceHistory.get(currentDate).setBalance(currentAmt+diff);
                           BalanceHistory bal = balanceHistory.get(currentDate);
                           bal.setBalance(currentAmt+diff);

//                           BalanceHistory addBalance= new BalanceHistory();
//                           addBalance.setDate(currentDate);
//                           addBalance.setBalance(storeBalance);

                           balanceRepo.save(bal);

                       }

                   }
                }




            }


        }

        return ResponseEntity.ok().build();
    }*/

}
