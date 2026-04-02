from django.http import JsonResponse

def success(data=None, message="操作成功"):
    return JsonResponse({
        "code": 200,
        "message": message,
        "data": data
    })

def error(code=500, message="操作失败"):
    return JsonResponse({
        "code": code,
        "message": message,
        "data": None
    })
