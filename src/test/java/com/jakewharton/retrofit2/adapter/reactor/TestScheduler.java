package com.jakewharton.retrofit2.adapter.reactor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;

final class TestScheduler implements Scheduler {
  private final Deque<Runnable> tasks = new ArrayDeque<>();

  void triggerActions() {
    while (!tasks.isEmpty()) {
      tasks.removeFirst().run();
    }
  }

  @Override public Disposable schedule(Runnable task) {
    return createWorker().schedule(task);
  }

  @Override public Worker createWorker() {
    return new Worker() {
      private final List<Runnable> workerTasks = new ArrayList<>();

      @Override public Disposable schedule(Runnable task) {
        workerTasks.add(task);
        tasks.add(task);
        return () -> tasks.remove(task);
      }

      @Override public void dispose() {
        tasks.removeAll(workerTasks);
      }
    };
  }
}
