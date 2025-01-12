# Websocket Protocol

JSON format (for both serverbound and clientbound):

```json
{
    "type": "...",
    "payload": {}
}
```

You are expected to compress your messages with gzip when sending messages. Recieved messages are also gzipped.

## Types (common keys)

If a property is described as a "common key" in a comment, it is a property in the list below.

**actioner**: the minecraft uuid of the user who did the action.

**uuid**: the minecraft uuid of the user connected

**display**: the minecraft username of the user connected

**x**: index of the character the cursor is on. 0 is before the first char, 1 is after the first char, 2 is after the second char, and so on

**y**: line the cursor is on. starts at 1 (the first line)

**timeout**: number of seconds until this client disconnects due to an expried token

## Disconnect codes

* 1000: room closed by admin (clientbound), user left room (serverbound)
* 1001: server is shutting down or restarting (clientbound), tab/app/etc closed (serverbound)
* 1009: message is too large (bidirectional)
* 1011: unresolved error (bidirectional)
* 1014: bad gateway (clientbound)
* 3000: bad authorization header, or authorization expired (clientbound)
* 3003: no permissions (clientbound)
* 3008: keepalive timeout (bidirectional)

## Handshake

Use the following headers when connecting to the wss:
* `Authorization`: `Bearer ___` where `___` is the JWT given to the app by the blazing api.
* `Blazing-Computer-Id`: set to the id of computer that you want to join the room of

## Keepalive / heartbeat

Whenever you recieve a `PING`, you should respond with a `PONG`: this is part of the websocket protocol, not this protocol.

Your websocket client may already do this for you. If you're unsure, check your client's documentation.

## User list update: `usrupd` (clientbound)

**Clientbound**: Sent when a user joins or leaves. The format is as follows:

```json
{
    "actioner": "84a33aad-bacf-40b4-a1b5-910300e7e3fc", // common key
    "type": true // true if join, false if leave
}
```


## tbd
