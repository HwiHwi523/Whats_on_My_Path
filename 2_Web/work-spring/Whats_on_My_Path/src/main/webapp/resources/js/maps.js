/*
 * 지도를 띄우는 코드
 */
var container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
var options = { //지도를 생성할 때 필요한 기본 옵션
	center: new kakao.maps.LatLng(37.56667, 126.97806), //지도의 중심좌표.
	level: 5 //지도의 레벨(확대, 축소 정도)
};
var map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴

/*
 * 마커 찍는 코드
 */
var marker;  // 기존 마커를 지우기 위한 전역 마커
function mark(lnglat) {
	let splitted = lnglat.split(',');
	let lng = parseFloat(splitted[0]);
	let lat = parseFloat(splitted[1]);
	
	var markerPosition = new kakao.maps.LatLng(lat, lng);
}

