# Generated by Django 4.0.10 on 2023-02-24 14:32

import ApiServer.models
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('ApiServer', '0002_remove_object_body'),
    ]

    operations = [
        migrations.AddField(
            model_name='object',
            name='body',
            field=models.ImageField(blank=True, null=True, upload_to=ApiServer.models.upload_to),
        ),
    ]
