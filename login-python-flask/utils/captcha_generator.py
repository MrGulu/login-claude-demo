import random
import io
import base64
from PIL import Image, ImageDraw, ImageFont

def generate_captcha():
    # Use 4-char alphanumeric to match Java original behavior and frontend requirement
    chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    code = "".join(random.choice(chars) for _ in range(4))
    result = code
    
    # Create image
    width, height = 120, 40
    image = Image.new('RGB', (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(image)
    
    # Try to load a generic font, or fallback to default
    try:
        font = ImageFont.truetype("arial.ttf", 24)
    except IOError:
        font = ImageFont.load_default()
        
    # Draw text
    text_bbox = draw.textbbox((0, 0), code, font=font)
    text_width = text_bbox[2] - text_bbox[0]
    text_height = text_bbox[3] - text_bbox[1]
    
    # Add some spacing to text
    x = (width - text_width) / 2
    y = (height - text_height) / 2
    draw.text((x, y), code, fill=(0, 0, 0), font=font)
    
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
