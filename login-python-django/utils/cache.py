from django.core.cache import cache as django_cache

class CacheWrapper:
    def set(self, key, value, timeout=300):
        django_cache.set(key, value, timeout)

    def get(self, key):
        return django_cache.get(key)

    def delete(self, key):
        django_cache.delete(key)

cache = CacheWrapper()
