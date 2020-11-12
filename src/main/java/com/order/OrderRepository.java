package com.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.user.Account;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order save(Order order);

    List<Order> findByAccount(Account account);

    Order findByAccountAndId(Account account, long id);

    List<Order> findByAccountAndStatus(Account account, String status);
}

