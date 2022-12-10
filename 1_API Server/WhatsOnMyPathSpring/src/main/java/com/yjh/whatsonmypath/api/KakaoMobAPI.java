package com.yjh.whatsonmypath.api;

import com.yjh.whatsonmypath.model.dto.Point;
import org.apache.ibatis.jdbc.Null;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KakaoMobAPI {
    private static final KakaoMobAPI instance = new KakaoMobAPI();
    private KakaoMobAPI() {
        // 헤더 선언 및 초기화
        // Request Headers, Entity 는 모든 API 호출에서 공통되므로 한 번만 생성해 사용
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.add("Authorization", "KakaoAK " + API_KEYS.REST_API_KEY);
        // Request 객체 생성 및 초기화
        requestEntity = new HttpEntity<>(headers);
    }
    public static KakaoMobAPI getInstance() { return instance; }

    private static final String URL = "https://apis-navi.kakaomobility.com/v1/directions";

    private static final RestTemplate restTemplate = new RestTemplate();

    private final HttpEntity<Null> requestEntity;  // API 호출 시 보낼 Http Request 객체

    public ResponseEntity<List<Point>> findPath(String origin_x,
                                                String origin_y,
                                                String destination_x,
                                                String destination_y,
                                                String... waypoint_xy) {
        StringBuilder urlBuilder = new StringBuilder(URL);

        // 출발지 설정
        String origin = origin_x + "," + origin_y;  // 출발지 경도 및 위도 문자열 조합
        urlBuilder.append("?origin=").append(origin);

        // 도착지 설정
        String destination = destination_x + "," + destination_y;  // 도착지 경도 및 위도 문자열 조합
        urlBuilder.append("&destination=").append(destination);

        // 경유지 설정
        if (waypoint_xy.length > 0) {  // 경유지가 존재할 경우만
            urlBuilder.append("&waypoints=").append(waypoint_xy[0]).append(",").append(waypoint_xy[1]);
            for (int waypointsIdx = 2; waypointsIdx < waypoint_xy.length; waypointsIdx += 2) {
                urlBuilder.append("|").append(waypoint_xy[waypointsIdx]).append(",").append(waypoint_xy[waypointsIdx + 1]);
            }
        }

        // Query URL 생성
        String queryUrl = urlBuilder.toString();

        // REST API 호출 및 데이터 얻기
        ResponseEntity<String> response = restTemplate.exchange(
                queryUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // Json -> List 변환, 경로를 구성하는 지점
        List<Point> points = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONObject route = jsonObject.getJSONArray("routes").getJSONObject(0);  // 경로

            // 출발지 -> ... 경유지 ... -> 도착지, 각 구간 탐색
            JSONArray sections = route.getJSONArray("sections");
            for (int sectionIdx = 0; sectionIdx < sections.length(); sectionIdx++) {
                // 구간을 구성하는 도로 탐색
                JSONArray roads = sections.getJSONObject(sectionIdx).getJSONArray("roads");
                for (int roadIdx = 0; roadIdx < roads.length(); roadIdx++) {
                    // 도로를 구성하는 각 지점 탐색 및 저장
                    JSONArray vertexes = roads.getJSONObject(roadIdx).getJSONArray("vertexes");
                    for (int vertexIdx = 0; vertexIdx < vertexes.length(); vertexIdx += 2) {
                        points.add(new Point(
                                vertexes.getString(vertexIdx),  // 경도
                                vertexes.getString(vertexIdx + 1)  // 위도
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(points, response.getStatusCode());
    }
}
