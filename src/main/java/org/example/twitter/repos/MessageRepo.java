package org.example.twitter.repos;

import org.example.twitter.domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepo extends CrudRepository<Message, Long> {
}
