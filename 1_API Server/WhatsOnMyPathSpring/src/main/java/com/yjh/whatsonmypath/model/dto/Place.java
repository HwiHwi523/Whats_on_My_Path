package com.yjh.whatsonmypath.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Place {
    String id;
    String placeName;
    String categoryName;
    String categoryGroupCode;
    String phone;
    String addressName;
    String roadAddressName;
    String placeUrl;
    String x;
    String y;
}
