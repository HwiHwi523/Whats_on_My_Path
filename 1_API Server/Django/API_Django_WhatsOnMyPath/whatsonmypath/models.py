from django.contrib.gis.db import models


class SearchLog(models.Model):
    departure = models.PointField()
    destination = models.PointField()
    keyword = models.CharField(max_length=20)
    search_time = models.DateTimeField(auto_now_add=True)


class Place(models.Model):
    place_id = models.CharField(primary_key=True, max_length=100, blank=True)
    place_name = models.CharField(blank=True, max_length=100)
    location = models.PointField()
    x = models.FloatField(default=0.0)
    y = models.FloatField(default=0.0)
    category_name = models.CharField(blank=True, max_length=100)
    phone = models.CharField(blank=True, max_length=20)
    address_name = models.CharField(blank=True, max_length=100)
    road_address_name = models.CharField(blank=True, max_length=100)
    place_url = models.URLField()


class RecentSearch(models.Model):
    location = models.PointField()
    search_time = models.DateTimeField(auto_now=True)
