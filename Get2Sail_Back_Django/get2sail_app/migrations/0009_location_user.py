# Generated by Django 3.2.13 on 2022-05-31 13:49

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('get2sail_app', '0008_alter_location_uid'),
    ]

    operations = [
        migrations.AddField(
            model_name='location',
            name='user',
            field=models.CharField(default='guest', max_length=64),
        ),
    ]
