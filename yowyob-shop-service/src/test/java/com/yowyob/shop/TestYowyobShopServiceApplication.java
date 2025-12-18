package com.yowyob.shop;

import org.springframework.boot.SpringApplication;

public class TestYowyobShopServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(YowyobShopServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
