package com.yjh.whatsonmypath.api;

public class KakaoMobAPI {
    private static final KakaoMobAPI instance = new KakaoMobAPI();
    private KakaoMobAPI() {}
    public KakaoMobAPI getInstance() { return instance; }

    private static final String URL = "https://apis-navi.kakaomobility.com/v1/directions";
    private static final String API_KEY = API_KEYS.REST_API_KEY;


}
