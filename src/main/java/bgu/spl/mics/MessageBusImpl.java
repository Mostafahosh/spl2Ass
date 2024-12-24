package bgu.spl.mics;

import java.util.LinkedList;
import java.util.Map;
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
	private ConcurrentHashMap<Message , Queue<MicroService>> QueuesForMessages = new ConcurrentHashMap<>();
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

		Queue<MicroService> MsgType = QueuesForMessages.get(type);
		MsgType.add(m);

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
			Queue<MicroService> queueOfBroad = QueuesForMessages.get(type);
			queueOfBroad.add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated methgod stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (Map.Entry<MicroService, Queue<Message>> entry : QueuesForMicroServices.entrySet()) {
			Queue<Message> queue = entry.getValue();
			queue.add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (QueuesForMessages.get(e).size() == 0 ){return null;}
		Queue<MicroService> currQofEvent = QueuesForMessages.get(e);

		//round-robin
		MicroService currMicro = currQofEvent.poll(); //should never return null because of the if above
		currQofEvent.add(currMicro);
		//

		Queue<Message> currQofMicro = QueuesForMicroServices.get(currMicro);
		currQofMicro.add(e);
		//should wait until its event take proccess time ?

//		e.getClass().c


	return null;}

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
