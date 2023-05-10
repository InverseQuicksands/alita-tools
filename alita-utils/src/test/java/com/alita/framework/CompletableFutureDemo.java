package com.alita.framework;


import java.util.Random;
import java.util.concurrent.*;
import java.util.function.*;


/**
 * CompletableStage 接口中有 50 多个方法，可以对 CompletableStage 进行组合、计算，方法看似很多，但可以按功能对其分类:
 * 大多数方法都有 3 种变体：
 * 1.不带 Async 方法：同步方法
 * 2.带 Async，只有一个参数：异步方法，使用默认的 ForkJoinPool.commonPool() 获取线程池
 * 3.带 Async，有两个参数：异步方法，且使用第二个参数指定的 ExecutorService 线程池
 */
public class CompletableFutureDemo {

    /**
     * 创建 CompletableFuture 实例
     */
//    @Test
    public void test1() throws Exception {
        // 创建一个具有默认结果的 CompletableFuture
        // TODO 这里需要注意一点，一旦 complete 设置成功，CompletableFuture 返回结果就不会被更改，即使后续 CompletableFuture 任务执行结束。
        CompletableFuture<String> completedFuture = CompletableFuture.completedFuture("Hello");
        System.out.println("completedFuture:" + completedFuture.get());

        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName() + "---" + "Hello World!");
        };
        // runAsync方法不支持返回值,默认将会使用公共的 ForkJoinPool.commonPool() 作为它的线程池执行异步代码，这个线程池默认线程数是 CPU 的核数
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(runnable);
        System.out.println("completableFuture1:" + completableFuture1.get());

        Supplier<String> supplier = () -> {
            System.out.println(Thread.currentThread().getName() + "---" + "Hello Supplier");
            return "Hello Supplier";
        };
        // supplyAsync可以支持返回值,默认将会使用公共的 ForkJoinPool.commonPool() 作为它的线程池执行异步代码，这个线程池默认线程数是 CPU 的核数
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(supplier);
        System.out.println("completableFuture2:" + completableFuture2.get());

        // 使用自定义线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(runnable, threadPool);
        System.out.println("completableFuture3:" + completableFuture3.get());

        CompletableFuture<String> completableFuture4 = CompletableFuture.supplyAsync(supplier, threadPool);
        System.out.println("completableFuture4:" + completableFuture4.get());
        threadPool.shutdown();

//        执行结果：
//        completedFuture:Hello
//        ForkJoinPool.commonPool-worker-1---Hello World!
//        completableFuture1:null
//        ForkJoinPool.commonPool-worker-1---Hello Supplier
//        completableFuture2:Hello Supplier
//        pool-1-thread-1---Hello World!
//        completableFuture3:null
//        pool-1-thread-2---Hello Supplier
//        completableFuture4:Hello Supplier
    }


    /**
     * whenComplete的使用.
     * <p>whenComplete方法比较复杂，当 X 线程执行了CompletableFuture.complete()方法后意味着CompletableFuture.get()
     * 已有返回值 ，这是一个十分关键时间点 A。 如果CompletableFuture.whenComplete()在时间点A 前被调用，
     * 则CompletableFuture.complete()完成后就马上调用CompletableFuture.whenComplete(),并且线程不切换。
     * 如果在时间点 A 后被用，由于调用CompletableFuture.complete()方法的线程已执行别的任务，
     * 这时CompletableFuture.whenComplete()中的任务只能由执行CompletableFuture.whenComplete()方法的线程代替完成
     */
//    @Test
    public void whenComplete() throws Exception {
        System.out.println("本方法使用的线程：" + Thread.currentThread().getName());
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        //一个5秒任务
        Supplier<String> supplier = () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String s = "supplier: " + Thread.currentThread().getName();
            System.out.println(s);
            return s;
        };

        //任务完成后的行为
        BiConsumer<Object, Throwable> action = (result, exception) -> {
            String s = "action: " + Thread.currentThread().getName();
            System.out.println(s);
        };

        //将任务交给线程池处理,任务结束会自动调用CompletableFuture.complete()方法。
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(supplier, threadPool);
        //睡1秒，此时任务还没结束，这时调用whenComplete方法，将会和supplier的执行使用相同的线程。
        Thread.sleep(1000);
        CompletableFuture<String> completableFuture2 = completableFuture1.whenComplete(action);

        //睡10秒，这时任务已结束，这时调用whenComplete方法。只能使用调用本方法的线程
        Thread.sleep(10000);
        CompletableFuture<String> completableFuture3 = completableFuture1.whenComplete(action);

        threadPool.shutdown();
