package com.ssafy.fitmeet.domain.meal.openapi;

import com.ssafy.fitmeet.domain.meal.openapi.dto.FoodNtrResponse;
import com.ssafy.fitmeet.global.config.OpenApiNutriProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FoodOpenApiClientImpl implements FoodOpenApiClient {

    private final OpenApiNutriProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    private String encodeParam(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private FoodNtrResponse callApi(URI uri) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<FoodNtrResponse> response =
                    restTemplate.exchange(uri, HttpMethod.GET, entity, FoodNtrResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("FoodNtr API 응답 오류: status={}", response.getStatusCode());
                return null;
            }

            FoodNtrResponse body = response.getBody();
            if (!"00".equals(body.getHeader().getResultCode())) {
                log.warn("FoodNtr API resultCode != 00, msg={}", body.getHeader().getResultMsg());
                return null;
            }
            return body;
        } catch (Exception e) {
            log.error("FoodNtr API 호출 실패", e);
            return null;
        }
    }

    @Override
    public FoodNtrResponse.Item getByFoodCd(String foodCd) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(properties.getEndPoint())
                .queryParam("serviceKey", properties.getKey())   // 이미 인코딩된 키
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1)
                .queryParam("type", "json")
                .queryParam("FOOD_CD", foodCd)
                .build(true)  // 값이 이미 인코딩된 것으로 취급
                .toUri();

        FoodNtrResponse res = callApi(uri);
        if (res == null || res.getBody() == null || res.getBody().getItems() == null) return null;

        List<FoodNtrResponse.Item> items = res.getBody().getItems();
        return items.isEmpty() ? null : items.get(0);
    }

    @Override
    public FoodNtrResponse.Item searchOne(String foodName, String cat1Name) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(properties.getEndPoint())
                .queryParam("serviceKey", properties.getKey())
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1)
                .queryParam("type", "json");

        String encodedName = encodeParam(foodName);
        String encodedCat1 = encodeParam(cat1Name);

        if (encodedName != null) {
            builder.queryParam("FOOD_NM_KR", encodedName);
        }
        if (encodedCat1 != null) {
            builder.queryParam("FOOD_CAT1_NM", encodedCat1);
        }

        URI uri = builder.build(true).toUri();

        FoodNtrResponse res = callApi(uri);
        if (res == null || res.getBody() == null || res.getBody().getItems() == null) return null;

        List<FoodNtrResponse.Item> items = res.getBody().getItems();
        return items.isEmpty() ? null : items.get(0);
    }

    @Override
    public List<FoodNtrResponse.Item> searchFoods(String foodName, String cat1Name,
                                                  int pageNo, int numOfRows) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(properties.getEndPoint())
                .queryParam("serviceKey", properties.getKey())
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("type", "json");

        String encodedName = encodeParam(foodName);
        String encodedCat1 = encodeParam(cat1Name);

        if (encodedName != null) {
            builder.queryParam("FOOD_NM_KR", encodedName);
        }
        if (encodedCat1 != null) {
            builder.queryParam("FOOD_CAT1_NM", encodedCat1);
        }

        URI uri = builder.build(true).toUri();

        FoodNtrResponse res = callApi(uri);
        if (res == null || res.getBody() == null) return List.of();
        if (res.getBody().getItems() == null) return List.of();

        return res.getBody().getItems();
    }
}
