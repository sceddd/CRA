from django.test import TestCase
from .models import InputImage

class TestModel(TestCase):
    def setUp(self):
        InputImage.objects.create()