# Generated by Django 3.2.13 on 2022-05-15 16:19

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('get2sail_app', '0003_alter_location_user'),
    ]

    operations = [
        migrations.RenameField(
            model_name='location',
            old_name='user',
            new_name='user_id',
        ),
    ]