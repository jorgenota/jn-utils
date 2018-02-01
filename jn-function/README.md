# Jn Function

Utilities to deal with functions, consumers, etc. as well as running code with retries.

## Functions, Consumers, etc. with checked exceptions

This library includes helper interfaces that are extensions of `Function`, `Consumer`, `Suplier`, etc. that allow
throwing checked exceptions. These interfaces help to handle checked exceptions with lambdas.

For example, `Class.forName` method throws a checked exception and you can use it inside as a lambda
by casting it to a `FunctionWithExceptions`:
```Java
Stream.of("java.lang.Object", "java.lang.Integer", "java.lang.String")
        .map((FunctionWithExceptions<String,Class<?>,Exception>)Class::forName)
        .collect(Collectors.toList());
```

You can also define a consumer that throws a checked exception and use it later (as if it was a usual `Consumer`).
```Java
ConsumerWithExceptions<Integer,IOException> consumer = (x) -> {throw new IOException();};
...
consumer.accept();
```

## Retriers

The module also provides a general purpose class for retrying arbitrary Java code with specific stop, retry, and
exception handling capabilities.

A minimal example could be:
```Java
Retrier defaultRetrier = RetrierBuilder.getDefaultRetrier();

Callable<Boolean> callable = ...;
try {
    defaultRetrier.call(callable);
} catch (RetryException e) {
    e.printStackTrace();
} catch (ExecutionException e) {
    e.printStackTrace();
}
```
This code is using a default retrier that will retry the callable if its code throws any exception using the default strategies (with no waiting time
between attempts. It will stop retrying after 10 unsuccessful attempts, throwing a `RetryException`. An
`ExecutionException` would be thrown in case an unsuccessful attempt matched a configured fail predicate... but
in this example this won't be the case because no fail predicate has been set when building the
retrier.

You can build a retrier using specific wait strategies, stop strategies and fail predicates. 
For example: 
```Java
Retrier myRetrier = RetrierBuilder.newBuilder()
                            .withWaitStrategy(WaitStrategies.fixedWait(100L))
                            .withStoptStrategy(StopStrategies.stopAfterDelay(800L))
                            .failIfExceptionOfType(NullPointerException.class)
                            .build();

BiFunction<Integer, Integer, String> sumToString = (x, y) -> String.valueOf(x + y);
try {
    myRetrier.apply(sumToString(4,8));
} catch (RetryException e) {
    e.printStackTrace();
} catch (ExecutionException e) {
    e.printStackTrace();
}
```
This coce uses a retrier that will retry if the code of the bifunction throws any exception but `NullPointerException`.
It will wait for 100 milliseconds between retries and will stop retrying after 800 milliseconds
(after that time without succeding, it will throw a `RetryException`). If the code throws a
`NullPointerException`, the retrier will immediately fail throwing an `ExecutionException`.


Once it's built, you can use a retrier whenever you want to execute callables, functions, suppliers, etc.

### Wait Strategies

Wait strategies allow you to define how much time (if any) the retrier will wait between attempts. Some
predefined wait strategies are:
* `WaitStrategies.noWait()`
* `WaitStrategies.fixedWait(sleepTime)`
* `WaitStrategies.randomWait(minTime, maxTime)`
* `WaitStrategies.incrementingWait(initialSleepTime, increment)`
* `WaitStrategies.exponentialWait(maximumTime)`

And you can build your own...

### Stop Strategies

You can also define stop strategies to decide when a retrier is going to stop retrying a code that is
not succeeding. These are some predefined stop strategies:
* `StopStrategies.neverStop()`
* `StopStrategies.stopAfterAttempt(attemptsNumber)`
* `StopStrategies.stopAfterDelay(delay)`

### Fail Predicate

A fail predicate is a `Predicate<FailedAttempt>` that will decide if a retrier should fail (instead of retrying) when
the code throws an exception. You can use `RetrierBuilder` to combine one of more predefined fail predicates:
* failIfException()
* failIfRuntimeException())
* failIfExceptionOfType(exceptionClass)
* failIfException(Predicate<Throwable> exceptionPredicate)

## Functions, Consumers, etc. with retries

Some helper classes have been provided to help building functions, consumers, etc. that internally
handle retries: `FunctionWithRetries`, `ConsumerWithRetries`, `CallableWithRetries`, ...

For example:
```Java
FunctionWithRetries<Integer, Integer, NullPointerException> function = new FunctionWithRetries<Integer, Integer, NullPointerException>() {
     @Override
     Integer applyWithRetries(Integer o) throws NullPointerException {
         return o + 3;
      }
};

// You can use the retrying function as any "normal" Function
Integer result = function.apply(2);
```
This code defines a function that adds 3 to the received value... and does it applying retries in case of exception :)
It uses a default retrier and also a default `RetryExceptionHandler` that, in case of ExecutionException
o RetryException, just logs the exception and finish the work (returning null).


You can also define your own retrier and `RetryExceptionHandler`. For example:
```Java
ConsumerWithRetries consumerWithRetries = new ConsumerWithRetries<String, IOException>() {
    @Override
    void acceptWithRetries(String o) throws IOException {
        // Your code here
    }

    @Override
    protected RetryExceptionHandler getRetryExceptionHandler() {
        // Provide your RetryExceptionHandler to decide how to handle
        // a RetryException and an ExecutionException
    }

    @Override
    protected Retrier getRetrier() {
        // Provide a retrier
    }
};

// You can use the retrying consumer as any "normal" Consumer
consumerWithRetries.accept("2");
```
This code shows how to build a Consumer that retries the code if unsuccessful providing your own retrier and
`RetryExceptionHandler`.