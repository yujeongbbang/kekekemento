package com.kekeke.kekekebackend.common.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FirebaseManager {
    @PostConstruct
    public void Initialize() throws Exception
    {
        ClassPathResource resource = new ClassPathResource("aa.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();
        FirebaseApp.initializeApp(options);
    }
}
