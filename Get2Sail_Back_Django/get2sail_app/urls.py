from django.contrib import admin
from django.urls import path, include
from rest_framework import routers
from rest_framework.authtoken.views import obtain_auth_token
# from django.views.decorators.csrf import csrf_exempt
from rest_framework.authtoken.views import obtain_auth_token

from . import views

from get2sail_app.views import LocationViewSet, UserViewSet

router = routers.DefaultRouter()
router.register('location', LocationViewSet)
router.register('users', UserViewSet)

urlpatterns = [
    path('', include(router.urls)),
]

