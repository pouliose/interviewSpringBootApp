package com.agileactors.fintech.repositories;

import com.agileactors.fintech.domain.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String > {
}
