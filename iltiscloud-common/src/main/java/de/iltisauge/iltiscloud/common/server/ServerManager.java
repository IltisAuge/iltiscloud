package de.iltisauge.iltiscloud.common.server;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.server.IServer;
import de.iltisauge.iltiscloud.api.server.IServerManager;
import de.iltisauge.iltiscloud.api.server.ServerPlatform;
import de.iltisauge.iltiscloud.api.server.ServerType;
import de.iltisauge.iltiscloud.api.utils.SocketAddressConverter;
import de.iltisauge.iltiscloud.common.server.packets.UpdateServerPacket;
import de.iltisauge.iltiscloud.common.server.packets.UpdateServerPacket.UpdateServerAction;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.client.ClientNetworkManager;
import de.iltisauge.transport.network.IMessageEvent;

public class ServerManager implements IServerManager {

	// Other
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	// Caching
	private Map<UUID, IServer> servers = new HashMap<UUID, IServer>();
			
	// Database
	private MongoCollection<Document> serverCollection;
	private static final String SERVER_COLLECTION = "servers";
	private static final String SERVER_ID_FIELD = "serverId";
	private static final String ADDRESS_FIELD = "address";
	private static final String NAME_FIELD = "name";
	private static final String IS_RUNNING_FIELD = "isRunning";
	private static final String WRAPPER_ID_FIELD = "wrapperId";
	private static final String SERVER_TYPE_FIELD = "serverType";
	private static final String SERVER_PLATFORM_FIELD = "serverPlatform";
	private static final String MAX_RAM_FIELD = "maxRam";
	
	// Transport
	private static final String PACKET_CHANNEL = "server-manager";
	
	@Override
	public void initialize() {
		serverCollection = IltisCloud.getAPI().getDatabase().getMongoDatabase().getCollection(SERVER_COLLECTION);
		final FindIterable<Document> iterable = serverCollection.find();
		for (Document document : iterable) {
			final UUID serverId = UUID.fromString(document.getString(SERVER_ID_FIELD));
			servers.put(serverId, getServerFromDB(serverId));
		}
		final ClientNetworkManager networkManager = Transport.getClient().getNetworkManager();
		networkManager.addSubscriptions(PACKET_CHANNEL);
		networkManager.registerCodec(UpdateServerPacket.class, UpdateServerPacket.CODEC);
		networkManager.registerEvent(UpdateServerPacket.class, new IMessageEvent<UpdateServerPacket>() {
			
			@Override
			public void onReceived(UpdateServerPacket message) {
				System.out.println("UpdateServerPacket received: " + message.toString());
				final UpdateServerAction action = message.getAction();
				final UUID serverId = message.getServerId();
				if (action.equals(UpdateServerAction.CREATE) || action.equals(UpdateServerAction.UPDATE_DATA)) {
					final IServer server = getServerFromDB(serverId);
					servers.put(serverId, server);
					return;
				}
				if (action.equals(UpdateServerAction.DELETE)) {
					servers.remove(serverId);
				}
			}
		});
	}
	
	private void sendUpdateServerPacket(UUID serverId, UpdateServerAction action, @Nullable String updatedField) {
		final UpdateServerPacket packet = new UpdateServerPacket(serverId, action, updatedField);
		packet.setReceiveSelf(false);
		packet.send(PACKET_CHANNEL);
	}
	
	@Override
	public IServer getServer(UUID serverId) {
		lock.readLock().lock();
		try {
			return servers.get(serverId);
		} catch (Exception exception) {
		} finally {
			lock.readLock().unlock();
		}
		return null;
	}

	@Override
	public List<IServer> getServers() {
		lock.readLock().lock();
		try {
			return new ArrayList<IServer>(servers.values());
		} catch (Exception exception) {
		} finally {
			lock.readLock().unlock();
		}
		return new ArrayList<IServer>();
	}
	
	@Override
	public void createServer(UUID serverId, SocketAddress address, String name, boolean isRunning, ServerType serverType, ServerPlatform serverPlatform, int maxRam, UUID wrapperId) {
		final MongoDatabase database = IltisCloud.getAPI().getDatabase();
		final MongoCollection<Document> serverCollection = database.getMongoDatabase().getCollection("servers");
		final Document doc = new Document();
		doc.put(SERVER_ID_FIELD, serverId.toString());
		doc.put(ADDRESS_FIELD, address.toString());
		doc.put(NAME_FIELD, name);
		doc.put(IS_RUNNING_FIELD, isRunning);
		doc.put(WRAPPER_ID_FIELD, wrapperId.toString());
		doc.put(SERVER_TYPE_FIELD, serverType.name());
		doc.put(SERVER_PLATFORM_FIELD, serverPlatform.name());
		doc.put(MAX_RAM_FIELD, maxRam);
		serverCollection.insertOne(doc);
		lock.writeLock().lock();
		try {
			servers.put(serverId, new Server(serverId, address, name, isRunning, wrapperId, serverType, serverPlatform, maxRam));
		} finally {
			lock.writeLock().unlock();
		}
		sendUpdateServerPacket(serverId, UpdateServerAction.CREATE, null);
	}

	@Override
	public void deleteServer(UUID serverId) {
		final MongoDatabase database = IltisCloud.getAPI().getDatabase();
		final MongoCollection<Document> serverCollection = database.getMongoDatabase().getCollection("servers");
		serverCollection.deleteOne(new Document("serverId", serverId.toString()));
		lock.writeLock().lock();
		try {
			servers.remove(serverId);
		} finally {
			lock.writeLock().unlock();
		}
		sendUpdateServerPacket(serverId, UpdateServerAction.DELETE, null);
	}
	
	private IServer getServerFromDB(UUID serverId) {
		final Document doc = serverCollection.find(Filters.eq(SERVER_ID_FIELD, serverId.toString())).first();
		final IServer server = new Server(
				UUID.fromString(doc.getString(SERVER_ID_FIELD)),
				SocketAddressConverter.convertToInet(doc.getString(ADDRESS_FIELD)),
				doc.getString(NAME_FIELD),
				doc.getBoolean(IS_RUNNING_FIELD),
				UUID.fromString(doc.getString(WRAPPER_ID_FIELD)),
				ServerType.valueOf(doc.getString(SERVER_TYPE_FIELD)),
				ServerPlatform.valueOf(doc.getString(SERVER_PLATFORM_FIELD)),
				doc.getInteger(MAX_RAM_FIELD));
		return server;
	}
}