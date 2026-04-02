import random
import io
import base64
from PIL import Image, ImageDraw, ImageFont

def generate_math_captcha():
    # Generate simple math equation
    num1 = random.randint(1, 10)
    num2 = random.randint(1, 10)
    operator = random.choice(['+', '-'])
    if operator == '+':
        result = num1 + num2
    else:
        # Ensure positive result
        if num1 < num2:
            num1, num2 = num2, num1
        result = num1 - num2
    
    equation = f"{num1} {operator} {num2} = ?"
    
    # Create image
    width, height = 120, 40
    image = Image.new('RGB', (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(image)
    
    # Try to load a generic font, or fallback to default
    try:
        font = ImageFont.truetype("arial.ttf", 24)
    except IOError:
        font = ImageFont.load_default()
        
    # Draw equation
    text_bbox = draw.textbbox((0, 0), equation, font=font)
    text_width = text_bbox[2] - text_bbox[0]
    text_height = text_bbox[3] - text_bbox[1]
    
    x = (width - text_width) / 2
    y = (height - text_height) / 2
    draw.text((x, y), equation, fill=(0, 0, 0), font=font)
    
    # Draw noise
    for _ in range(50):
        draw.point((random.randint(0, width), random.randint(0, height)), fill=(100, 100, 100))
        
    for _ in range(5):
        draw.line([
            (random.randint(0, width), random.randint(0, height)),
            (random.randint(0, width), random.randint(0, height))
        ], fill=(0, 0, 0), width=1)
        
    # Convert to base64
    buffered = io.BytesIO()
    image.save(buffered, format="PNG")
    img_str = base64.b64encode(buffered.getvalue()).decode('utf-8')
    
    return result, img_str
