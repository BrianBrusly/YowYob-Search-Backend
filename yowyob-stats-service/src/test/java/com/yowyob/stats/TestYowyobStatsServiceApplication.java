package com.yowyob.stats;

import org.springframework.boot.SpringApplication;

public class TestYowyobStatsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(YowyobStatsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
