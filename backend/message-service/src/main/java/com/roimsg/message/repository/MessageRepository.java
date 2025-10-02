package com.roimsg.message.repository;

import com.roimsg.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.tenantId = :tenantId AND ((m.senderId = :u1 AND m.recipientId = :u2) OR (m.senderId = :u2 AND m.recipientId = :u1)) ORDER BY m.createdAt DESC")
    List<Message> findConversation(@Param("tenantId") UUID tenantId, @Param("u1") UUID u1, @Param("u2") UUID u2);
}