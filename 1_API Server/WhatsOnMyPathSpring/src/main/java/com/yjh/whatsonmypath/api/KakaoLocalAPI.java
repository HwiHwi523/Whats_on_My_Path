package com.yjh.whatsonmypath.api;

import com.yjh.whatsonmypath.model.dto.Place;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KakaoLocalAPI {
    // Singleton
    private static final KakaoLocalAPI instance = new KakaoLocalAPI();
    private KakaoLocalAPI() {}
    public static KakaoLocalAPI getInstance() { return instance; }

    private static final String HOST = "https://dapi.kakao.com";  // 카카오 로컬 API 호스트
    private static final String KEYWORD_PATH = "/v2/local/search/keyword.json";  // 키워드 검색 API 경로

    private static final RestTemplate restTemplate = new RestTemplate();  // API 호출용
    private static final String API_KEY = API_KEYS.REST_API_KEY;  // Kakao API Key

    public HashMap<String, Object> searchPlaceByKeyword(String keyword, String x, String y, String radius) {
        // Request Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.add("Authorization", "KakaoAK " + API_KEY);

        // keyword 파라미터로 Query 설정
        String queryURL = HOST + KEYWORD_PATH;
        queryURL += "?query=" + keyword;
        if (!x.equals("") && !y.equals("") && !radius.equals("")) {  // 중심지역 및 반경 정보가 주어질 시 설정
            queryURL += "&x=" + x;
            queryURL += "&y=" + y;
            queryURL += "&radius=" + radius;
        }

        // REST API 호출 및 데이터 얻기
        ResponseEntity<String> response = restTemplate.exchange(
                queryURL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        // 결과 Object 변환 및 조합
        HashMap<String, Object> result = new HashMap<>();
        result.put("StatusCode", response.getStatusCode());  // 상태코드

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
        result.put("Data", places);

        return result;
    }
}
