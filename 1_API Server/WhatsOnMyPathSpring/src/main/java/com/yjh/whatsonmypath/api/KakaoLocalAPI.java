package com.yjh.whatsonmypath.api;

import com.yjh.whatsonmypath.model.dto.Place;
import org.apache.ibatis.jdbc.Null;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KakaoLocalAPI {
    // Singleton
    private static final KakaoLocalAPI instance = new KakaoLocalAPI();
    private KakaoLocalAPI() {
        // 헤더 선언 및 초기화
        // Request Headers, Entity 는 모든 API 호출에서 공통되므로 한 번만 생성해 사용
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.add("Authorization", "KakaoAK " + API_KEYS.REST_API_KEY);
        // Request 객체 생성 및 초기화
        requestEntity = new HttpEntity<>(headers);
    }
    public static KakaoLocalAPI getInstance() { return instance; }

    // 카카오 로컬 API 정보
    private static final String HOST = "https://dapi.kakao.com";  // 카카오 로컬 API 호스트
    private static final String KEYWORD_PATH = "/v2/local/search/keyword.json";  // 키워드 검색 API 경로

    private static final RestTemplate restTemplate = new RestTemplate();  // API 호출용

    private final HttpEntity<Null> requestEntity;  // API 호출 시 보낼 Http Request 객체

    public ResponseEntity<List<Place>> searchPlaceByKeyword(String keyword,
                                                            String size,
                                                            String category_group_code,
                                                            String x,
                                                            String y,
                                                            String radius) {
        // keyword 파라미터로 Query 설정
        String queryUrl = HOST + KEYWORD_PATH;
        queryUrl += "?query=" + keyword;
        queryUrl += "&size=" + size;
        if (!category_group_code.equals("")) {  // 카테고리 코드 설정
            queryUrl += "&category_group_code=" + category_group_code;
        }
        if (!x.equals("") && !y.equals("") && !radius.equals("")) {  // 중심지역 및 반경 정보 설정
            queryUrl += "&x=" + x;
            queryUrl += "&y=" + y;
            queryUrl += "&radius=" + radius;
        }

        // REST API 호출 및 데이터 얻기
        ResponseEntity<String> response = restTemplate.exchange(
                queryUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // Json -> List 변환
        List<Place> places = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONArray documents = jsonObject.getJSONArray("documents");  // 상가 List 획득

            for (int i = 0; i < documents.length(); i++) {
                JSONObject store = documents.getJSONObject(i);
                places.add(new Place(
                        store.getString("id"),
                        store.getString("place_name"),
                        store.getString("category_name"),
                        store.getString("category_group_code"),
                        store.getString("phone"),
                        store.getString("address_name"),
                        store.getString("road_address_name"),
                        store.getString("place_url"),
                        store.getString("x"),
                        store.getString("y")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(places, response.getStatusCode());
    }
}
