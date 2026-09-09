package ru.drmemex.classifieds.feature.conversation.message.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;
import ru.drmemex.classifieds.feature.conversation.message.repository.MessageRepository;

import java.util.List;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Message save(Message message) {
        entityManager.persist(message);
        return message;
    }

    @Override
    public List<Message> findByConversationId(Long conversationId) {
        TypedQuery<Message> query = entityManager.createQuery(
                """
                        SELECT m
                        FROM Message m
                        WHERE m.conversation.id = :conversationId
                        ORDER BY m.createdAt ASC, m.id ASC
                        """,
                Message.class
        );

        query.setParameter("conversationId", conversationId);

        return query.getResultList();
    }

    @Override
    public List<Message> searchByConversationIdAndText(
            Long conversationId,
            String text
    ) {
        TypedQuery<Message> query = entityManager.createQuery(
                """
                        SELECT m
                        FROM Message m
                        WHERE m.conversation.id = :conversationId
                        AND LOWER(m.text) LIKE LOWER(:text)
                        ORDER BY m.createdAt ASC, m.id ASC
                        """, Message.class
        );

        query.setParameter("conversationId", conversationId);
        query.setParameter("text", "%" + text + "%");

        return query.getResultList();
    }
}