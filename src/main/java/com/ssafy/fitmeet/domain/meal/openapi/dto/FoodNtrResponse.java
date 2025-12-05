package com.ssafy.fitmeet.domain.meal.openapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodNtrResponse {

    @JsonProperty("header")
    private Header header;

    @JsonProperty("body")
    private Body body;

    @Getter
    @Setter
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    @Setter
    public static class Body {
        @JsonProperty("pageNo")
        private int pageNo;

        @JsonProperty("totalCount")
        private int totalCount;

        @JsonProperty("numOfRows")
        private int numOfRows;

        @JsonProperty("items")
        private List<Item> items;
    }

    @Getter
    @Setter
    public static class Item {

        @JsonProperty("NUM")
        private String num;

        @JsonProperty("FOOD_CD")
        private String foodCd;

        @JsonProperty("FOOD_NM_KR")
        private String foodNmKr;

        @JsonProperty("DB_GRP_CM")
        private String dbGrpCm;

        @JsonProperty("DB_GRP_NM")
        private String dbGrpNm;

        @JsonProperty("FOOD_CAT1_CD")
        private String foodCat1Cd;

        @JsonProperty("FOOD_CAT1_NM")
        private String foodCat1Nm;

        @JsonProperty("FOOD_CAT2_CD")
        private String foodCat2Cd;

        @JsonProperty("FOOD_CAT2_NM")
        private String foodCat2Nm;

        @JsonProperty("FOOD_CAT3_CD")
        private String foodCat3Cd;

        @JsonProperty("FOOD_CAT3_NM")
        private String foodCat3Nm;

        @JsonProperty("SERVING_SIZE")
        private String servingSize;

        @JsonProperty("AMT_NUM1") // 열량(kcal)
        private String amtNum1;

        @JsonProperty("AMT_NUM3") // 단백질
        private String amtNum3;

        @JsonProperty("AMT_NUM4") // 지방
        private String amtNum4;

        @JsonProperty("AMT_NUM6") // 탄수화물
        private String amtNum6;

        @JsonProperty("AMT_NUM7") // 당류
        private String amtNum7;

        @JsonProperty("AMT_NUM8") // 식이섬유
        private String amtNum8;

        @JsonProperty("AMT_NUM13") // 나트륨
        private String amtNum13;

        @JsonProperty("UPDATE_DATE")
        private String updateDate;  // "2025-01-23"
    }
}
