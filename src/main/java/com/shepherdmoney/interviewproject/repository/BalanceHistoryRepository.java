package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("BalanceHistoryRepo")
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Integer> {


}
