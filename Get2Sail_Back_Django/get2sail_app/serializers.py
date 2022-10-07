from django.contrib.auth.models import User
from rest_framework import serializers
from rest_framework.authtoken.models import Token
from get2sail_app.models import Location

class LocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Location
        fields = ('id', 'uid', 'user', 'lat', 'lng', 'date')



class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'username', 'password')
        extra_kwargs = {'password': {'write_only': True, 'required': True}}

        # def create(self, validated_data):
        #     user = User.objects.create_user(**validated_data)
        #     Token.objects.create(user=user)
        #     return user

        def create(self, validated_data):
            # user = User(
            #     username=validated_data['username'],
            #     password=validated_data['password']
            # )
            user = User.objects.create_user(**validated_data)
            Token.objects.create(user=user)
            user.set_password('password')
            user.save()
            return user