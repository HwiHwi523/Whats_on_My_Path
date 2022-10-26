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
 * 경로 검색 버튼 눌렀을 때 Form Submit 하기
 */
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
	
	let searchPathForm = document.getElementById('searchPathForm');
	searchPathForm.submit();
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
//	fetch("https://dapi.kakao.com/v2/local/search/keyword.json" + "?query=" + locationQuery, {
//		method: "GET",
//		headers: {
//			"Authorization": "KakaoAK " + "99642aded3fb5aaffaca7f5f816b46fe",
//		},
//	})
	fetch("/whatsonmypath/locations" + "?query=" + locationQuery, {
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
			data = data.documents;
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
	document.getElementById('location-list').innerHTML = "";
	let inputLocation = document.getElementById('input-location');
	inputLocation.innerHTML = "";
}
