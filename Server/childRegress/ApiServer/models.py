from django.db import models


def upload_to(instance, filename):
    return 'image/{filename}'.format(filename=filename)


class InputImage(models.Model):
    Img = models.ImageField(upload_to=upload_to, blank=True, null=True)

class OutputImage(models.Model):
