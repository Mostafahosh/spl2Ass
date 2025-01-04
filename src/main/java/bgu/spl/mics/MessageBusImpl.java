package bgu.spl.mics;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	//SingletonHolder?? because maybe 2 threads will be in line if(instance==null){...}!
	//we need to use concurrentLinkedQueue

	private static MessageBusImpl instance;
	//we should have collection of Queues for Microservices
	//each MessageType should have queue of which microservices are subscribed
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> micros = new ConcurrentHashMap<>();
	private ConcurrentHashMap< Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventMicro = new ConcurrentHashMap<>();
	private ConcurrentHashMap< Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadMicro = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Event>>> microEvent = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService , ConcurrentLinkedQueue<Class<? extends Broadcast>>> microBroad = new ConcurrentHashMap<>();//must be Blocking queue
	private ConcurrentHashMap<Event , Future> event_Future = new ConcurrentHashMap<>();


	//singleton DesignPattern - need to be changed
	public static MessageBusImpl getInstance(){
		if(instance==null){
			instance=new MessageBusImpl();
		}
		return instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventMicro.putIfAbsent(type, new ConcurrentLinkedQueue<>()); //initialize a queue for Event type if not initialized before
		microEvent.putIfAbsent(m, new ConcurrentLinkedQueue<Class<? extends Event>>()); //initialize a queue for MicroService m if not initialized before

		synchronized (m) {
			ConcurrentLinkedQueue<Class<? extends Event>> eventQ = microEvent.get(m);
			if (eventQ != null) {
				eventQ.add(type);
			}
		}

		synchronized (type) {
			ConcurrentLinkedQueue<MicroService> microQ = eventMicro.get(type);
			if (microQ != null) {
				microQ.add(m);
			}
		}
	}


		@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
			broadMicro.putIfAbsent(type, new ConcurrentLinkedQueue<>()); //initialize a queue for Broad type if not initialized before
			microBroad.putIfAbsent(m, new ConcurrentLinkedQueue<Class<? extends Broadcast>>()); //initialize a queue for MicroService m if not initialized before

			synchronized (m) {
				ConcurrentLinkedQueue<Class<? extends Broadcast>>  queueOfBroad = microBroad.get(m);
				if (queueOfBroad != null) {
					queueOfBroad.add(type);
				}
			}

			synchronized (type){
				ConcurrentLinkedQueue<MicroService> Q = broadMicro.get(type);
				if (Q != null){
					Q.add(m);
				}
			}
		}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = event_Future.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (b.getClass()) {
			//System.out.println("im before the if statement - MsgBus line 90");
			if (broadMicro.containsKey(b.getClass())) {
				//System.out.println("im in the if statement - MsgBus line 92");

				ConcurrentLinkedQueue<MicroService> micro = broadMicro.get(b.getClass());
				//System.out.println("the sizeOF TickBroadCAst Q is: " + micro.size());
				for (MicroService microService : micro) {
					//System.out.println("im " + microService.getName() + " subscribed to TickBroadCast");
					LinkedBlockingQueue<Message> Q = micros.get(microService);

					boolean sign = Q != null;
					//System.out.println("is Q != null: " + sign);

					if (Q != null) {
						Q.add(b);
					}
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();

		if (eventMicro.get(e.getClass()) == null || !eventMicro.containsKey(e.getClass())){return null;}

		event_Future.put(e,future);
		ConcurrentLinkedQueue<MicroService> currQofEvent = eventMicro.get(e.getClass());

		if (currQofEvent == null){return null;} //if no queue then no one has registered to it yet, or already unregistered


		//round-robin
		MicroService currMicro = currQofEvent.poll();//should never return null because of the if above
		if(currMicro==null){return null;} //there's no microService to proccess the event
		currQofEvent.add(currMicro);
		//

synchronized (currMicro){
		LinkedBlockingQueue<Message> Q = micros.get(currMicro);
		if (Q == null) {
			return null;
		}
		Q.add(e);}

	return future;}

	@Override
	public void register(MicroService m) {
		micros.putIfAbsent(m, new LinkedBlockingQueue<>());

	}

	@Override
	public void unregister(MicroService m) {
		if (micros.containsKey(m)) {
			LinkedBlockingQueue<Message> q;

			if (microEvent.containsKey(m)) {
				ConcurrentLinkedQueue<Class<? extends Event>> q3 = microEvent.get(m);
				for (Class<? extends Event> type : q3) {
					synchronized (type) {
						eventMicro.get(type).remove(m);
					}
				}
				microEvent.remove(m);
			}
			synchronized (m) {
				if (microBroad.containsKey(m)) {
					ConcurrentLinkedQueue<Class<? extends Broadcast>> q2 = microBroad.get(m);
					for (Class<? extends Broadcast> type : q2) {
						synchronized (type) {
							broadMicro.get(type).remove(m);
						}
					}
					microBroad.remove(m);
				}
				q = micros.remove(m);
				if (q == null) {
					return;
				}
			}
			while (!q.isEmpty()) {
				Message message = q.poll();
				if (message != null) {
					Future<?> future = event_Future.get(message);
					if (future != null) {
						future.resolve(null);
					}
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m)  {
//		if(!isRegistered(m)) throw new IllegalStateException();
//		LinkedBlockingQueue<Message> currQ = micros.get(m);
//		while(currQ.isEmpty()){
//			try {
//				wait();
//			}
//			//where notify should be?
//			catch(InterruptedException ignored){}
//		}
//		return currQ.remove();



		LinkedBlockingQueue<Message> q = micros.get(m);
		if (q == null) {
			throw new IllegalArgumentException("MicroService is not registered");
		}
		Message msg = null;
		synchronized (q) {
			try {
				msg = q.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isRegistered(MicroService m){
		return microEvent.get(m)!=null;
	}


	}

	


