from django.db import models
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token
from django.conf import settings


@receiver(post_save, sender=settings.AUTH_USER_MODEL)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)


class Location(models.Model):
        date = models.CharField(max_length=64)
        lat = models.CharField(max_length=32)
        lng = models.CharField(max_length=32)
        user = models.CharField(max_length=64, default='guest', null=False, blank=False)
        uid = models.CharField(max_length=64, blank=False, null=False, unique=True)

        # class Meta:
        #     unique_together = (('id', 'user_id'),)
        #     index_together = (('id', 'user_id'),)
        #     # UniqueConstraint(fields=['id', 'user_id'], name='unique_location')