//        执行结果：
//        本方法使用的线程：main
//        supplier: pool-1-thread-1
//        action: pool-1-thread-1
//        action: main
    }


    /**
     *  whenCompleteAsync的使用.
     *  <p>whenCompleteAsync的场合，就简单很多。一句话就是从线程池里面拿一个空的线程或者新启一个线程来执行回调。
     *  和执行f.complete的线程以及执行whenCompleteAsync的线程无关
     */
//    @Test
    public void whenCompleteAsync() throws InterruptedException {
        System.out.println("本方法所用线程 " + Thread.currentThread().getName());
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        //一个5秒任务
        Supplier<String> supplier = () -> {
          try {
              Thread.sleep(5000);
          } catch (Exception ex) {
              ex.printStackTrace();
          }

          String s = "supplier: " + Thread.currentThread().getName();
          System.out.println(s);
          return s;
        };

        //任务完成后的行为
        BiConsumer<Object, Throwable> action = (result, exception) -> {
            String s = "action: " + Thread.currentThread().getName();
            System.out.println(s);
        };
        //将任务交给线程池处理,任务结束会自动调用CompletableFuture.complete()方法
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(supplier, threadPool);
        //睡1秒，这时任务还没结束，这时调用whenCompleteAsync方法，将会从线程池里面拿一个空的线程或者新启一个线程来执行
        Thread.sleep(1000);
        CompletableFuture<String> completableFuture2 = completableFuture1.whenCompleteAsync(action);

        //睡10秒，这时任务已结束，whenCompleteAsync，将会从线程池里面拿一个空的线程或者新启一个线程来执行，whenCompleteAsync可以指定执行的线程池
        Thread.sleep(10000);
        CompletableFuture<String> completableFuture3 = completableFuture2.whenCompleteAsync(action, threadPool);
        threadPool.shutdown();
//        执行结果：
//        本方法所用线程 main
//        supplier: pool-1-thread-1
//        action: ForkJoinPool.commonPool-worker-1
//        action: pool-1-thread-2
    }


    /**
     * thenApply/thenApplyAsync的使用.
     * <p>当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化.
     * thenApply/thenApplyAsync 的区别和 whenComplete/whenCompleteAsync 区别是一样的
     */
//    @Test
    public void thenApply() throws Exception {
        System.out.println("本方法所用线程 " + Thread.currentThread().getName());

        //一个5秒任务
        Supplier<Integer> supplier = () -> {
          try {
              Thread.sleep(5000);
          } catch (Exception ex) {
              ex.printStackTrace();
          }
          String s = "supplier: " + Thread.currentThread().getName();
          System.out.println(s);
          return 5;
        };

        /**
         * Function<? super T,? extends U> —— T：上一个任务返回结果的类型；U：当前任务的返回值类型.
         * 一个后继任务
         */
        Function<Integer, String> function = (result) -> {
            String s = "function: " + Thread.currentThread().getName();
            System.out.println(s);
            return "结果是：" + result;
        };

        //将任务交给线程池处理,任务结束会自动调用CompletableFuture.complete()方法
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(supplier);
        //睡1秒
        Thread.sleep(1000);
        CompletableFuture<String> completableFuture2 = completableFuture1.thenApply(function);
        System.out.println(completableFuture2.get());

        //睡10秒
        Thread.sleep(10000);
        CompletableFuture<String> completableFuture3 = completableFuture1.thenApply(function);
        System.out.println(completableFuture3.get());
//        执行结果：
//        本方法所用线程 main
//        supplier: ForkJoinPool.commonPool-worker-1
//        function: ForkJoinPool.commonPool-worker-1
//        结果是：5
//        function: main
//        结果是：5
    }


    /**
     * handle/handleAsync 方法.
     * <p>handle 是执行任务完成时对结果的处理。
     * handle 方法和 thenApply 方法处理方式基本一样。不同的是 handle 是在任务完成后再执行，
     * 还可以处理异常的任务。thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法。
     */
//    @Test
    public void handle() throws ExecutionException, InterruptedException {
        Supplier<Integer> supplier = new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = 10 / 0;
                return new Random().nextInt(10);
            }
        };

        BiFunction<Integer, Throwable, Integer> biFunction = new BiFunction<Integer, Throwable, Integer>() {
            @Override
            public Integer apply(Integer param, Throwable throwable) {
                int result = -1;
                if (throwable == null) {
                    result = param * 2;
                } else {
                    System.out.println(throwable.getMessage());
                }

                return result;
            }
        };

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(supplier).handle(biFunction);
        System.out.println(completableFuture.get());
//        执行结果：
//        java.lang.ArithmeticException: / by zero
//        -1
    }


    /**
     * thenAccept/thenAcceptAsync 消费处理结果.
     * <p>接收任务的处理结果，并消费处理，无返回结果.
     */
