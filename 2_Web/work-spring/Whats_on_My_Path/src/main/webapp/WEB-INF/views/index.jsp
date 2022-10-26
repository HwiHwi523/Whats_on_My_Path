<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<%@ include file="include/header.jsp"%>

<main>
	<div class="row w-100 h-100 m-0 p-0">

		<!-- 길찾기 검색 폼 Begin -->
		<div class="col-3 h-100 m-0 p-0">
			<!-- 출발지, 도착지, 키워드 입력 폼 Begin -->
			<div class="container m-0">
				<div class="mt-4 p-2 rounded">
					<form id="searchPathForm" action="#">
						<!-- 출발지 검색을 위한 input -->
						<div class="container input-group mt-2 p-0">
							<span class="input-group-text">출발지</span>
							<input type="text" id="departure" name="departure"
								class="form-control" placeholder="출발지 검색" readonly>
							<input type="hidden" id="departureLngLat" name="departureLngLat"
								class="form-control">
						</div>
		
						<!-- 도착지 검색을 위한 input -->
						<div class="container input-group mt-2 p-0">
							<span class="input-group-text">도착지</span>
							<input type="text" id="destination" name="destination"
								class="form-control" placeholder="도착지 검색" readonly>
							<input type="hidden" id="destinationLngLat" name="destinationLngLat"
								class="form-control">
						</div>
		
						<!-- 키워드 입력 -->
						<div class="container input-group mt-2 p-0">
							<span class="input-group-text">키워드</span>
							<input type="text" id="keyword" name="keyword"
								class="form-control" placeholder="키워드 입력">
						</div>
		
						<!-- 경로 검색 버튼 -->
						<div class="container input-group mt-2 mb-2 p-0">
							<button type="button" id="searchPathBtn" name="searchPathBtn"
								class="btn border border-dark w-100" style="box-shadow:1px 1px 1px gray;">경로 검색</button>
						</div>
					</form>
				</div>
			</div>
			<!-- 출발지, 도착지, 키워드 입력 폼 End -->
			
			<!-- 출발지 및 도착지 목록 리스트 Begin -->
			<div class="container m-0">
				<div class="p-2 rounded">
					<!-- 검색할 장소의 이름을 검색할 Form -->
					<div id="input-location"></div>
					<!-- 장소 검색 시 검색 결과 장소들이 표시될 곳 -->
					<div id="location-list" class="list-group p-0"></div>
				</div>
			</div>
			<!-- 출발지 및 도착지 목록 리스트 End -->
		</div>
		<!-- 길찾기 검색 폼 End -->

		<!-- 카카오 맵 Begin -->
		<div class="col-9 h-100 m-0 p-0">
			<div id="map"></div>
		</div>
		<!-- 카카오 맵 End -->

	</div>
</main>

<%@ include file="include/footer.jsp"%>