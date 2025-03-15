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

**offset**: index of the character the cursor is on. 0 is before the first char, 1 is after the first char, 2 is after the second char, and so on.
(newlines are counted as characters)

**selection**: the number of characters selected, either to the left (negative) or to the right (positive). purely visible for the users, except
for when writing text. for example, if the document text is `hello world`, `offset` is 6, and `selection` is 5, then the user has selected `world`.
set to `0` to deselect.

**timeout**: number of seconds until this client disconnects due to an expried token

No value can ever be null, and no key is optional, except when stated otherwise.



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

**Clientbound**: sent when a user joins or leaves. The format is as follows:

```json
{
    "actioner": "84a33aad-bacf-40b4-a1b5-910300e7e3fc", // common key
    "type": true // true if join, false if leave
}
```



## Full state sync: `sync` (clientbound, serverbound->clientbound)

**Clientbound**: contains full document data about the current state:

```json
{
    "actioner": "84a33aad-bacf-40b4-a1b5-910300e7e3fc", // common key
    "document": "...", // base64 encoded, gzip compressed, document contents
    "users": { // includes current user
        "84a33aad-bacf-40b4-a1b5-910300e7e3fc": 10 // actioner -> offset
    }
}
```

**Serverbound**: send with empty payload to request sync.



## Move cursor: `move` (serverbound, clientbound)

**Serverbound**: send this to move your cursor to a new position, or to change your selection:

```json
{
    "offset": 10, // common key
    "selection": 5 // common key
}
```

**Clientbound**: sent when a user moves their cursor. format is same as serverbound, but with an `actioner` common key.



## Write text: `write` (serverbound, clientbound)

**Serverbound**: send a payload with these contents to write text:

```json
{
    "offset": 10, // common key
    "text": "hello world", // common key
    "selection": 0 // common key
}
```

Setting `selection` to a non-zero value replaces text instead of inserting it.

It is expected that you combine many writes into a single message. For example, instead of sending `h`, `e,` `l`, `l`, `o` seperatley, wait until
the user stops typing, then send a single `write` message with the full text.

Note that `offset` and `selection` do not need to match where the server thinks your cursor is positioned at. 

**Clientbound**: when text is edited. same as serverbound, but with the `actioner` common key added.

It is expected that for both serverbound and clientbound packets, the client reacts by moving the cursor to either the end of the written text
(when `selection` is zero), or before the written text (when `selection` is negative), or after the written text (when `selection` is positive).



## 