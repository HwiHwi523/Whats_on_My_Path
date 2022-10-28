/*******************************************************************************
 * 선택자 관련 함수 *
 ******************************************************************************/
/*
 * 출발지 검색 Input을 누르면 장소 검색 Form 띄우기
 */
let departureInput = document.getElementById('departure');
departureInput.addEventListener('click', function() {
	// 기존 검색 결과 초기화
	document.getElementById('location-list').innerHTML = "";
	
	let inputLocation = document.getElementById('input-location');
	inputLocation.innerHTML = `
	<div class="input-group">
		<i class="input-group-text fa fa-search" aria-hidden="true"></i>
		<input type="text" id="location-query" class="form-control" placeholder="출발지 입력">
		<span type="button" class="input-group-text" onclick="searchLocBtn('departure')">검색</span>
	</div>`;
});

/*
 * 도착지 검색 Input을 누르면 장소 검색 Form 띄우기
 */
let destinationInput = document.getElementById('destination');
destinationInput.addEventListener('click', function() {
	// 기존 검색 결과 초기화
	document.getElementById('location-list').innerHTML = "";
	
	let inputLocation = document.getElementById('input-location');
	inputLocation.innerHTML = `
	<div class="input-group">
		<i class="input-group-text fa fa-search" aria-hidden="true"></i>
		<input type="text" id="location-query" class="form-control" placeholder="도착지 입력">
		<span type="button" class="input-group-text" onclick="searchLocBtn('destination')">검색</span>
	</div>`;
});

/*
 * 경로 검색 버튼 눌렀을 때 데이터를 가져 와 searchPathData에 저장
 */
var pointsOnPath = null;  // 경로 지점 데이터
var locationsOnPath = null;  // 키워드 장소 데이터
let searchPathBtn = document.getElementById('searchPathBtn');
searchPathBtn.addEventListener('click', function() {
	const departureName = document.getElementById('departure').value;
	const destinationName = document.getElementById('destination').value;
	const keyword = document.getElementById('keyword').value;
	
	if (!departureName) {
		alert("출발지를 입력해주세요.");
		return;
	}
	if (!destinationName) {
		alert("도착지를 입력해주세요.");
		return;
	}
	if (!keyword) {
		alert("키워드를 입력해주세요.");
		return;
	}
	if (keyword.length > 20) {
		alert("키워드는 20자 이하로 입력해주세요.");
		return;
	}
	
	const departureLngLat = document.getElementById('departureLngLat').value;
	const destinationLngLat = document.getElementById('destinationLngLat').value;
	
	// 검색 결과 가져오고 페이저 초기화 하기// fetch()를 통해 데이터 가져오기
	fetch("http://ec2-3-38-190-202.ap-northeast-2.compute.amazonaws.com:8000/whatsonmypath/paths" + "?keyword=" + keyword
			+ "&departureLngLat=" + departureLngLat
			+ "&destinationLngLat=" + destinationLngLat , {
		method: "GET",
	})
	.then((response) => response.json())
	.then((data) => {
		// 기존 폼 초기화
		document.getElementById('common-div').innerHTML = `
		<div class="container m-0">
			<div class="mt-2 p-2 rounded">
				<div id="myList" class="list-group"></div>
				<div class="pager text-center">
					<button class="btn btn-white border border-dark previous" onclick="prevPage(); return false;">이전</button>
					<button class="btn btn-white border border-dark next" onclick="nextPage(); return false;">다음</button>
				</div>
			</div>
		</div>`;
		if (data) {
			pointsOnPath = data.points;  // 경로 지점 데이터
			locationsOnPath = data.locations;  // 장소 데이터
			
			// 경로 그리기
			var pathsForDraw = [];
			for (var idx = 0; idx < pointsOnPath.length; idx++) {
				pathsForDraw.push(new kakao.maps.LatLng(pointsOnPath[idx][1], pointsOnPath[idx][0]));
			}

			var polyline = new kakao.maps.Polyline({
				path: pathsForDraw,
				strokeWeight: 5,
				strokeColor: '#009900',
				strokeOpacity: 0.7,
				strokeStyle: 'solid'
			});
			polyline.setMap(map);
			
			// 시작 위치로 이동
			moveFocus(departureLngLat);
			
			// 페이저 설정 및 초기화
			pager = {};
			pager.items = locationsOnPath;
			pager.itemsPerPage = 10;
			pagerInit(pager);  // 페이저 초기화
			nextPage();
			prevPage();
		}
	})
	.catch((error) => {
		console.log("fetch error:", error);
	});
	
});

/*
 * 이벤트 리스너, 검색 버튼 눌렀을 때 장소 검색 결과 띄우는 함수
 */
