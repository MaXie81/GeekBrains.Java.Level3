package network;

import ru.geekbrains.messages.MessageDTO;

/**
 * Project java_core_l2
 *
 * @Author Alexander Grigorev
 * Created 08.03.2021
 * v1.0
 * Абстракция для обозначения класса, который обрабатывает полученные из сети сообщения
 */
public interface MessageProcessor {
    void receiveMessage(MessageDTO dto);
}
