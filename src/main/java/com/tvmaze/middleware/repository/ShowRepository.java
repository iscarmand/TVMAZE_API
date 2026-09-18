package com.tvmaze.middleware.repository;

import com.tvmaze.middleware.model.ShowDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author armand
 */
@Repository
public interface ShowRepository extends MongoRepository<ShowDocument, Long> {
    
}
