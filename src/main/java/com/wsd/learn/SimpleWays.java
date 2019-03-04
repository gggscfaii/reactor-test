package com.wsd.learn;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sdwang on 2019/3/2.
 */
public class SimpleWays {

    public static void main(String[] args) {
        Flux<Integer> range = Flux.range(1, 10);
        range.subscribe(System.out::println,
                System.err::print,
                () -> System.out.println("done"),
                sub -> sub.request(5));

        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        range.limitRate(2).subscribe(ss);

        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                });
        flux.subscribe(System.out::println);
    }

    public static class SampleSubscriber<T> extends BaseSubscriber<T> {

        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("Subscribed");
            request(2);
        }

        public void hookOnNext(T value) {
            System.out.println(value);
            request(2);
        }
    }

}
