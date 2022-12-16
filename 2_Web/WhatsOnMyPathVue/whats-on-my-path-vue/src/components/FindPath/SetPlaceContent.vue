<template>
  <!-- ======= Hero Section ======= -->
  <section id="hero" class="street d-flex align-items-center">
    <div
      class="container d-flex flex-column align-items-center"
      data-aos="zoom-in"
      data-aos-delay="100"
    >
      <h1 class="hangeul">어디로 가시나요?</h1>

      <div class="container" style="width: 75%">
        <div class="wrap-input1">
          <input
            class="input1"
            type="text"
            name="departure"
            v-model="departureName"
            placeholder="출발지"
            @click="clickInputForm"
            readonly
          />
          <span class="shadow-input1"></span>
          <div class="list-group mt-2" v-if="isDepartureOn">
            <div class="input-group">
              <input
                type="text"
                class="form-control hangeul-air"
                placeholder="장소 입력"
                v-model="departureKeyword"
                name="searchDeparture"
                @keyup.enter="searchPlaces"
              />
              <span
                class="btn input-group-text hangeul-air"
                style="background-color: #34b7a7; color: white"
                name="searchDeparture"
                @click="searchPlaces"
                >검색
              </span>
            </div>
            <span class="list-group-item mt-1">
              <small class="d-flex hangeul-air">검색결과</small>
            </span>
            <span
              class="list-group-item list-group-item-action"
              v-if="departurePlaces.length == 0"
            >
              <small></small>
            </span>
            <span
              v-for="(place, index) in departurePlaces"
              :key="index"
              class="list-group-item list-group-item-action"
              @click="selectPlace(place, 'departure')"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1 hangeul">{{ place.placeName }}</h5>
              </div>
              <p
                class="mb-1 d-flex hangeul-air"
                v-if="place.roadAddressName != ''"
              >
                {{ place.roadAddressName }}
              </p>
              <p class="mb-1 d-flex hangeul-air" v-else>
                {{ place.addressName }}
              </p>
              <small class="d-flex hangeul-air">
                {{ place.categoryName }}
              </small>
            </span>
          </div>
        </div>

        <div class="wrap-input1">
          <input
            class="input1"
            type="text"
            name="arrival"
            v-model="arrivalName"
            placeholder="도착지"
            @click="clickInputForm"
            readonly
          />
          <span class="shadow-input1"></span>
          <div class="list-group mt-2" v-if="isArrivalOn">
            <div class="input-group">
              <input
                type="text"
                class="form-control hangeul-air"
                placeholder="장소 입력"
                v-model="arrivalKeyword"
                name="searchArrival"
                @keyup.enter="searchPlaces"
              />
              <span
                class="btn input-group-text hangeul-air"
                style="background-color: #34b7a7; color: white"
                name="searchArrival"
                @click="searchPlaces"
                >검색
              </span>
            </div>
            <span class="list-group-item mt-1">
              <small class="d-flex hangeul-air">검색결과</small>
            </span>
            <span
              class="list-group-item list-group-item-action"
              v-if="arrivalPlaces.length == 0"
            >
              <small></small>
            </span>
            <span
              v-for="(place, index) in arrivalPlaces"
              :key="index"
              class="list-group-item list-group-item-action"
              @click="selectPlace(place, 'arrival')"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1 hangeul">{{ place.placeName }}</h5>
              </div>
              <p
                class="mb-1 d-flex hangeul-air"
                v-if="place.roadAddressName !== ''"
              >
                {{ place.roadAddressName }}
              </p>
              <p class="mb-1 d-flex hangeul-air" v-else>
                {{ place.addressName }}
              </p>
              <small class="d-flex hangeul-air">
                {{ place.categoryName }}
              </small>
            </span>
          </div>
        </div>

        <div class="wrap-input1">
          <input
            class="input1"
            type="text"
            name="keyword"
            v-model="keyword"
            placeholder="키워드 (e.g. 국밥)"
            @keyup.enter="search"
          />
          <span class="shadow-input1"></span>
        </div>
      </div>

      <a class="btn-about" @click="search">
        <span class="hangeul-air">검색</span>
      </a>
    </div>
  </section>
  <!-- End Hero -->
</template>

<script>
import http from "@/api/axios.js";

export default {
  data() {
    return {
      isDepartureOn: false, // 출발지 검색창 On / Off
      departure: "", // 출발지 장소 정보, Json
      departureName: "", // 출발지 장소명
      departureKeyword: "", // 출발지 검색 키워드
      departurePlaces: [], // 출발지 검색 결과, Json[]
      isArrivalOn: false, // 도착지 검색창 On / Off
      arrival: "", // 도착지 장소 정보, Json
      arrivalName: "", // 도착지 장소명
      arrivalKeyword: "", // 도착지 검색 키워드
      arrivalPlaces: [], // 도착지 검색 결과, Json[]
      keyword: "", // 음식 키워드
    };
  },
  methods: {
    // 출발지 or 도착지 폼 클릭 시 장소 검색창 띄우기
    clickInputForm(event) {
      if (event.target.name == "departure") {
        this.departureKeyword = "";
        this.departurePlaces = [];
        this.isDepartureOn = !this.isDepartureOn;
      } else {
        this.arrivalKeyword = "";
        this.arrivalPlaces = [];
        this.isArrivalOn = !this.isArrivalOn;
      }
    },
    // 출발지 or 도착지에서 입력한 키워드로 장소 검색
    searchPlaces(event) {
      // 출발지 or 도착지 중 어느 곳에서 호출했는지 확인
      const whereToCall = event.target.getAttribute("name");

      // 출발지 or 도착지 키워드 가져오기
      let placeKeyword =
        whereToCall == "searchDeparture"
          ? this.departureKeyword
          : this.arrivalKeyword;

      // API Server 데이터 가져오기
      http
        .get("/places", { params: { keyword: placeKeyword, size: 5 } })
        .then(({ data }) => {
          // 출발지 or 도착지 검색 결과 반영
          if (whereToCall == "searchDeparture") {
            this.departurePlaces = data;
          } else {
            this.arrivalPlaces = data;
          }
        });
    },
    // 검색된 장소 선택 시 장소명 삽입
    selectPlace(place, whereToCall) {
      if (whereToCall == "departure") {
        this.departure = place;
        this.departureName = "출발지 : " + place.placeName;
        this.departureKeyword = "";
        this.departurePlaces = [];
        this.isDepartureOn = false;
      } else {
        this.arrival = place;
        this.arrivalName = "도착지 : " + place.placeName;
        this.arrivalKeyword = "";
        this.arrivalPlaces = [];
        this.isArrivalOn = false;
      }
    },
    // 입력된 출발지, 도착지, 키워드로 경로와 음식점 검색
    search() {
      if (!this.departure || this.departure == "") {
        alert("출발지를 설정해주세요.");
        return;
      }
      if (!this.arrival || this.arrival == "") {
        alert("도착지를 설정해주세요.");
        return;
      }
      if (!this.keyword || this.keyword == "") {
        alert("키워드를 입력해주세요.");
        return;
      }

      // 음식 키워드, 출발지, 도착지 정보로 쿼리 넘겨주기
      this.searchCompleted = true;
      let query = {
        keyword: this.keyword,
        origin_x: this.departure.x,
        origin_y: this.departure.y,
        destination_x: this.arrival.x,
        destination_y: this.arrival.y,
      };
      this.$router.push({ name: "ResultPath", query: query });
    },
  },
};
</script>
