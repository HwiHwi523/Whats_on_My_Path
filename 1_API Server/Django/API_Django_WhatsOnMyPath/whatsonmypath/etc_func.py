
def focus_point(lng, lat):
    """
    경도, 위도로 나올 수 있는 좌표는 굉장히 다양하므로,
        약 (50M * 50M) 범위 기준으로 좌표를 조정함
        이후 조정된 좌표를 통해 DB에 저장한다면 오차도 적고 효율도 증가할 것
    """
    focus_result = [lng, lat]

    lng_floor = int(lng * 1000) / 1000
    min_diff, focus_result[0] = abs(lng - lng_floor), lng_floor
    lng_floor += 0.0005 - 0.001 * (lng < 0)
    if abs(lng - lng_floor) < min_diff:
        min_diff, focus_result[0] = abs(lng - lng_floor), lng_floor
    lng_floor += 0.0005 - 0.001 * (lng < 0)
    if abs(lng - lng_floor) < min_diff:
        min_diff, focus_result[0] = abs(lng - lng_floor), lng_floor

    lat_floor = int(lat * 1000) / 1000
    min_diff, focus_result[1] = abs(lat - lat_floor), lat_floor
    lat_floor += 0.0005 - 0.001 * (lat < 0)
    if abs(lat - lat_floor) < min_diff:
        min_diff, focus_result[1] = abs(lat - lat_floor), lat_floor
    lat_floor += 0.0005 - 0.001 * (lat < 0)
    if abs(lat - lat_floor) < min_diff:
        min_diff, focus_result[1] = abs(lat - lat_floor), lat_floor

    return focus_result
