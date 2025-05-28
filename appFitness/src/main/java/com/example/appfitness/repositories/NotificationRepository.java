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
    @Query("SELECT n FROM Notification n WHERE n.receiver = :recipient")
    List<Notification> findByRecipient(User receiver);

    // obter notificações não lidas
    @Query("SELECT n FROM Notification n WHERE n.receiver = :recipient AND n.read = FALSE")
    List<Notification> findByRecipientAndIsReadFalse( User receiver);

    // obter apenas certo tipo de notificações (nao deve ser necessário)
    @Query("SELECT n FROM Notification n WHERE n.receiver = :recipient AND n.type = :type")
    List<Notification> findByRecipientAndType(User receiver, Notification.NotificationType type);

}
