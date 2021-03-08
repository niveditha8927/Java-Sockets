# Java-Sockets

Uses connection-oriented communication and implements Multi-Threaded Multi-
Session Server concept.

Implements a asynchronous text based communication protocol between Server and Clients.

1. All messages are case-insensitive and framed by the two characters ’<’ and ’>’.
Bytes outside a message have to be ignored.
2. A valid message starts with the character ’<’, followed by a two digit integer indicating
the total message length “00”, a colon ’:’, an actual message content “...”, the
ending character ’>’ (e.g., a valid message is <07:OK>)
3. A valid message content is a single integer value, a calculation operator “ADD”,
“SUB”, “MUL”, or “RES”, or an info operator “RDY”, “OK”, “ERR’, or “FIN”’.
4. Multiple message contents can be concatenated in a valid message content by separating
each content by one or more whitespace. Leading and trailing whitespace
characters have to be ignored. For instance, a valid message content from a client
is <16:ADD 23 9 -1>, <13: SUB 10 >, or <19:MUL 2 SUB 13 >.
5. After a client has connected to the server, the server sends the content “RDY” to
the client, if it is ready to receive requests from this client (e.g., <08:RDY>).
6. For each received message, the server sends immediately a response with the content
“OK”. After the server has processed every message content from a received message,
it sends the content “FIN” to the client.
7. If a valid content equals to the operator “ADD”, “SUB”, or “MUL”, the server-side
calculation operator is changed to ADDing, SUBtracting, or MULtiplying by values.
8. If a valid content equals to an integer value, the value is – based on the current
calculation operator – added to, subtracted from, or multiplied with the result.
9. Each valid content (e.g, “ADD”, “SUB”, “MUL”, or an integer value) is acknowledged
to the client with the content “OK” followed by a single whitespace character
and the valid content.
10. If a valid content equals to the operator “RES”, the current calculation result is
sent to the client with “OK” followed by a single whitespace character, the operator
“RES”, another single whitespace character and the current calculation value.
11. Each invalid content (e.g., “DS1”) is acknowledged with “ERR” followed by a single
whitespace character and the invalid content. Invalid contents are not processed.
12. A session is only terminated when the client closes the connection.
