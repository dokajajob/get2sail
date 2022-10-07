# Generated by Django 3.2.13 on 2022-05-15 13:51

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Location',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('date', models.CharField(max_length=64)),
                ('lat', models.CharField(max_length=32)),
                ('lng', models.CharField(max_length=32)),
                ('uid', models.CharField(max_length=64)),
                ('uname', models.CharField(max_length=64)),
            ],
        ),
    ]