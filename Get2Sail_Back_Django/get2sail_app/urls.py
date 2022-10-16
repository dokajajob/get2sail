from django.urls import path, include
from rest_framework import routers
from get2sail_app.views import LocationViewSet, UserViewSet, SearchUserTypeViewSet

router = routers.DefaultRouter()
router.register('location', LocationViewSet)
router.register('users', UserViewSet)
router.register('usertypesearch', SearchUserTypeViewSet)

urlpatterns = [
    path('', include(router.urls)),
]

