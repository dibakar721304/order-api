package com.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.order.inventory.InventoryService;
import com.order.user.Account;
import com.order.user.AccountRepository;
import com.order.user.ApiKeyHandlerInterceptor;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

	@Autowired
    OrderRepository orderRepository;

    @Autowired
    DataSource dataSource;

    @Bean
    OrderService orderService() { return new OrderService(orderRepository, inventoryService()); }

    @Bean
    InventoryService inventoryService() { return new InventoryService(dataSource); }

    /*
     * A webapp with a request interceptor that verifies API keys.
     */
    public static class WebConfig extends WebMvcConfigurerAdapter {
        @Autowired
        AccountRepository accountRepository;

        @Bean
        ApiKeyHandlerInterceptor apiKeyInterceptor() {
            return new ApiKeyHandlerInterceptor(accountRepository);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(apiKeyInterceptor());
        }
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository) {
        /*
         * Seed the system with a couple of client accounts.
         */
        accountRepository.save(new Account("User1", "Password1"));
        accountRepository.save(new Account("User2", "Password2"));
        return null;
    }

    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
