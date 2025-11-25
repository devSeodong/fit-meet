package com.ssafy.fitmeet.domain.diet.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/diet")
@RequiredArgsConstructor
@Tag(name = "Diet", description = "식단 관련 API ( CRUD )")
@Log4j2
public class DietRestController {
	
}
