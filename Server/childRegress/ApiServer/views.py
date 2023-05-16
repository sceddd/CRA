from rest_framework import viewsets
from rest_framework.decorators import parser_classes
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.views import APIView

from .models import InputImage
from .serializers import ObjectSerializer


class ObjectViewSet(viewsets.ModelViewSet):
    queryset = InputImage.objects.all()
    serializer_class = ObjectSerializer
    parser_classes = (MultiPartParser, FormParser)


@parser_classes((MultiPartParser,))
class UploadFileAndJson(APIView):
    pass