//    @Test
    public void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return new Random().nextInt(10);
            }
        }).thenAccept(integer -> {
            System.out.println(integer);
        });
        completableFuture.get();
//        执行结果：
//        2
    }


    /**
     * thenRun/thenRunAsync 方法.
     * <p>该方法同 thenAccept 方法类似。不同的是上个任务处理完成后，并不会把计算的结果传给 thenRun 方法。
     * 只是处理玩任务后，执行 thenAccept 的后续操作.
     */
//    @Test
    public void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return new Random().nextInt(10);
            }
        }).thenRun(() -> {
            System.out.println("thenRun...");
        });
        completableFuture.get();
//        执行结果：
//        thenRun...
    }


    /**
     * thenCombine/thenCombineAsync 合并任务.
     * <p>thenCombine 会把两个 CompletionStage 的任务都执行完成后，把两个任务的结果一块交给 thenCombine 来处理。
     */
//    @Test
    public void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "hello";
            }
        });

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "world";
            }
        });

        CompletableFuture<String> completableFuture = completableFuture1.thenCombine(completableFuture2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String t, String u) {
                return t + " " + u;
            }
        });
        System.out.println(completableFuture.get());
//        执行结果：
//        hello world
    }


    /**
     * thenAcceptBoth/thenAcceptBothAsync
     * <P>当两个CompletionStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗.
     */
//    @Test
    public void thenAcceptBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        });

        CompletableFuture<Void> completableFuture = f1.thenAcceptBoth(f2, new BiConsumer<Integer, Integer>() {
            @Override
            public void accept(Integer integer, Integer integer2) {
                System.out.println("f1=" + integer + ";f2=" + integer2 + ";");
            }
        });
        completableFuture.get();
//        执行结果：
//        f2=2
//        f1=2
//        f1=2;f2=2;
    }


    /**
     * applyToEither/applyToEitherAsync
     * <p>两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的转化操作
     */
//    @Test
    public void applyToEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1 = " + i);
                return i;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2 = " + i);
                return i;
            }
        });

        CompletableFuture<Integer> completableFuture = f1.applyToEither(f2, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                System.out.println(integer);
                return integer * 2;
            }
        });
        completableFuture.get();
//        执行结果：
//        f1 = 1
//        1
    }


    /**
     * acceptEither/acceptEitherAsync方法
     * <p>两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的消耗操作。
     */
//    @Test
    public void acceptEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1=" + t);
                return t;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int t = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2=" + t);
                return t;
            }
        });
        CompletableFuture<Void> completableFuture = f1.acceptEither(f2, new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                System.out.println(t);
            }
        });
        completableFuture.get();
//        执行结果：
//        f1=0
//        0
    }

    /**
     * runAfterEither/runAfterEitherAsync 方法
     * <p>两个CompletionStage，任何一个完成了都会执行下一步的操作（Runnable）.
     */
//    @Test
    public void runAfterEither() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1 = " + i);
                return i;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2 = " + i);
                return i;
            }
        });

        CompletableFuture<Void> completableFuture = f1.runAfterEither(f2, () -> {
            System.out.println("上面有一个已经完成了。");
        });
        completableFuture.get();
//        执行结果：
//        f1 = 1
//        f2 = 1
//        上面有一个已经完成了。
    }


    /**
     * runAfterBoth/runAfterBothAsync方法.
     * <p>两个CompletionStage，都完成了计算才会执行下一步的操作（Runnable）.
     */
//    @Test
    public void runAfterBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f1 = " + i);
                return i;
            }
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f2 = " + i);
                return i;
            }
        });

        CompletableFuture<Void> completableFuture = f1.runAfterBoth(f2, new Runnable() {
            @Override
            public void run() {
                System.out.println("上面两个任务都执行完成了。");
            }
        });
        completableFuture.get();
//        执行结果：
//        f1 = 0
//        f2 = 1
//        上面两个任务都执行完成了。
    }


    /**
     * thenCompose/thenComposeAsync 方法
     * <p>thenCompose 方法允许你对两个 CompletionStage 进行流水线操作，第一个操作完成时，将其结果作为参数传递给第二个操作。
     */
//    @Test
    public void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                int i = new Random().nextInt(3);
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("f = " + i);
                return i;
            }
        }).thenCompose(new Function<Integer, CompletionStage<Integer>>() {
            @Override
            public CompletionStage<Integer> apply(Integer param) {
                return CompletableFuture.supplyAsync(new Supplier<Integer>() {
                    @Override
                    public Integer get() {
                        int t = param * 2;
                        System.out.println("t2 = "+t);
                        return t;
                    }
                });
            }
        });
        System.out.println(f.get());
