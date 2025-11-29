package com.yowyob.yowyob_stats_service;

import org.springframework.boot.SpringApplication;

public class TestYowyobStatsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(YowyobStatsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
