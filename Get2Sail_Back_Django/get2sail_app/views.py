from rest_framework import viewsets
from rest_framework.authtoken.admin import User
from rest_framework.decorators import api_view
from rest_framework.permissions import AllowAny
from get2sail_app.models import Location
from get2sail_app.serializers import LocationSerializer, UserSerializer
from django.http import JsonResponse


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

    @api_view(['GET', 'POST', 'DELETE'])
    def user_type_list(self, request):
        if request.method == 'GET':
            users = Location.objects.all()

            user = request.GET.get('user', None)
            if user is not None:
                users = users.filter(user__icontains=user)

            users_serializer = LocationSerializer(users, many=True)
            return JsonResponse(users_serializer.data, safe=False)
            # 'safe=False' for objects serialization





