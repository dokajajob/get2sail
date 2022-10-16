from rest_framework import viewsets
from rest_framework.authtoken.admin import User
from rest_framework.permissions import AllowAny
from get2sail_app.models import Location
from get2sail_app.serializers import LocationSerializer, UserSerializer
from rest_framework.exceptions import ValidationError


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


class SearchUserTypeViewSet(viewsets.ModelViewSet):
    queryset = Location.objects.all()
    serializer_class = LocationSerializer

    def get_queryset(self):
        searched_user = self.request.query_params.get('user')
        if self.request.method == 'GET' and searched_user is not None:
            queryset = Location.objects.all()
            queryset = queryset.filter(user=searched_user)
            return queryset
        elif self.request.method == 'GET' and searched_user is None:
            raise ValidationError({"error": ["check user type param input"]})






