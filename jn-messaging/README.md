# Jn Messaging

## Maing differences with Spring Messaging APIs

* Generics are used along all the classes (`Message`, `MessageConverter`, etc).
* `MessageHeaders` is nullable. It is not a map and can be a strongly typed bean.
* `MessageConverter` never returns `null`. Instead, it
throws a MessageConversionException when the converter
cannot perform the conversion.
* `MessageChannel` doesn't return a `boolean` from send methods.
Instead, it must throws a `MessagingException` when the message
couldn't be sent.
* `PollableChannel` doesn't return `null` from receive methods.
Instead, it must throws a `MessagingException` when the message
couldn't be received.
* All `MessageSendingOperations`, `MessageReveicingOperations`, etc. 
send and receive according to the same behaviour as channels (i.e.
allways throwing a `MessagingException` when the message couldn't
be sent or received.
