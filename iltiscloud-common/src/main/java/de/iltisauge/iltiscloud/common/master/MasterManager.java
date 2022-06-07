package de.iltisauge.iltiscloud.common.master;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import de.iltisauge.databaseapi.databases.MongoDatabase;
import de.iltisauge.iltiscloud.api.Startable.Action;
import de.iltisauge.iltiscloud.api.Startable.Type;
import de.iltisauge.iltiscloud.api.master.IMaster;
import de.iltisauge.iltiscloud.api.master.IMasterManager;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket.ConfirmationType;
import de.iltisauge.transport.Transport;
import de.iltisauge.transport.network.IMessageEvent;
import lombok.Getter;

public class MasterManager implements IMasterManager {
	
	private IMessageEvent<UpdateStartablePacket> messageEvent;
	private final UUID masterId;
	private IMaster master;
	private MongoDatabase database;
	@Getter
	private MongoCollection<?> masterCollection;
	public static final String MASTER_COLLECTION = "master";
	public static final String MASTER_ID_FIELD = "masterId";
	public static final String HOSTNAME_FIELD = "hostname";
	public static final String PORT_FIELD = "port";
	public static final String STARTED_UP_AT_FIELD = "startedUpAt";
	public static final String MAX_RAM_FIELD = "maxRam";
	
	public MasterManager(UUID masterId, MongoDatabase database) {
		this.masterId = masterId;
		this.database = database;
	}
	
	@Override
	public void initialize() {
		messageEvent = new IMessageEvent<UpdateStartablePacket>() {
		
			@Override
			public void onReceived(UpdateStartablePacket message) {
				if (!message.getStartableType().equals(Type.MASTER) || message.getConfirmationType().equals(ConfirmationType.UNCONFIRMED)) {
					return;
				}
				// Packet comes from the master and its confirmed, that the action has taken place.
				final Action action = message.getStartableAction();
				if (action.equals(Action.START_UP)) {
					getMaster().setRunning(true);
				} else if (action.equals(Action.SHUT_DOWN) || action.equals(Action.KILL)) {
					getMaster().setRunning(false);
				}
			}
		};
		Transport.getNetworkManager().registerEvent(UpdateStartablePacket.class, messageEvent);
	}
	
	@Override
	public void initializeDatabase() {
		masterCollection = database.getMongoDatabase().getCollection("master");
	}
	
	@Override
	public IMaster getMaster() {
		if (master == null) {
			System.out.println("Loading master " + masterId.toString() + "...");
			final Document document = (Document) masterCollection.find(Filters.eq("masterId", masterId.toString())).first();
			System.out.println("Document=" + document);
			if (document == null) {
				return null;
			}
			final SocketAddress address = new InetSocketAddress(document.getString(HOSTNAME_FIELD), document.getInteger(PORT_FIELD));
			final Date startedUpAt = new Date(document.getLong(STARTED_UP_AT_FIELD));
			final int maxRam = document.getInteger(MAX_RAM_FIELD);
			master = new Master(masterId, address, startedUpAt != null, maxRam, startedUpAt);
		}
		return master;
	}
	
	@Override
	public void destroy() {
		Transport.getNetworkManager().unregisterEvent(messageEvent);
	}
}
