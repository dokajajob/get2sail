# Generated by Django 3.2.13 on 2022-05-23 10:30

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('get2sail_app', '0007_rename_user_id_location_uid'),
    ]

    operations = [
        migrations.AlterField(
            model_name='location',
            name='uid',
            field=models.CharField(max_length=64, unique=True),
        ),
    ]
