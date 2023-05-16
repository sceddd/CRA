from rest_framework import serializers
from .models import InputImage


class ObjectSerializer(serializers.ModelSerializer):
    class Meta:
        model = InputImage
        fields = '__all__'
