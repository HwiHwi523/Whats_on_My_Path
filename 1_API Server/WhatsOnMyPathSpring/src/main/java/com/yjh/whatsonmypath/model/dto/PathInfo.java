package com.yjh.whatsonmypath.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PathInfo {
    List<Point> points;
    List<Place> places;
}
