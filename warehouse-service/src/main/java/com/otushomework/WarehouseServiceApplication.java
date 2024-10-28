package com.otushomework;

import com.otushomework.itemsservice.entity.Product;
import com.otushomework.itemsservice.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;


@SpringBootApplication
public class WarehouseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataInitializer(WarehouseRepository warehouseRepository) {
        return args -> {
            if (warehouseRepository.count() == 0) {
                Random random = new Random();

                for (int i = 0; i < 10; i++) {
                    Product product = new Product();
                    product.setId(UUID.randomUUID().toString());
                    product.setName("Product " + (i + 1));
                    product.setDescription("Description for product " + (i + 1));

                    // Генерируем цену от 10.0 до 100.0 и округляем до двух знаков после запятой
                    double randomPrice = 10.0 + random.nextDouble() * 90.0;
                    double roundedPrice = new BigDecimal(randomPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    product.setPrice(roundedPrice);

                    product.setAvailableQuantity(random.nextInt(50) + 1); // Количество от 1 до 50

                    warehouseRepository.save(product);
                }

                System.out.println("База данных заполнена случайными товарами.");
            } else {
                System.out.println("База данных уже содержит данные, пропускаем инициализацию.");
            }
        };
    }
}

