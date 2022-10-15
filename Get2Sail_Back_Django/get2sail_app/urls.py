from django.urls import path, include
from rest_framework import routers
from get2sail_app.views import LocationViewSet, UserViewSet

router = routers.DefaultRouter()
router.register('location', LocationViewSet)
router.register('users', UserViewSet)

urlpatterns = [
    path('', include(router.urls)),
]

