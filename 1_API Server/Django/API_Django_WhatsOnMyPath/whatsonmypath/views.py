from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response

from django.contrib.gis.geos import Point
from django.contrib.gis.measure import D
from django.http import JsonResponse

from .serializers import PlaceSerializer
from .models import Place, RecentSearch, SearchLog

from .kakao_apis import get_locations, get_points_on_path

from haversine import haversine
from .etc_func import focus_point
import datetime


@api_view(['GET'])
def locations(request):
    if request.method != 'GET':
        return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)

    params = request.GET
    query = params.get('query')
    x, y = params.get('x'), params.get('y')
    radius = params.get('radius')

    # 필수 파라미터 query 유무 및 공백 확인
    if not query:
        return Response(status=status.HTTP_400_BAD_REQUEST)
    # x, y 존재할 경우 모두 존재하는지 확인
    if x or y:  # x, y 존재하는데
        if not x or not y:  # 하나라도 빠져 있는 경우 오류 반환
            return Response(status=status.HTTP_400_BAD_REQUEST)
    # radius 파라미터 없을 경우 기본값 설정
    radius = int(radius) if radius else 500

    # 키워드 장소 검색 결과 가져오기
    places_result = get_locations(query, x, y, radius)

    # 새로운 장소를 DB에 저장하기 위해 DB에서 기존 장소 ID Set 가져오기
    if x and y:  # 파라미터로 지정된 인자가 있다면, 해당 범위 내에서 검색
        places_db = {query_set['place_id']
                     for query_set in Place.objects.filter(
                location__distance_lte=(Point(float(x), float(y)), D(m=radius))
            ).values('place_id')}
    else:
        places_db = {query_set['place_id']
                     for query_set in Place.objects.all().values('place_id')}

    # API로 가져온 데이터 중에서 DB에 없는 장소 정보 삽입
    queries_list = []
    for place_result in places_result:
        # 검색 결과를 반환하기 위해 모델로 변환
        queries_list.append(Place(
            place_id=place_result['place_id'],
            place_name=place_result['place_name'],
            location=place_result['location'],
            x=float(place_result['location'].x),
            y=float(place_result['location'].y),
            category_name=place_result['category_name'],
            phone=place_result['phone'],
            address_name=place_result['address_name'],
            road_address_name=place_result['road_address_name'],
            place_url=place_result['place_url'],
        ))
        if place_result['place_id'] in places_db:  # DB에 존재하는 장소면 저장하지 않고 넘어가기
            continue
        queries_list[-1].save()  # DB 저장

    data = PlaceSerializer(queries_list, many=True).data
    return Response(data)


@api_view(['GET'])
def paths(request):
    if request.method != 'GET':
        return Response(status.HTTP_405_METHOD_NOT_ALLOWED)

    params = request.GET
    departure_lnglat = params.get('departureLngLat')
    destination_lnglat = params.get('destinationLngLat')
    keyword = params.get('keyword')

    # 출발지 경도 위도, 도착지 경도 위도 유효 여부 확인
    try:
        departure_lng, departure_lat = map(float, departure_lnglat.split(','))
        destination_lng, destination_lat = map(float, destination_lnglat.split(','))
        if not keyword:
            raise Exception
    except:
        return Response(status=status.HTTP_400_BAD_REQUEST)

    # 길찾기 결과 얻기
    result = get_points_on_path(departure_lnglat, destination_lnglat)
    result['locations'] = []  # 경로 위에 있는 장소의 정보를 담을 리스트
    if result['result_code'] != 0:  # 길찾기 실패 시 빈 데이터 및 400 반환
        return JsonResponse(data=result, status=400)

    # 유사 검색 결과를 최소화 하기 위해 약 500M 단위로 구분한 지점들에 대해서만 키워드 검색
    prev_point = [0, 0]  # 시작 위치
    for p_idx in range(len(result['points'])):
        cur_point = focus_point(*result['points'][p_idx])  # 현재 위치
        # 이전 지점과 현재 지점 간 거리 구하기
        distance = haversine(prev_point[::-1], cur_point[::-1], unit='m')
        # 거리 차이가 500M 미만이면 다음 지점으로 넘어가기
        if distance < 500:
            continue
        # (경도, 위도) -> Point 변환 및 장소 검색 이력 확인
        location = Point(*cur_point)
        # try:
        #     recent = RecentSearch.objects.get(location=location)
        # except:
        #     recent = RecentSearch(location=location)

        # if | 검색 이력이 없거나 전날 이전일 경우 API로 장소 검색
        # else | 오늘자 검색 이력이 있다면 DB에서 가져오기 => !!!!!!!! 연관 없는 장소들과 겹쳐 꼬일 수 있음 !!!!!!!!
        # if recent.search_time is None \
        #         or recent.search_time.date() < datetime.date.today():
        # 키워드 장소 검색 결과 가져오기
        places_result = get_locations(keyword, *map(str, cur_point))

        # 새로운 장소를 DB에 저장하기 위해 DB에서 기존 장소 ID Set 가져오기
        places_db = {query_set['place_id']
                     for query_set in Place.objects.filter(
                location__distance_lte=(location, D(m=500))
            ).values('place_id')}

        # API로 가져온 데이터 중에서 DB에 없는 장소 정보 삽입
        for place_result in places_result:
            # 검색 결과를 반환하기 위해 모델로 변환
            result['locations'].append(Place(
                place_id=place_result['place_id'],
                place_name=place_result['place_name'],
                location=place_result['location'],
                x=float(place_result['location'].x),
                y=float(place_result['location'].y),
                category_name=place_result['category_name'],
                phone=place_result['phone'],
                address_name=place_result['address_name'],
                road_address_name=place_result['road_address_name'],
                place_url=place_result['place_url'],
            ))
            if place_result['place_id'] not in places_db:  # DB에 존재하지 않다면 저장
                result['locations'][-1].save()  # DB 저장
            # 직렬화를 위해 model -> dict
            result['locations'][-1] = dict(PlaceSerializer(result['locations'][-1]).data)
            # recent.save()  # 검색 이력 갱신
        # else:
        #     # 오늘자 검색 이력이 있다면 DB에서 가져오기
        #     for query_result in Place.objects.filter(location__distance_lte=(location, D(m=500))):
        #         result['locations'].append(dict(PlaceSerializer(query_result).data))

        return JsonResponse(data=result, json_dumps_params={'ensure_ascii': False}, status=200)
