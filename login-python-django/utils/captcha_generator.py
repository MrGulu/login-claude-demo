import random
import string
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import io
import base64

def generate_captcha(length=4):
    """
    生成4位字符验证码（字母+数字）
    """
    # 选择字符集（去掉容易混淆的字符如 0, O, 1, I）
    chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
    code = ''.join(random.choices(chars, k=length))
    
    # 图像配置
    width, height = 120, 40
    bgcolor = (255, 255, 255)
    image = Image.new('RGB', (width, height), bgcolor)
    draw = ImageDraw.Draw(image)
    
    try:
        # Windows 常用字体路径
        font_paths = ["arial.ttf", "C:\\Windows\\Fonts\\arial.ttf", "DejaVuSans.ttf"]
        font = None
        for path in font_paths:
            try:
                font = ImageFont.truetype(path, 25)
                break
            except:
                continue
        if font is None:
            font = ImageFont.load_default()
    except:
        font = ImageFont.load_default()
        
    # 绘制字符
    for i, char in enumerate(code):
        char_color = (random.randint(0, 150), random.randint(0, 150), random.randint(0, 150))
        draw.text((10 + i * 25, 5), char, font=font, fill=char_color)
        
    # 绘制干扰线
    for _ in range(5):
        line_color = (random.randint(150, 255), random.randint(150, 255), random.randint(150, 255))
        draw.line([(random.randint(0, width), random.randint(0, height)), 
                   (random.randint(0, width), random.randint(0, height))], fill=line_color)
        
    # 绘制噪点
    for _ in range(50):
        draw.point((random.randint(0, width), random.randint(0, height)), fill=(random.randint(0, 255), 0, 0))
        
    # 保存到内存
    buf = io.BytesIO()
    image.save(buf, format='PNG')
    img_base64 = base64.b64encode(buf.getvalue()).decode('utf-8')
    
    return code, f"data:image/png;base64,{img_base64}"

def generate_math_captcha():
    return generate_captcha()
