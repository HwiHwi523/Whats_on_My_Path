package com.yjh.whatsonmypath.controller;

import com.yjh.whatsonmypath.api.KakaoLocalAPI;
import com.yjh.whatsonmypath.model.dto.Place;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/places")
@Slf4j
public class PlaceController {
    KakaoLocalAPI kakaoLocalAPI = KakaoLocalAPI.getInstance();

    @ApiOperation(value = "장소 검색", notes = "사용자가 입력한 키워드와 관련된 장소 검색하기")
    @GetMapping("")
    private ResponseEntity<List<Place>> getPlaces(@RequestParam String keyword,
                                                  @RequestParam(defaultValue = "") String x,
                                                  @RequestParam(defaultValue = "") String y,
                                                  @RequestParam(defaultValue = "") String radius) {
        Map<String, Object> apiResponse = kakaoLocalAPI.searchPlaceByKeyword(keyword, x, y, radius);
        return new ResponseEntity<>((List<Place>) apiResponse.get("Data"), (HttpStatus) apiResponse.get("StatusCode"));
    }
}
