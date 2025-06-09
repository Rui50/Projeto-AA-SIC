package com.example.appfitness.repositories;

import com.example.appfitness.models.Notification;
import com.example.appfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    // obter todas notificações de um user
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :userId")
    List<Notification> findByReceiverId(Integer userId);

    // obter notificações não lidas
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :userId AND n.isRead = FALSE")
    List<Notification> findByReceiverIdAndUnread(Integer userId);

    // obter apenas certo tipo de notificações (nao deve ser necessário)
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :userId AND n.type = :notificationType")
    List<Notification> findByReceiverIdAndType(
            Integer userId,
           Notification.NotificationType notificationType
    );
}
