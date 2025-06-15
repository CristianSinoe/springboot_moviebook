package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.init;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.EReaction;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Reaction;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.ReactionRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ReactionRepository reactionRepository;

    public DataInitializer(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public void run(String... args) {
        for (EReaction r : EReaction.values()) {
            boolean exists = reactionRepository.existsByName(r);
            if (!exists) {
                Reaction newReaction = new Reaction();
                newReaction.setName(r);
                // newReaction.setName(r);
                reactionRepository.save(newReaction);
                System.out.println("Inserted reaction: " + r.name());
            }
        }
    }
}