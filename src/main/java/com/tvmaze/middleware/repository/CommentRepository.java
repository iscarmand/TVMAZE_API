package com.tvmaze.middleware.repository;

import com.tvmaze.middleware.model.CommentDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author armand
 */
@Repository
public interface CommentRepository extends MongoRepository<CommentDocument, String> {
    List<CommentDocument> findByShowId(Long showId);
}