function searchLocBtn(inputFormID) {
	// 장소명 입력 여부 확인
	const locationQuery = document.getElementById('location-query').value;
	if (!locationQuery) {
		alert("장소명을 입력해주세요.");
		return;
	}
	
	// 검색 결과 삽입할 태그 가져오기
	let locationList = document.getElementById('location-list');
	
	// 검색 결과를 삽입하기 위해 기존 innerHTML 초기화
	locationList.innerHTML = "";

	// fetch()를 통해 데이터 가져오기
	fetch("http://ec2-3-38-190-202.ap-northeast-2.compute.amazonaws.com:8000/whatsonmypath/locations" + "?query=" + locationQuery, {
		method: "GET",
	})
	.then((response) => response.json())
	.then((data) => {
		// 검색 결과가 존재하지 않을 수 있으므로 해당 메시지로 innerHTML 초기화
		locationList.innerHTML = `
			<a class="list-group-item list-group-item-action text-center">
			<strong><mark>검색 결과가 없습니다.</mark></strong><br>
			</a>`;
		if (data) {
			locationList.innerHTML = "";
			for (var d_idx = 0; d_idx < Math.min(10, data.length); d_idx++) {
				const placeName = data[d_idx].place_name;  // 장소명
				const address = data[d_idx].road_address_name;  // 주소 (도로명 우선)
				if (!address)  // 도로명 없을 경우 지번
					data[d_idx].address_name;
				const lnglat = data[d_idx].x + "," + data[d_idx].y;
				
				locationList.innerHTML += `
					<a class="list-group-item list-group-item-action text-center"
						onclick="selectLocation(this, ` + inputFormID + `)">
					<strong><mark>` + placeName + `</mark></strong><br>
					<small>` + address + `</small>
					<span hidden>` + lnglat + `</span>
					</a>`;
			}
		}
	})
	.catch((error) => {
		locationList.innerHTML = `
			<a class="list-group-item list-group-item-action text-center">
			<strong><mark>오류가 발생했습니다.</mark></strong><br>
			<small>` + error + `</small>
			</a>`;
	});
}

/*
 * 검색 결과의 장소를 선택했을 때 출발지 검색 Form 또는 도착지 검색 Form에 데이터를 삽입하는 함수
 */
function selectLocation(event, inputForm) {
	// 장소명, 경도와 위도 가져오기
	const placeName = event.querySelector('mark').innerHTML;
	const lnglat = event.querySelector('span').innerHTML;
	
	// inputForm 장소명 삽입
	inputForm.value = placeName;
	// inputForm hidden 경도 및 위도 삽입
	document.getElementById(inputForm.id + 'LngLat').value = lnglat;
	
	// 검색 결과 및 장소명 입력 Form 초기화
// document.getElementById('location-list').innerHTML = "";
// let inputLocation = document.getElementById('input-location');
// inputLocation.innerHTML = "";
	
	// 마커 찍기
	mark(lnglat, true);
	// 해당 위치로 이동
	moveFocus(lnglat);
}

/*******************************************************************************
 * 지도 관련 함수 *
 ******************************************************************************/
/*
 * 지도를 띄우는 코드
 */
var container = document.getElementById('map'); // 지도를 담을 영역의 DOM 레퍼런스
var options = { // 지도를 생성할 때 필요한 기본 옵션
	center: new kakao.maps.LatLng(37.56667, 126.97806), // 지도의 중심좌표.
	level: 5 // 지도의 레벨(확대, 축소 정도)
};
var map = new kakao.maps.Map(container, options); // 지도 생성 및 객체 리턴

/*
 * 마커 찍는 코드
 */
var marker = null;  // 기존 마커를 지우기 위한 전역 마커
function mark(lnglat, removable) {
	let splitted = lnglat.split(',');
	let lng = parseFloat(splitted[0]);
	let lat = parseFloat(splitted[1]);
	
	var markerPosition = new kakao.maps.LatLng(lat, lng);
	
	// 기존에 생성했던 마커가 있다면 삭제하기
	if (marker && removable) {
		marker.setMap(null);
		marker = null;
	}
	// 마커 생성
	marker = new kakao.maps.Marker({
		position: markerPosition
	});
	// 마커 할당
	marker.setMap(map);
}
function moveFocus(lnglat) {
	let splitted = lnglat.split(',');
	let lng = parseFloat(splitted[0]);
	let lat = parseFloat(splitted[1]);
	var markerPosition = new kakao.maps.LatLng(lat, lng);
	
	// 지도 이동시키기 & 부드러운 이동
	map.setCenter(markerPosition);
	map.panTo(markerPosition);
}

/*******************************************************************************
 * 페이징 함수 * 참조: https://gist.github.com/rxtur/6c29e2b0d81bac2578ca
 ******************************************************************************/

var pager = {};

function bindList() {
  var pgItems = pager.pagedItems[pager.currentPage];
  $("#myList").empty();
  for(var i = 0; i < pgItems.length; i++){
	  // onclick : 해당 위치로 이동
	  var option = $(`<a class="list-group-item list-group-item-action text-center" onclick=moveFocus('` + pgItems[i].x + "," + pgItems[i].y + `')>`);
	  // 마커 찍기
	  mark(pgItems[i].x + "," + pgItems[i].y, false);
    
	  for( var key in pgItems[i] ){
		  option.html(
				  "<strong><mark>" + pgItems[i].place_name + "</mark></strong><br>"
			  		+ " <small>" + pgItems[i].address_name + "</small>"
			  		+ " <span hidden>" + pgItems[i].x + "," + pgItems[i].y + "</span>");
	  }
	  $("#myList").append(option);
  }
}
function prevPage(){
  pager.prevPage();
  bindList();
}
function nextPage(){
  pager.nextPage();
  bindList();
}
function pagerInit(p) {	
  p.pagedItems = [];
  p.currentPage = 0;
  if (p.itemsPerPage === undefined) {
    p.itemsPerPage = 5;
  }
  p.prevPage = function () {
if (p.currentPage > 0) {
      p.currentPage--;
    }
  };
  p.nextPage = function () {
    if (p.currentPage < p.pagedItems.length - 1) {
      p.currentPage++;
    }
  };
  init = function () {
    for (var i = 0; i < p.items.length; i++) {
      if (i % p.itemsPerPage === 0) {
        p.pagedItems[Math.floor(i / p.itemsPerPage)] = [p.items[i]];
      } else {
        p.pagedItems[Math.floor(i / p.itemsPerPage)].push(p.items[i]);
      }
    }
  };
  init();
}		
$(function() {
  bindList();
});
