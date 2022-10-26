from rest_framework import serializers
from .models import Place


class PlaceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Place
        fields = (
            'place_id', 'place_name', 'x', 'y',
            'category_name', 'phone',
            'address_name', 'road_address_name', 'place_url'
        )
