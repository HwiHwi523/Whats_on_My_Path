package com.yjh.whatsonmypath.controller;

import com.yjh.whatsonmypath.api.KakaoLocalAPI;
import com.yjh.whatsonmypath.model.dto.Place;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/places")
@Slf4j
public class PlaceController {
    KakaoLocalAPI kakaoLocalAPI = KakaoLocalAPI.getInstance();

    @ApiOperation(value = "장소 검색", notes = "키워드와 관련된 장소 검색하기")
    @GetMapping("")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "검색할 키워드", dataTypeClass = String.class),
            @ApiImplicitParam(name = "size", value = "가져올 데이터 개수", dataTypeClass = String.class),
            @ApiImplicitParam(name = "category_group_code", value = "카테고리 그룹 코드", dataTypeClass = String.class),
            @ApiImplicitParam(name = "x", value = "경도", dataTypeClass = String.class),
            @ApiImplicitParam(name = "y", value = "위도", dataTypeClass = String.class),
            @ApiImplicitParam(name = "radius", value = "반경", dataTypeClass = String.class)
    })
    private ResponseEntity<List<Place>> getPlaces(@RequestParam String keyword,
                                                  @RequestParam(defaultValue = "10") String size,
                                                  @RequestParam(defaultValue = "") String category_group_code,
                                                  @RequestParam(defaultValue = "") String x,
                                                  @RequestParam(defaultValue = "") String y,
                                                  @RequestParam(defaultValue = "") String radius) {
        ResponseEntity<List<Place>> apiResponse = kakaoLocalAPI.searchPlaceByKeyword(keyword, size, category_group_code, x, y, radius);
        return new ResponseEntity<>(apiResponse.getBody(), apiResponse.getStatusCode());
    }
}
