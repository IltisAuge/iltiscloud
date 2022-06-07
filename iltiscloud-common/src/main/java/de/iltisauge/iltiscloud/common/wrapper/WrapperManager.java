package de.iltisauge.iltiscloud.common.wrapper;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.utils.SocketAddressConverter;
import de.iltisauge.iltiscloud.api.wrapper.IWrapper;
import de.iltisauge.iltiscloud.api.wrapper.IWrapperManager;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.client.ClientNetworkManager;
import lombok.Getter;

public class WrapperManager implements IWrapperManager {

	// Other
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	// Caching
	private final Map<UUID, IWrapper> wrappers = new HashMap<UUID, IWrapper>();
	
	// Database
	@Getter
	private MongoCollection<Document> wrapperCollection;
	private static final String WRAPPER_COLLECTION = "wrappers";
	private static final String WRAPPER_ID_FIELD = "wrapperId";
	private static final String ADDRESS_FIELD = "address";
	private static final String NAME_FIELD = "name";
	private static final String IS_RUNNING_FIELD = "isRunning";
	private static final String MAX_RAM_FIELD = "maxRam";
	
	// Transport
	private static final String PACKET_CHANNEL = "wrapper-manager";

	@Override
	public void initialize() {
		wrapperCollection = IltisCloud.getAPI().getDatabase().getMongoDatabase().getCollection(WRAPPER_COLLECTION);
		final FindIterable<Document> iterable = wrapperCollection.find();
		for (Document document : iterable) {
			final UUID wrapperId = UUID.fromString(document.getString(WRAPPER_ID_FIELD));
			final SocketAddress address = SocketAddressConverter.convertToInet(document.getString(ADDRESS_FIELD));
			final String name = document.getString(NAME_FIELD);
			final boolean isRunning = document.getBoolean(IS_RUNNING_FIELD);
			final Integer maxRam = document.getInteger(MAX_RAM_FIELD);
			final IWrapper wrapper = new Wrapper(wrapperId, address, name, isRunning, maxRam);
			wrappers.put(wrapperId, wrapper);
			IltisCloud.getLogger().log(Level.INFO, "Loaded wrapper '" + name + "'.");
		}
		final ClientNetworkManager networkManager = Transport.getClient().getNetworkManager();
		networkManager.addSubscriptions(PACKET_CHANNEL);
	}
	
	@Override
	public IWrapper getWrapper(UUID wrapperId) {
		lock.readLock().lock();
		try {
			return wrappers.get(wrapperId);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void createWrapper(SocketAddress address, int maxRam) {
		
	}
}
