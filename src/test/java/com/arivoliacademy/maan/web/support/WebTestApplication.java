package com.arivoliacademy.maan.web.support;

import com.arivoliacademy.maan.web.autoconfigure.MaanWebAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({MaanWebAutoConfiguration.class, SampleTestController.class})
public class WebTestApplication {
}
