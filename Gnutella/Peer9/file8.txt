
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/*
 * Copyright © 2018 by Abhishek Kumar
 *
   All rights reserved. No part of this code may be reproduced, distributed, or transmitted 
   in any form or by any means, without the prior written permission of the programmer.  
 *
 * 
   CS 5352 Advanced Operating Systems and Design
 * 
 * */
public class ProdConsAOS {
	public static int maxTotalItems = 0;
	public static final int BUFFER_SIZE = 8;
	public static int itemIndex = 1;
	public static Integer itemConsumed = 0;
	

	public static void main(String[] args) {
	
		Scanner sc = new Scanner(System.in);
		// Colored output for GitBash
		//System.out.println("\033[92;1mProducer Consumer using MultiThreading\033[0m: \033[36;1;2mCS5352 Programming Project 1\033[0m");
		System.out.println("Producer Consumer using MultiThreading, CS5352 Programming Project 1");
		System.out.println();
		//Take user i/p for number of producers
		System.out.println("Enter number of producer threads: ");  
			int p = sc.nextInt();
		//Take user i/p for number of consumers
		System.out.println("Enter number of consumer threads: "); 
			int c = sc.nextInt();

		sc.close();
		// The maximum number of items produced depends on the number of producer threads 
		// Given: One producer produces 64 items
		maxTotalItems = p*64;
		// Passing the actual parameters to the constructor start
		start(p, c);
		//System.out.println("Exiting Main Thread");
	}

	private static void start(int p, int c) {
		// Making p producers and c consumers using Thread class
		Thread[] producers = new Thread[p];
		Thread[] consumers = new Thread[c];
		// Declaring the lock 
		Queue<Integer> queue = new LinkedList<>();

		for(int i=0; i < producers.length; i++) {
			producers[i] = new Thread(new Producer(queue));
			}

		for(int i=0; i < consumers.length; i++) {
			consumers[i] = new Thread(new Consumer(queue));
			}
		// Creating one thread per call stack 
		for(Thread consumer : consumers) {
			consumer.start();
			}
		// Creating one thread per call stack 
		for(Thread producer : producers) {
			producer.start();
			}
		}
	}

class Producer implements Runnable {
	Queue<Integer> queue;
	// instance count to check that every producer thread produces a maximum of 64 items
	private int itemIndexNew = 1;
	
	public Producer(Queue<Integer> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		
		while(itemIndexNew <= 64) {
			synchronized(queue) {

				while(queue.size() == ProdConsAOS.BUFFER_SIZE) {
					try {
						System.out.println("Buffer is FULL: Producer in Wait Pool. Notify Consumer Thread.");
						queue.wait();
						if(ProdConsAOS.itemIndex > ProdConsAOS.maxTotalItems) {return;}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				queue.offer(itemIndexNew);
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("|PRODUCER " + Thread.currentThread().getName() +" scheduled by JVM| -> "  +"Item#- " + itemIndexNew +" Total Count- " +ProdConsAOS.itemIndex);
				itemIndexNew++;
				ProdConsAOS.itemIndex++;
				queue.notify();
			}
		}
	}
}

class Consumer implements Runnable {
	Queue<Integer> queue;
	public Consumer(Queue<Integer> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		while(ProdConsAOS.itemConsumed < ProdConsAOS.maxTotalItems) {
			synchronized(queue) {
				while(queue.size() == 0) {
					try {
						System.out.println("Buffer is EMPTY: Consumer in Wait Pool. Notify Consumer Thread.");
						queue.wait();
						if(ProdConsAOS.itemConsumed >= ProdConsAOS.maxTotalItems) return;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int num = queue.poll();
				ProdConsAOS.itemConsumed++;
				System.out.println("|CONSUMER "+ Thread.currentThread().getName() +" scheduled by JVM| -> " +"Item#- " +num +" Total Count- " +ProdConsAOS.itemConsumed);
				queue.notify();
			}
		}
	}
}