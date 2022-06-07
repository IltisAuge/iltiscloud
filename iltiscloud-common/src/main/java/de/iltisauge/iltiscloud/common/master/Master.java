package de.iltisauge.iltiscloud.common.master;

import java.net.SocketAddress;
import java.util.Date;
import java.util.UUID;

import de.iltisauge.iltiscloud.api.master.IMaster;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket;
import de.iltisauge.iltiscloud.api.transport.UpdateStartablePacket.ConfirmationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Master implements IMaster {

	private final UUID masterId;
	private final SocketAddress address;
	@Setter
	private boolean isRunning;
	private final int maxRam;
	@Setter
	private Date startedUpAt;
	
//	public Master(UUID masterId, SocketAddress address, boolean isRunning, int maxRam) {
//		this.masterId = masterId;
//		this.address = address;
//		this.isRunning = isRunning;
//		this.maxRam = maxRam;
//	}

	@Override
	public void startUp() {
		sendAction(Action.START_UP);
	}

	@Override
	public void shutDown() {
		sendAction(Action.SHUT_DOWN);
	}

	@Override
	public void kill() {
		sendAction(Action.KILL);
	}
	
	private final void sendAction(Action action) {
		System.out.println("Send action " + action + " to CloudMaster");
		new UpdateStartablePacket(Type.MASTER, masterId, action, ConfirmationType.UNCONFIRMED).send();
	}
}
