import action as action
from django.contrib.auth.models import User
from django.http import request, HttpResponse
from django.views import View
from rest_framework import viewsets, status
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
from get2sail_app.models import Location
from get2sail_app.serializers import LocationSerializer, UserSerializer
# from braces.views import CsrfExemptMixin
# from sqlalchemy.schema import UniqueConstraint
from rest_framework import generics
import asyncio
from django.http import JsonResponse
from asgiref.sync import sync_to_async
from time import sleep


class UserViewSet(viewsets.ModelViewSet):
    authentication_classes = []
    queryset = User.objects.all()
    serializer_class = UserSerializer
    # authentication_classes = (TokenAuthentication,)
    permission_classes = (AllowAny,)

class LocationViewSet(viewsets.ModelViewSet):
    authentication_classes = []
    queryset = Location.objects.all()
    serializer_class = LocationSerializer
    # authentication_classes = (TokenAuthentication,)
    permission_classes = (AllowAny,)

class async_task:

    @sync_to_async
    def crunching_stuff(self):
        sleep(10)
        print("Woke up after 10 seconds!")

    async def index(self, request):
        json_payload = {
            "message": "Hello world"
        }
        """
        or also
        asyncio.ensure_future(crunching_stuff())
        loop.create_task(crunching_stuff())
        """
        asyncio.create_task(obj.crunching_stuff())
        return JsonResponse(json_payload)

obj = async_task()



    # # @action(detail=True, methods=['POST'])
    # def post(self, request, pk=None):
    #     return Response("ok")




