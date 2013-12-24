package gaobq.example.mqttdemo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	/**
	 * 线程池中最大线程数
	 */
	private static final int THREAD_MAX_NUM = 6;

	private ExecutorService service;

	private ThreadPool() {
		// 获取可运行的线程数量
		int num = Runtime.getRuntime().availableProcessors();
		// 线程池中线程数量暂定为5个
		service = Executors.newFixedThreadPool(THREAD_MAX_NUM);
	}

	private static ThreadPool threadPool;

	public synchronized static ThreadPool getInstance() {
		if (threadPool == null) {
			threadPool = new ThreadPool();
		}
		return threadPool;
	}

	public void addTask(Runnable runnable) {
		service.execute(runnable);
	}

	public void cancelAllTasks() {
		service.shutdownNow();
		service = Executors.newFixedThreadPool(THREAD_MAX_NUM);
	}

}
