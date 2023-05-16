from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ObjectViewSet


router = DefaultRouter()
router.register('api', ObjectViewSet)

urlpatterns = [
    path('', include(router.urls))
]