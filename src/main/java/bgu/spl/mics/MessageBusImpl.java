package bgu.spl.mics;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance;
	//we should have collection of Queues for Microservices
	//each MessageType should have queue of which microservices are subscribed
	private ConcurrentHashMap<Message , Queue<MicroService>> QueuesForEvents = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService , Queue<Message>> QueuesForMicroServices = new ConcurrentHashMap<>();

	//singleton DesignPattern
	public static MessageBusImpl getInstance(){
		if(instance==null){
			instance=new MessageBusImpl();
		}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		Queue<Message> QMicro=new LinkedList<>();
		QueuesForMicroServices.put(m,QMicro);
	}

	@Override
	public void unregister(MicroService m) {
		if(isRegistered(m)){
			QueuesForMicroServices.remove(m);
		}
		//ask lotam about whether the queue should be empty when doing unregister??!!
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!isRegistered(m)) throw new IllegalStateException();
		Queue<Message> currQ=QueuesForMicroServices.get(m);
		while(currQ.isEmpty()){
			wait();
			//where notify should be?
		}
		return currQ.remove();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isRegistered(MicroService m){
		return QueuesForMicroServices.get(m)!=null;
	}

	

}