//        执行结果：
//        f = 2
//        t2 = 4
//        4
    }

    /**
     *  CompletionStage
     * CompletableFuture 分别实现两个接口 Future与 CompletionStage.
     * CompletableFuture 大部分方法来自CompletionStage 接口，正是因为这个接口，CompletableFuture才有如从强大功能。
     * 想要理解 CompletionStage 接口，我们需要先了解任务的时序关系的。我们可以将任务时序关系分为以下几种：
     * ■ 串行执行关系
     * ■ 并行执行关系
     * ■ AND 汇聚关系
     * ■ OR 汇聚关系
     *
     * 串行执行关系
     * 任务串行执行，下一个任务必须等待上一个任务完成才可以继续执行。
     * CompletionStage 有四组接口可以描述串行这种关系，分别为:
     * thenApply、thenApplyAsync、thenAccept、thenAcceptAsync、thenRun、thenRunAsync、thenCompose、thenComposeAsync
     * thenApply 方法需要传入核心参数为 Function<T,R>类型。这个类核心方法为：
     * <pre>
     *  {@code  R apply(T t)}
     * </pre>
     * 所以这个接口将会把上一个任务返回结果当做入参，执行结束将会返回结果。
     * thenAccept 方法需要传入参数对象为 Consumer<T>类型，这个类核心方法为：
     * <pre>
     *  {@code  void accept(T t)}
     * </pre>
     * 返回值 void 可以看出，这个方法不支持返回结果，但是需要将上一个任务执行结果当做参数传入。
     * thenRun 方法需要传入参数对象为 Runnable 类型，这个类大家应该都比较熟悉，核心方法既不支持传入参数，也不会返回执行结果。
     * thenCompose 方法作用与 thenApply 一样，只不过 thenCompose 需要返回新的 CompletionStage。这么理解比较抽象，可以集合代码一起理解。
     *
     * AND 汇聚关系
     * AND 汇聚关系代表所有任务完成之后，才能进行下一个任务.
     * 只有任务 A 与任务 B 都完成之后，任务 C 才会开始执行。
     * CompletionStage 有以下接口描述这种关系:
     * thenCombine、thenCombineAsync、thenAcceptBoth、thenAcceptBothAsync、runAfterBoth、runAfterBothAsync、allOf。
     * thenCombine 方法核心参数 BiFunction ，作用与 Function一样，只不过 BiFunction 可以接受两个参数，而 Function 只能接受一个参数。
     * thenAcceptBoth 方法核心参数BiConsumer 作用也与 Consumer一样，不过其需要接受两个参数。
     * runAfterBoth 方法核心参数最简单，上面已经介绍过，不再介绍。
     * 这三组方法只能完成两个任务 AND 汇聚关系，如果需要完成多个任务汇聚关系，需要使用 CompletableFuture#allOf，
     * 不过这里需要注意，这个方法是不支持返回任务结果。
     *
     * OR 汇聚关系
     * 有 AND 汇聚关系，当然也存在 OR 汇聚关系。OR 汇聚关系代表只要多个任务中任一任务完成，就可以接着接着执行下一任务。
     * applyToEither、applyToEitherAsync、acceptEither、acceptEitherAsync、runAfterEither、runAfterEitherAsync、anyOf、
     * 前面三组接口方法传参与 AND 汇聚关系一致，这里也不再详细解释了。
     * 当然 OR 汇聚关系可以使用 CompletableFuture#anyOf 执行多个任务。
     *
     * 异常处理
     * 上面代码我们显示使用 try..catch 处理上面的异常。不过这种方式不太优雅，CompletionStage 提供几个方法，可以优雅处理异常。
     * exceptionally 使用方式类似于 try..catch 中 catch代码块中异常处理。
     * whenComplete 与 handle 方法就类似于 try..catch..finanlly 中 finally 代码块。
     * 无论是否发生异常，都将会执行的。这两个方法区别在于 handle 支持返回结果。
     * whenComplete、whenCompleteAsync、handle、handleAsync、exceptionally。
     *
     * JDK8 提供 CompletableFuture 功能非常强大，可以编排异步任务，完成串行执行，并行执行，AND 汇聚关系，OR 汇聚关系.
     * 不过这个类方法实在太多，且方法还需要传入各种函数式接口，新手刚开始使用会直接会被弄懵逼。这里帮大家在总结一下三类核心参数的作用:
     * ■ Function 这类函数接口既支持接收参数，也支持返回值
     * ■ Consumer 这类接口函数只支持接受参数，不支持返回值
     * ■ Runnable 这类接口不支持接受参数，也不支持返回值
     *
     */





}
