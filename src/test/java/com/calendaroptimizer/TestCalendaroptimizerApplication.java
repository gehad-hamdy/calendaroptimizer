package com.calendaroptimizer;

import org.springframework.boot.SpringApplication;

public class TestCalendaroptimizerApplication {

	public static void main(String[] args) {
		SpringApplication.from(CalendaroptimizerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
