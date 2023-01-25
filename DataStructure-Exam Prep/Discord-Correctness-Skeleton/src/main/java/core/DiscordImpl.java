package core;

import models.Message;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordImpl implements Discord {

    private final Map<String, Message> messageMap;

    private final Map<String, Set<Message>> messagesByChannel;

    public DiscordImpl() {
        this.messageMap = new LinkedHashMap<>();
        this.messagesByChannel = new HashMap<>();
    }

    @Override
    public void sendMessage(Message message) {
        this.messageMap.put(message.getId(), message);
        if (!this.messagesByChannel.containsKey(message.getChannel())) {
            this.messagesByChannel.put(message.getChannel(), new LinkedHashSet<>());
        }
//        Set<Message> messages = this.messagesByChannel.get(message.getChannel());
//        messages.add(message);
//        this.messagesByChannel.put(message.getChannel(),messages);

        this.messagesByChannel.get(message.getChannel()).add(message);
        // FIXME: 25.1.2023 г. add to channel set
    }

    @Override
    public boolean contains(Message message) {
        return this.messageMap.containsKey(message.getId());
    }

    @Override
    public int size() {
        return this.messageMap.size();
    }

    @Override
    public Message getMessage(String messageId) {
        Message result = this.messageMap.get(messageId);
        if (result == null) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    @Override
    public void deleteMessage(String messageId) {
        Message msToRemove = this.messageMap.remove(messageId);
        if (msToRemove == null) {
            throw new IllegalArgumentException();
        }
        this.messagesByChannel.get(msToRemove.getChannel()).remove(msToRemove);
    }

    @Override
    public void reactToMessage(String messageId, String reaction) {
        Message message = this.getMessage(messageId);
        message.getReactions().add(reaction);
    }

    @Override
    public Iterable<Message> getChannelMessages(String channel) {
//        List<Message> result = this.messageMap.values()
////                .stream()
////                .filter(m -> m.getChannel()
////                        .equals(channel))
////                .collect(Collectors.toList());
////        if (result.isEmpty()) {
////            throw new IllegalArgumentException();
////        }
////        return result;
        Set<Message> allMessages = this.messagesByChannel.get(channel);
        if (allMessages == null) {
            throw new IllegalArgumentException();
        }
        return allMessages;
    }

    @Override
    public Iterable<Message> getMessagesByReactions(List<String> reactions) {
        return this.messageMap.values().stream()
                .filter(m -> new HashSet<>(m.getReactions()).containsAll(reactions))
                .sorted((l, r) -> {
                    int lSize = l.getReactions().size();
                    int rSize = r.getReactions().size();
                    if (lSize != rSize) {
                        return rSize - lSize;
                    }
                    return l.getTimestamp() - r.getTimestamp();
                }).collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getMessageInTimeRange(Integer lowerBound, Integer upperBound) {
        return this.messageMap.values().stream()
                .filter(m -> lowerBound <= m.getTimestamp() && m.getTimestamp() <= upperBound)
                .sorted((l, r) -> {
                    int lChannelMsCount = this.messagesByChannel.get(l.getChannel()).size();
                    int rChannelMsCount = this.messagesByChannel.get(r.getChannel()).size();
                    return rChannelMsCount - lChannelMsCount;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getTop3MostReactedMessages() {
        return this.messageMap.values().stream()
                .sorted((l,r) -> r.getReactions().size() - l.getReactions().size())
                .limit(3).collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent() {
        return this.messageMap.values().stream().sorted((l, r) -> {
            int lReactionsSize = l.getReactions().size();
            int rReactionsSize = r.getReactions().size();
            if (lReactionsSize != rReactionsSize) {
                return rReactionsSize - lReactionsSize;
            }
            if (l.getTimestamp() != r.getTimestamp()) {
                return l.getTimestamp() - r.getTimestamp();
            }
            return l.getContent().length() - r.getContent().length();
        }).collect(Collectors.toList());
    }
}
