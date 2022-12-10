package com.yjh.whatsonmypath.controller;

import com.yjh.whatsonmypath.api.KakaoLocalAPI;
import com.yjh.whatsonmypath.api.KakaoMobAPI;
import com.yjh.whatsonmypath.model.dto.PathInfo;
import com.yjh.whatsonmypath.model.dto.Place;
import com.yjh.whatsonmypath.model.dto.Point;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/paths")
@Slf4j
@ApiImplicitParams({
        @ApiImplicitParam(name = "keyword", value = "검색할 키워드", dataTypeClass = String.class),
        @ApiImplicitParam(name = "origin_x", value = "출발지 경도", dataTypeClass = String.class),
        @ApiImplicitParam(name = "origin_y", value = "출발지 위도", dataTypeClass = String.class),
        @ApiImplicitParam(name = "destination_x", value = "도착지 경도", dataTypeClass = String.class),
        @ApiImplicitParam(name = "destination_y", value = "도착지 위도", dataTypeClass = String.class),
        @ApiImplicitParam(name = "waypoint_xy", value = "경유지 경도, 위도 순서로 저장된 1차원 배열", dataTypeClass = String.class)
})
public class PathController {
    private static final KakaoLocalAPI kakaoLocalAPI = KakaoLocalAPI.getInstance();
    private static final KakaoMobAPI kakaoMobAPI = KakaoMobAPI.getInstance();

    @ApiOperation(value = "경로 & 장소 찾기", notes = "경로와 경로 상에 있는 장소 찾기")
    @GetMapping("")
    public ResponseEntity<?> getPath(@RequestParam String keyword,
                                            @RequestParam String origin_x,
                                            @RequestParam String origin_y,
                                            @RequestParam String destination_x,
                                            @RequestParam String destination_y,
                                            @RequestParam(defaultValue = "") String[] waypoint_xy) {
        if (waypoint_xy.length > 10) {  // 경유지는 5개를 넘을 수 없음
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<List<Point>> result = kakaoMobAPI.findPath(origin_x, origin_y, destination_x, destination_y, waypoint_xy);
        HttpStatus httpStatus = result.getStatusCode();

        // 중복을 줄이기 위해 반경 1 KM 단위로 지점 자르기
        List<Point> points = new ArrayList<>();  // 1 KM 단위로 잘린 지점
        List<Place> places = new ArrayList<>();  // 각 지점에서 사용자가 입력한 키워드로 검색된 장소
        
        if (httpStatus.is2xxSuccessful()) {
            Set<String> searchedId = new HashSet<>();  // 장소 ID를 저장하며 중복 지점 여부 확인
            Point lastPoint = new Point(origin_x, origin_y);  // 키워드를 검색한 마지막 지점, 시작지점으로 설정

            points.add(lastPoint);  // 출발지점 삽입

            List<Point> path = result.getBody();
            if (path == null)
                path = new ArrayList<>();
            for (Point point : path) {
                // 현재 지점 삽입, 웹에서 경로 그리기 위함
                points.add(point);

                // 직전 검색 지점으로부터 거리를 구해 1 KM 이하라면 현재 지점 키워드 검색 건너뛰기, 지점 중복 방지
                double distFromLast = distance(lastPoint, point);
                if (distFromLast <= 1000)  // Unit: Meter
                    continue;

                // 현재 위치에서 반경 1 KM 이내 장소 검색
                ResponseEntity<List<Place>> searchedPlaces = kakaoLocalAPI.searchPlaceByKeyword(
                        keyword,
                        "15",
                        "",
                        point.getX(),
                        point.getY(),
                        "1000"
                );

                // 각 장소 추가
                List<Place> placesOnPath = searchedPlaces.getBody();
                if (placesOnPath == null)
                    placesOnPath = new ArrayList<>();
                for (Place place : placesOnPath) {
                    // 중복되지 않은 장소만 추가
                    if (!searchedId.contains(place.getId())) {
                        searchedId.add(place.getId());  // 중복 확인용 ID Set 추가
                        places.add(place);  // 반환할 장소 목록에 추가
                    }
                }
            }
            points.add(new Point(destination_x, destination_y));  // 도착지점 삽입
        }

        PathInfo pathInfo = new PathInfo(points, places);
        return new ResponseEntity<>(pathInfo, httpStatus);
    }

    // 두 좌표 사이의 거리를 구하는 함수
    private double distance(Point point1, Point point2){
        double lon1 = Double.parseDouble(point1.getX()), lat1 = Double.parseDouble(point1.getY());
        double lon2 = Double.parseDouble(point2.getX()), lat2 = Double.parseDouble(point2.getY());
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;

        return dist; //단위 meter
    }
    // 10진수를 Radian(라디안)으로 변환
    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }
    // Radian(라디안)을 10진수로 변환
    private double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }
}
