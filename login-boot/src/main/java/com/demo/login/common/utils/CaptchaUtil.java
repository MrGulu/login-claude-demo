package com.demo.login.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码生成工具类
 */
public class CaptchaUtil {

    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    /**
     * 生成验证码文本
     */
    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片（Base64）
     */
    public static String generateImage(String code) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            // 设置背景色
            g.setColor(new Color(245, 245, 245));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // 绘制干扰线
            Random random = new Random();
            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < 5; i++) {
                int x1 = random.nextInt(WIDTH);
                int y1 = random.nextInt(HEIGHT);
                int x2 = random.nextInt(WIDTH);
                int y2 = random.nextInt(HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }

            // 绘制验证码
            g.setFont(new Font("Arial", Font.BOLD, 28));
            for (int i = 0; i < code.length(); i++) {
                g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
                g.drawString(String.valueOf(code.charAt(i)), 20 + i * 25, 28);
            }

            g.dispose();

            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        } catch (Exception e) {
            throw new RuntimeException("验证码生成失败", e);
        }
    }
}
