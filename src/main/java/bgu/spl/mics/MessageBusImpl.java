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
	//SingletonHolder?? because maybe 2 threads will be in line if(instance==null){...}!
	private static MessageBusImpl instance;
	//we should have collection of Queues for Microservices
	//each MessageType should have queue of which microservices are subscribed
	private ConcurrentHashMap<Message, Queue<MicroService>> msgMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService , Queue<Message>> microMap = new ConcurrentHashMap<>();//must be Blocking queue
	private ConcurrentHashMap<Event , Future> event_Future = new ConcurrentHashMap<>();


	//singleton DesignPattern
	public static MessageBusImpl getInstance(){
		if(instance==null){
			instance=new MessageBusImpl();
		}
		return instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		Queue<MicroService> MsgType = msgMap.get(type);
		MsgType.add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
			Queue<MicroService> queueOfBroad = msgMap.get(type);
			queueOfBroad.add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = event_Future.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		//check if null ??
		for (Map.Entry<MicroService, Queue<Message>> entry : microMap.entrySet()) {
			Queue<Message> queue = entry.getValue();
			queue.add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();

		if (msgMap.get(e) == null || !msgMap.containsKey(e)){return null;}
		Queue<MicroService> currQofEvent = msgMap.get(e);

		if (currQofEvent == null){return null;}
		event_Future.put(e,future);

		//round-robin
		MicroService currMicro = currQofEvent.poll();//should never return null because of the if above
		if(currMicro==null){return null;} //there's no microService to proccess the event
		currQofEvent.add(currMicro);
		//

		Queue<Message> currQofMicro = microMap.get(currMicro); //Q contains the events of the currMicroService
		currQofMicro.add(e);
		//should wait until its event take proccess time ?
		//should do syncronized
	return future;}

	@Override
	public void register(MicroService m) {
		Queue<Message> QMicro=new LinkedList<>();
		microMap.put(m,QMicro);
	}

	@Override
	public void unregister(MicroService m) {
		if(isRegistered(m)){
			microMap.remove(m);
		}
		for (Map.Entry<Message, Queue<MicroService>> entry : msgMap.entrySet()) {
			Queue<MicroService> queue = entry.getValue();
			queue.remove(m);
		}
		//ask lotam about whether the queue should be empty when doing unregister??!!
	}

	@Override
	public Message awaitMessage(MicroService m)  {
		if(!isRegistered(m)) throw new IllegalStateException();
		Queue<Message> currQ = microMap.get(m);
		while(currQ.isEmpty()){
			try {
				wait();
			}
			//where notify should be?
			catch(InterruptedException ignored){}
		}
		return currQ.remove();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isRegistered(MicroService m){
		return microMap.get(m)!=null;
	}

	

}
