import requests
from django.contrib.gis.geos import Point


_REST_API_KEY = "KAKAO REST API Key"

_HOST_LOCAL = "https://dapi.kakao.com"
_HOST_NAVI = "https://apis-navi.kakaomobility.com"


_HEADERS = {
    "Authorization": "KakaoAK" + " " + _REST_API_KEY,
    "Content-Type": "application/json"
}


def get_locations(query, x=None, y=None, radius=500):
    """
    query: 질의어
    x: 중심 경도
    y: 중심 위도
    radius: 중심 좌표로부터의 반경
    return: 중심 좌표 (x, y)로부터 반경 radius 미터 내에 있는 질의어와 관련된 장소들의 목록
    """

    # API 호출을 위한 파라미터 설정
    params = {"query": query}
    if x and y:
        params["x"] = x
        params["y"] = y
        params["radius"] = radius

    # API Request
    response = requests.get(
        url=_HOST_LOCAL + "/v2/local/search/keyword.json",
        headers=_HEADERS,
        params=params
    )

    response_result = response.json()  # Response -> Json
    documents = response_result["documents"]  # 질의어와 관련된 장소들

    result = []  # 필요한 정보만 추린 장소들

    for document in documents:
        result.append(
            {
                "place_id": document["id"],
                "place_name": document["place_name"],
                "location": Point(float(document["x"]), float(document["y"])),
                "category_name": document["category_name"],
                "phone": document["phone"],
                "address_name": document["address_name"],
                "road_address_name": document["road_address_name"],
                "place_url": document["place_url"],
            }
        )

    return result


def get_points_on_path(origin, destination):
    """
    origin: 출발지 {경도,위도}
        (e.g. "127.00,32.00" )
    destination: 도착지 {경도,위도} 문자열
        (e.g. "127.53,32.53" )
    return: 출발지에서 도착지로 가는 경로를 이루는 (경도, 위도) 리스트
        (e.g. [(127.0, 32.0), (127.1, 32.1), ..., (127.53, 32.53)] )
    """

    # API Request
    response = requests.get(
        url=_HOST_NAVI + "/v1/directions",
        headers=_HEADERS,
        params={
            "origin": origin,
            "destination": destination,
        }
    )
    response_result = response.json()  # Response -> Json 변환
    route = response_result['routes'][0]  # 경로 정보
    if response.status_code >= 400 \
            or route['result_code'] != 0:  # 응답에 실패했거나 길찾기 실패 시 종료
        return {
            "result_code": route['result_code'],
            "result_msg": route['result_msg'],
            "points": [],
        }
    roads = route['sections'][0]['roads']  # 경로를 구성하는 도로들 정보

    points = []  # 경로를 구성하는 지점을 저장할 리스트
    for road in roads:  # 모든 도로를 확인하며
        vertexes = road['vertexes']  # 각 도로의 지점 리스트를 통해
        for idx in range(0, len(vertexes), 2):  # 경도와 위도 저장, 경도와 위도 순서의 1차원 리스트이므로 2씩 증가시키며 저장
            points.append((vertexes[idx], vertexes[idx + 1]))

    # 길찾기 검색 결과 메시지와 경로를 이루는 지점 리스트 반환
    result = {
        "result_code": route['result_code'],
        "result_msg": route['result_msg'],
        "points": points,
    }
    return result
