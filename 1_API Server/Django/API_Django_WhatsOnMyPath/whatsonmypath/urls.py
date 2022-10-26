from django.urls import path

from . import views


urlpatterns = [
    path('locations', views.locations),
    path('paths', views.paths),
]
