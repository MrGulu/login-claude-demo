import time

class SimpleCache:
    def __init__(self):
        self.store = {}
        
    def set(self, key, value, timeout=None):
        expire = time.time() + timeout if timeout else None
        self.store[key] = (value, expire)
        
    def get(self, key):
        if key in self.store:
            value, expire = self.store[key]
            if expire and time.time() > expire:
                del self.store[key]
                return None
            return value
        return None
        
    def delete(self, key):
        if key in self.store:
            del self.store[key]

cache = SimpleCache()
