<template>
  <main id="main">
    <div id="map"></div>
    <div id="preloader" v-if="searchCompleted"></div>
  </main>
</template>

<script>
import http from "@/api/axios.js";

export default {
  data() {
    return {
      searchCompleted: true,
      points: [],
      places: [],
    };
  },
  created() {
    http
      .get("/paths", {
        params: this.$route.query,
      })
      .then(({ data }) => {
        this.places = data.places;
        this.points = data.points;

        /************
         * 경로 처리
         */
        // 경로를 구성하는 좌표
        let linePath = [];
        for (let pointIdx = 0; pointIdx < this.points.length; pointIdx++) {
          const point = this.points[pointIdx];
          linePath.push(
            new kakao.maps.LatLng(parseFloat(point.y), parseFloat(point.x))
          );
        }
        // 경로 그리기
        var polyLine = new kakao.maps.Polyline({
          path: linePath,
          strokeWeight: 7,
          strokeColor: "#0054FF",
          strokeOpacity: 1,
          strokeStyle: "solid",
        });
        polyLine.setMap(this.map);

        /************
         * 음식점 처리
         */
        // 음식점 마커 표시하기
        for (let placeIdx = 0; placeIdx < this.places.length; placeIdx++) {
          const place = this.places[placeIdx];
          const x = parseFloat(place.x);
          const y = parseFloat(place.y);
          let point = new kakao.maps.LatLng(y, x);
          let marker = new kakao.maps.Marker({ position: point });
          marker.setMap(this.map);

          let iwContent =
            "<div style='padding:5px;'>" + place.placeName + "</div>";
          let infoWindow = new kakao.maps.InfoWindow({
            position: point,
            content: iwContent,
          });
          infoWindow.open(this.map, marker);
        }

        /*************
         * 출발지로 지도 이동
         */
        this.map.panTo(linePath[0]);

        // Preloader 해제
        this.searchCompleted = false;
      })
      .catch((error) => {
        // 에러 출력 후 빈 지도 띄우기
        alert(error);
        this.searchCompleted = false;
      });
  },
  mounted() {
    // 지도 띄우기
    if (window.kakao && window.kakao.maps) {
      this.initMap();
    } else {
      const script = document.createElement("script");
      script.onload = () => kakao.maps.load(this.initMap);
      script.src =
        "//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=" +
        process.env.VUE_APP_JS_API_KEY;
      document.head.appendChild(script);
    }
  },
  methods: {
    // 지도 초기화
    initMap() {
      const container = document.getElementById("map");
      const options = {
        center: new kakao.maps.LatLng(33.450701, 126.570667),
        level: 4,
      };
      this.map = new kakao.maps.Map(container, options);
    },
  },
};
</script>

<style>
#map {
  width: 100%;
  height: 100vh;
}
</style>
