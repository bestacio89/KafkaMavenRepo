package org.personal.kafkamavenrepo.persistence.Postgres;

import org.personal.kafkamavenrepo.domain.Postgres.BusinessObjects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}