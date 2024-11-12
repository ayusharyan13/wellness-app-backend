package com.ayush.blog.config;
import com.cloudinary.Cloudinary;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dgz1lwjmk");
        config.put("api_key", "162541985346481");
        config.put("api_secret", "cB3WlCUqITTjnlGbMgfdkx5PshQ");
        return new Cloudinary(config);
    }
}
